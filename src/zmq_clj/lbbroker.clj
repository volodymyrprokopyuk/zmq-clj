(ns zmq-clj.lbbroker
  (:require [ zeromq.zmq :as zmq ]
    [ zmq-clj.zmq-utils :as utils ])
  (:gen-class))

(defn client [ client-id ]
  (let [ context (zmq/context 1) ]
    (with-open [ client (-> context (zmq/socket :req)
                          (zmq/set-identity (-> client-id str .getBytes)) ; for ROUTER
                          (zmq/connect "tcp://127.0.0.1:5672")) ] ; to frontend
      (dotimes [ _ 4 ]
        (-> client (zmq/send-str (format "Hello %s" client-id)))
        (let [ reply (-> client zmq/receive-str) ]
          (println (format "Client %s: %s" client-id reply)))))))

(defn worker [ worker-id ]
  (let [ context (zmq/context 1) ]
    (with-open [ worker (-> context (zmq/socket :req)
                          (zmq/set-identity (-> worker-id str .getBytes)) ; for ROUTER
                          (zmq/connect "tcp://127.0.0.1:5673")) ] ; to backend
      (-> worker (zmq/send-str (format "Ready %s" worker-id)))
      (while (not (.. Thread currentThread isInterrupted))
        (let [ [ client-id _ request ] (-> worker utils/receive-all-str) ]
          (println (format "Worker %s: %s" worker-id request))
          (Thread/sleep 1000)
          (-> worker (utils/send-all-str
                       client-id "" (format "Done %s" request))))))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (-> context (zmq/socket :router)
                            (zmq/bind "tcp://*:5672"))
                 backend (-> context (zmq/socket :router)
                           (zmq/bind "tcp://*:5673")) ]
      (dotimes [ client-id 2 ]
        (-> (partial client client-id) Thread. .start))
      (dotimes [ worker-id 3 ]
        (-> (partial worker worker-id) Thread. .start))
      (let [ ready-workers (atom clojure.lang.PersistentQueue/EMPTY) ]
        (while (not (.. Thread currentThread isInterrupted))
          (let [ has-ready-workers? (-> @ready-workers empty? not)
                 poller (-> context (zmq/poller (if has-ready-workers? 2 1))) ]
            (-> poller (zmq/register backend :pollin)) ; always poll backend
            (when has-ready-workers? ; poll frontend when has ready workers
              (-> poller (zmq/register frontend :pollin)))
            (-> poller (zmq/poll -1)) ; infinite poll timeout
            ; backend -> frontend
            (when (-> poller (zmq/check-poller 0 :pollin))
              (let [ [ worker-id _ client-id _ reply ]
                     (-> backend utils/receive-all-str) ]
                (swap! ready-workers conj worker-id)
                (if-not (re-find #"Ready" client-id)
                  (-> frontend (utils/send-all-str client-id "" reply)))))
            ; frontend -> backend
            (when (and has-ready-workers?
                    (-> poller (zmq/check-poller 1 :pollin)))
              (let [ [ client-id _ request ]
                     (-> frontend utils/receive-all-str)
                     worker-id (peek @ready-workers) ] ; Last Used worker
                (swap! ready-workers pop)
                (-> backend (utils/send-all-str
                              worker-id "" client-id "" request))))))))))
