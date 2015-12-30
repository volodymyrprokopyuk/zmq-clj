(ns zmq-clj.spqueue
  (:require [ zeromq.zmq :as zmq ]
    [ zmq-clj.zmq-utils :as utils ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (-> context (zmq/socket :router)
                            (zmq/bind "tcp://*:5555")) ; for clients
                 backend (-> context (zmq/socket :router)
                           (zmq/bind "tcp://*:5556")) ] ; for workers
      (let [ ready-workers (atom clojure.lang.PersistentQueue/EMPTY) ]
        (while (not (.. Thread currentThread isInterrupted))
          (let [ has-ready-workers? (-> @ready-workers empty? not)
                 poller (-> context (zmq/poller (if has-ready-workers? 2 1))) ]
            (-> poller (zmq/register backend :pollin)) ; always poll backend
            (when has-ready-workers? ; poll frontend when has ready workers
              (-> poller (zmq/register frontend :pollin)))
            (-> poller (zmq/poll -1)) ; intinite poll timeout
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
              (let [ [ client-id _ request ] (-> frontend utils/receive-all-str)
                     worker-id (peek @ready-workers) ] ; Last Used worker
                (swap! ready-workers pop)
                (println client-id)
                (-> backend (utils/send-all-str
                              worker-id _ client-id _ request))))))))))
