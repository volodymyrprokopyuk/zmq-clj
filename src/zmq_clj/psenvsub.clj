(ns zmq-clj.psenvsub
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ subscriber (doto (zmq/socket context :sub)
                              (zmq/connect "tcp://127.0.0.1:5563")
                              (zmq/subscribe "B")) ]
      (dotimes [ i 5 ]
        (-> subscriber zmq/receive-str) ; message key
        (-> subscriber zmq/receive-str println))))) ; message data
