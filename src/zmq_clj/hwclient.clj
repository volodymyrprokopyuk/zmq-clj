(ns zmq-clj.hwclient
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ client (doto (zmq/socket context :req) ; sync client
                          (zmq/connect "tcp://127.0.0.1:5555")) ]
      (dotimes [ _ 5 ]
        (-> client (zmq/send-str "Hello")) ; REQ initiates communication
        (-> client zmq/receive-str println)))))
