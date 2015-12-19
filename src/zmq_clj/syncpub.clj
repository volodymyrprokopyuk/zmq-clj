(ns zmq-clj.syncpub
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ publisher (doto (zmq/socket context :pub)
                             (zmq/bind "tcp://*:5561"))
                 syncserver (doto (zmq/socket context :rep)
                              (zmq/bind "tcp://*:5562")) ]
      (dotimes [ i 2 ] ; expected subscribers
        (-> syncserver zmq/receive-str println)
        (-> syncserver (zmq/send-str "synced")))
      (while (not (.. Thread currentThread isInterrupted))
        (let [ zip (rand-int 100000)
               temp (- (rand-int 70) 30)
               hum (rand-int 101)
               message (format "%s %s %s" zip temp hum) ]
          (-> publisher (zmq/send-str message)))))))
