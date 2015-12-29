(ns zmq-clj.psenvpub
  (:require [ zeromq.zmq :as zmq ]
    [ zmq-clj.zmq-utils :as utils ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ publisher (-> context (zmq/socket :pub)
                             (zmq/bind "tcp://*:5563")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (-> publisher (utils/send-all-str "A key" "A data"))
        (-> publisher (utils/send-all-str "B key" "B data"))
        (Thread/sleep 500)))))
