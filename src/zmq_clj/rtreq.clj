(ns zmq-clj.rtreq
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn worker [ worker-id ]
  (let [ context (zmq/context 1) ]
    (with-open [ worker (doto (zmq/socket context :req)
                          (zmq/set-identity (-> worker-id str .getBytes))
                          (zmq/connect "tcp://127.0.0.1:5671")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (-> worker (zmq/send-str (format "Ready %s" worker-id)))
        (-> worker zmq/receive-str println)
        (Thread/sleep 1000)))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ broker (doto (zmq/socket context :router)
                          (zmq/bind "tcp://*:5671")) ]
      (dotimes [ i 3 ]
        (-> (partial worker i) Thread. .start))
      (while (not (.. Thread currentThread isInterrupted))
        (let [ worker-id (-> broker zmq/receive-str) ; LRU worker
               _ (-> broker zmq/receive-str)
               _ (-> broker zmq/receive-str println) ]
          (-> broker (zmq/send-str worker-id zmq/send-more))
          (-> broker (zmq/send-str "" zmq/send-more))
          (-> broker (zmq/send-str (format "Workload %s" worker-id))))))))
