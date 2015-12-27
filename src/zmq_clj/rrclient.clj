(ns zmq-clj.rrclient
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ client (-> context (zmq/socket :req) ; sync client
                          (zmq/connect "tcp://127.0.0.1:5559")) ] ; connect to ROUTER
      (dotimes [ _ 5 ]
        (-> client (zmq/send-str "Hello"))
        (-> client zmq/receive-str println)))))
