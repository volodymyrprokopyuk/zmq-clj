(ns zmq-clj.hwserver
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ server (doto (zmq/socket context :rep)
                          (zmq/bind "tcp://*:5555")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (-> server zmq/receive-str println)
        (Thread/sleep 1000)
        (-> server (zmq/send-str "World"))))))
