(ns zmq-clj.lbbroker
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn client [ client-id ]
  (let [ context (zmq/context 1) ]
    (with-open [ client (doto (zmq/socket context :req)
                          (zmq/set-identity (-> client-id str .getBytes))
                          (zmq/connect "tcp://127.0.0.1:5672")) ] ; frontend
      (dotimes [ i 4 ]
        (-> client (zmq/send-str (format "Hello %s" client-id)))
        (let [ reply (-> client zmq/receive-str) ]
          (println (format "Client: %s: %s" client-id, reply)))))))

(defn worker [ worker-id ]
  (let [ context (zmq/context 1) ]
    (with-open [ worker (doto (zmq/socket context :req)
                          (zmq/set-identity (-> worker-id str .getBytes))
                          (zmq/connect "tcp://127.0.0.1:5673")) ] ; backend
      (-> worker (zmq/send-str (format "Ready %s" worker-id)))
      (while (not (.. Thread currentThread isInterrupted))
        (let [ client-id (-> worker zmq/receive-str)
               _ (-> worker zmq/receive-str)
               request (-> worker zmq/receive-str) ]
          (println (format "Worker %s: %s" worker-id request))
          (Thread/sleep 1000)
          (-> worker (zmq/send-str client-id zmq/send-more))
          (-> worker (zmq/send-str "" zmq/send-more))
          (-> worker (zmq/send-str (format "Done %s" request))))))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (doto (zmq/socket context :router)
                            (zmq/bind "tcp://*:5672"))
                 backend (doto (zmq/socket context :router)
                           (zmq/bind "tcp://*:5673")) ]
      (dotimes [ i 2 ]
        (-> (partial client i) Thread. .start))
      (dotimes [ i 3 ]
        (-> (partial worker i) Thread. .start))
      (let [ ready-workers (atom clojure.lang.PersistentQueue/EMPTY) ]
        (while (not (.. Thread currentThread isInterrupted))
          (let [ has-ready-workers? (-> @ready-workers empty? not)
                 poller (zmq/poller context (if has-ready-workers? 2 1)) ]
            (zmq/register poller backend :pollin) ; always poll backend
            (when has-ready-workers? ; poll frontend when has ready workers
              (zmq/register poller frontend :pollin))
            (zmq/poll poller -1) ; infinite poll timeout
            ; backend -> frontend
            (when (zmq/check-poller poller 0 :pollin)
              (let [ worker-id (-> backend zmq/receive-str)
                     _ (-> backend zmq/receive-str)
                     client-id (-> backend zmq/receive-str) ]
                (swap! ready-workers conj worker-id)
                (if-not (re-find #"Ready" client-id)
                  (let [ _ (-> backend zmq/receive-str)
                         reply (-> backend zmq/receive-str) ]
                    (-> frontend (zmq/send-str client-id zmq/send-more))
                    (-> frontend (zmq/send-str "" zmq/send-more))
                    (-> frontend (zmq/send-str reply))))))
            ; frontend -> backend
            (when (and has-ready-workers?
                    (zmq/check-poller poller 1 :pollin))
              (let [ client-id (-> frontend zmq/receive-str)
                     _ (-> frontend zmq/receive-str)
                     request (-> frontend zmq/receive-str)
                     worker-id (peek @ready-workers) ]
                (swap! ready-workers pop)
                (-> backend (zmq/send-str worker-id zmq/send-more))
                (-> backend (zmq/send-str "" zmq/send-more))
                (-> backend (zmq/send-str client-id zmq/send-more))
                (-> backend (zmq/send-str "" zmq/send-more))
                (-> backend (zmq/send-str request))))))))))
