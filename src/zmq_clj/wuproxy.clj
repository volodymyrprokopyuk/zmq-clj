(ns zmq-clj.wuproxy
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (-> context (zmq/socket :xsub)
                            (zmq/connect "tcp://127.0.0.1:5556")) ; to wuserver.clj
                 backend (-> context (zmq/socket :xpub)
                           (zmq/bind "tcp://127.0.0.1:5557")) ] ; for wuclient.clj
      (-> context (device/proxy frontend backend)))))
