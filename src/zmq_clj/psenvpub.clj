(ns zmq-clj.psenvpub
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ publisher (doto (zmq/socket context :pub)
                             (zmq/bind "tcp://*:5563")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (-> publisher (zmq/send-str "A" zmq/send-more)) ; message key
        (-> publisher (zmq/send-str "A data")) ; message data
        (-> publisher (zmq/send-str "B" zmq/send-more))
        (-> publisher (zmq/send-str "B data"))
        (Thread/sleep 500)))))
