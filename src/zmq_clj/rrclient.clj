(ns zmq-clj.rrclient
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ client (doto (zmq/socket context :req)
                          (zmq/connect "tcp://127.0.0.1:5559")) ] ; connect to ROUTER
      (-> client (zmq/send-str "Hello"))
      (-> client zmq/receive-str println))))
