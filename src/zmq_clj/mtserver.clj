(ns zmq-clj.mtserver
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn worker [ i context ]
  (with-open [ server (doto (zmq/socket context :rep)
                        (zmq/connect "inproc://workers")) ]
    (while (not (.. Thread currentThread isInterrupted))
      (-> server zmq/receive-str println)
      (Thread/sleep 1000)
      (-> server (zmq/send-str (format "World %s" i))))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ clients (doto (zmq/socket context :router)
                            (zmq/bind "tcp://*:5555"))
                 workers (doto (zmq/socket context :dealer)
                           (zmq/bind "inproc://workers")) ]
      (dotimes [ i 5 ]
        (-> (partial worker i context) Thread. .start))
      (device/proxy context clients workers))))
