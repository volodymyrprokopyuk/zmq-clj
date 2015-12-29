(ns zmq-clj.syncpub
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ publisher (-> context (zmq/socket :pub)
                             (zmq/bind "tcp://*:5561"))
                 syncserver (-> context (zmq/socket :rep)
                              (zmq/bind "tcp://*:5562")) ]
      (dotimes [ _ 2 ] ; expected subscribers
        (-> syncserver zmq/receive-str println) ; wait for next subscriber
        (-> syncserver (zmq/send-str "Synchronized")))
      (while (not (.. Thread currentThread isInterrupted))
        (let [ zip (rand-int 100000)
               temp (- (rand-int 70) 30)
               hum (rand-int 101) ]
          (-> publisher (zmq/send-str (format "%s %s %s" zip temp hum))))))))
