(ns zmq-clj.syncsub
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ subscriber (-> context (zmq/socket :sub)
                              (zmq/connect "tcp://127.0.0.1:5561")
                              (zmq/subscribe "28921")
                              (zmq/subscribe "28042"))
                 syncclient (-> context (zmq/socket :req)
                              (zmq/connect "tcp://127.0.0.1:5562")) ]
      (-> syncclient (zmq/send-str "Synchronize"))
      (-> syncclient zmq/receive-str println) ; wait for server
      (dotimes [ _ 5 ]
        (-> subscriber zmq/receive-str println)))))
