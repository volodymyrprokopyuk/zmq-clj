(ns zmq-clj.syncsub
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ subscriber (doto (zmq/socket context :sub)
                              (zmq/connect "tcp://127.0.0.1:5561")
                              (zmq/subscribe "28921")
                              (zmq/subscribe "28042"))
                 syncclient (doto (zmq/socket context :req)
                              (zmq/connect "tcp://127.0.0.1:5562")) ]
      (-> syncclient (zmq/send-str "sync"))
      (-> syncclient zmq/receive-str println)
      (dotimes [ i 5 ]
        (-> subscriber zmq/receive-str println)))))
