(ns zmq-clj.rtdealer
  (:require [ zeromq.zmq :as zmq ]
    [ zmq-clj.zmq-utils :as utils ])
  (:gen-class))

(defn worker [ worker-id ]
  (let [ context (zmq/context 1) ]
    (with-open [ worker (-> context (zmq/socket :dealer)
                          (zmq/set-identity (-> worker-id str .getBytes)) ; for ROUTER
                          (zmq/connect "tcp://127.0.0.1:5671")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (-> worker (zmq/send-str (format "Ready %s" worker-id)))
        (let [ workload (-> worker zmq/receive-str) ]
          (println (format "Worker %s: %s" worker-id workload)))
        (Thread/sleep 1000)))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ broker (-> context (zmq/socket :router)
                          (zmq/bind "tcp://*:5671")) ]
      (dotimes [ worker-id 3 ]
        (-> (partial worker worker-id) Thread. .start))
      (while (not (.. Thread currentThread isInterrupted))
        (let [ [ worker-id ready ] (-> broker utils/receive-all-str) ]
          (println ready) ; LRU worker
          (-> broker (utils/send-all-str
                       worker-id (format "Workload %s" worker-id))))))))
