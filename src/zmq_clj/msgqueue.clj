(ns zmq-clj.msgqueue
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (doto (zmq/socket context :router) ; fair queueing
                            (zmq/bind "tcp://*:5559"))
                 backend (doto (zmq/socket context :dealer) ; load balancing
                           (zmq/bind "tcp://*:5560")) ]
      (device/proxy context frontend backend))))
