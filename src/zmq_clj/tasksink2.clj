(ns zmq-clj.tasksink2
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ receiver (doto (zmq/socket context :pull) ; fair queueing
                            (zmq/bind "tcp://*:5558"))
                 publisher (doto (zmq/socket context :pub) ; shutdown workers
                             (zmq/bind "tcp://*:5559")) ]
      (-> receiver zmq/receive-str println) ; wait for start batch
      (let [ start (System/currentTimeMillis) ]
        (dotimes [ i 10 ]
          (-> receiver zmq/receive-str println))
        (println "Total elapsed time:"
          (- (System/currentTimeMillis) start)))
      (-> publisher (zmq/send-str "Shutdown"))))) ; shutdown workers
