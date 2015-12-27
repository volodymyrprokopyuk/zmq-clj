(ns zmq-clj.msgqueue
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (-> context (zmq/socket :router) ; fair queueing
                            (zmq/bind "tcp://*:5559")) ; async server
                 backend (-> context (zmq/socket :dealer) ; load balancing
                           (zmq/bind "tcp://*:5560")) ] ; async client
      (-> context (device/proxy frontend backend)))))
