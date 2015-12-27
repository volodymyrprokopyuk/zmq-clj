(ns zmq-clj.tasksink
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ receiver (-> context (zmq/socket :pull) ; fair queueing
                            (zmq/bind "tcp://*:5558")) ]
      (-> receiver zmq/receive-str println) ; wait for batch synchronization
      (let [ start (System/currentTimeMillis) ]
        (dotimes [ _ 10 ]
          (-> receiver zmq/receive-str println))
        (println "Total elapsed time:"
          (- (System/currentTimeMillis) start))))))
