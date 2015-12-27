(ns zmq-clj.wuserver
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ publisher (-> context (zmq/socket :pub)
                             (zmq/bind "tcp://*:5556")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (let [ zip (rand-int 100000)
               temp (- (rand-int 70) 30)
               hum (rand-int 101) ]
          (-> publisher (zmq/send-str (format "%s %s %s" zip temp hum))))))))
