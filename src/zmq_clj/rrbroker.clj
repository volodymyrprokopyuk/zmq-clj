(ns zmq-clj.rrbroker
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1)
         poller (zmq/poller context 2) ]
    (with-open [ frontend (doto (zmq/socket context :router) ; fair queueing
                            (zmq/bind "tcp://*:5559"))
                 backend (doto (zmq/socket context :dealer) ; load balancing
                           (zmq/bind "tcp://*:5560")) ]
      (zmq/register poller frontend :pollin)
      (zmq/register poller backend :pollin)
      (while (not (.. Thread currentThread isInterrupted))
        (zmq/poll poller)
        (when (zmq/check-poller poller 0 :pollin)
          (loop [ part (zmq/receive frontend) ]
            (let [ more? (zmq/receive-more? frontend) ]
              (zmq/send backend part (if more? zmq/send-more 0))
              (when more?
                (recur (zmq/receive frontend))))))
        (when (zmq/check-poller poller 1 :pollin)
          (loop [ part (zmq/receive backend) ]
            (let [ more? (zmq/receive-more? backend) ]
              (zmq/send frontend part (if more? zmq/send-more 0))
              (when more?
                (recur (zmq/receive backend))))))))))
