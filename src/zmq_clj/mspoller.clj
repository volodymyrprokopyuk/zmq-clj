(ns zmq-clj.mspoller
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1)
         poller (-> context (zmq/poller 2)) ] ; in/out sockets multiplexer
    (with-open [ subscriber (-> context (zmq/socket :sub) ; to wuserver.clj
                              (zmq/connect "tcp://127.0.0.1:5556")
                              (zmq/subscribe "28921") ; set subscriptions
                              (zmq/subscribe "28042"))
                 consumer (-> context (zmq/socket :pull) ; for taskvent.clj
                            (zmq/connect "tcp://127.0.0.1:5557")) ]
      (let [ subscriber-index (-> poller (zmq/register subscriber :pollin))
             consumer-index (-> poller (zmq/register consumer :pollin)) ]
        (while (not (.. Thread currentThread isInterrupted))
          (-> poller zmq/poll)
          (when (-> poller (zmq/check-poller subscriber-index :pollin))
            (-> subscriber zmq/receive-str println))
          (when (-> poller (zmq/check-poller consumer-index :pollin))
            (-> consumer zmq/receive-str println)))))))
