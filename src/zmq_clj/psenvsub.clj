(ns zmq-clj.psenvsub
  (:require [ zeromq.zmq :as zmq ]
    [ zmq-clj.zmq-utils :as utils ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ subscriber (-> context (zmq/socket :sub)
                              (zmq/connect "tcp://127.0.0.1:5563")
                              (zmq/subscribe "B")) ]
      (dotimes [ _ 5 ]
        (let [ [ key data ] (-> subscriber (utils/receive-all-str)) ]
          (println key data))))))
