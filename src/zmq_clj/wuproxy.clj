(ns zmq-clj.wuproxy
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (doto (zmq/socket context :xsub)
                            (zmq/connect "tcp://127.0.0.1:5556"))
                 backend (doto (zmq/socket context :xpub)
                           (zmq/bind "tcp://127.0.0.1:5557")) ]
      (device/proxy context frontend backend))))
