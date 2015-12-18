(ns zmq-clj.wuclient
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ subscriber (doto (zmq/socket context :sub)
                              ;(zmq/connect "tcp://127.0.0.1:5556")
                              (zmq/connect "tcp://127.0.0.1:5557") ; with proxy
                              (zmq/subscribe "28921")
                              (zmq/subscribe "28042")) ]
      (dotimes [ i 5 ]
        (-> subscriber zmq/receive-str println)))))
