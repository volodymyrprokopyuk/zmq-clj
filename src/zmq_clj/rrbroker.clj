(ns zmq-clj.rrbroker
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1)
         poller (-> context (zmq/poller 2)) ]
    (with-open [ frontend (-> context (zmq/socket :router) ; fair queueing
                            (zmq/bind "tcp://*:5559")) ; async server
                 backend (-> context (zmq/socket :dealer) ; distributed load balancing
                           (zmq/bind "tcp://*:5560")) ] ; async client
      (let [ frontend-index (-> poller (zmq/register frontend :pollin))
             backend-index (-> poller (zmq/register backend :pollin)) ]
        (while (not (.. Thread currentThread isInterrupted))
          (-> poller zmq/poll)
          ; frontend -> backend
          (when (-> poller (zmq/check-poller frontend-index :pollin))
            (loop [ part (-> frontend zmq/receive) ]
              (let [ more? (-> frontend zmq/receive-more?) ]
                (-> backend (zmq/send part (if more? zmq/send-more 0)))
                (when more?
                  (recur (-> frontend zmq/receive))))))
          ; backend -> frontend
          (when (-> poller (zmq/check-poller backend-index :pollin))
            (loop [ part (-> backend zmq/receive) ]
              (let [ more? (-> backend zmq/receive-more?) ]
                (-> frontend (zmq/send part (if more? zmq/send-more 0)))
                (when more?
                  (recur (-> backend zmq/receive)))))))))))
