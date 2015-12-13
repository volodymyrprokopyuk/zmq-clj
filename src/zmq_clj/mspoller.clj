(ns zmq-clj.mspoller
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1)
         poller (zmq/poller context 2) ] ; in/out sockets multiplexer
    (with-open [ subscriber (doto (zmq/socket context :sub)
                              (zmq/connect "tcp://127.0.0.1:5556")
                              (zmq/subscribe "28921")
                              (zmq/subscribe "28042"))
                 consumer (doto (zmq/socket context :pull)
                            (zmq/connect "tcp://127.0.0.1:5557")) ]
      (zmq/register poller subscriber :pollin)
      (zmq/register poller consumer :pollin)
      (while (not (.. Thread currentThread isInterrupted))
        (zmq/poll poller)
        (when (zmq/check-poller poller 0 :pollin)
          (-> subscriber zmq/receive-str println))
        (when (zmq/check-poller poller 1 :pollin)
          (-> consumer zmq/receive-str println))))))
