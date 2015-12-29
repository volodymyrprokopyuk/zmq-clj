(ns zmq-clj.mtserver
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn worker [ worker-id context ]
  (with-open [ server (-> context (zmq/socket :rep) ; sync server
                        (zmq/connect "inproc://workers")) ]
    (while (not (.. Thread currentThread isInterrupted))
      (-> server zmq/receive-str println)
      (Thread/sleep 1000)
      (-> server (zmq/send-str (format "World %s" worker-id))))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ clients (-> context (zmq/socket :router) ; fair queueing
                            (zmq/bind "tcp://*:5555"))
                 workers (-> context (zmq/socket :dealer) ; distributed load balancing
                           (zmq/bind "inproc://workers")) ] ; INPROC
      (dotimes [ worker-id 5 ]
        (-> (partial worker worker-id context) Thread. .start))
      (-> context (device/proxy clients workers)))))
