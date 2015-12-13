(ns zmq-clj.rrworker
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ server (doto (zmq/socket context :rep)
                          (zmq/connect "tcp://127.0.0.1:5560")) ] ; connect to DEALER
      (while (not (.. Thread currentThread isInterrupted))
        (-> server zmq/receive-str println)
        (Thread/sleep 1000)
        (-> server (zmq/send-str "World"))))))
