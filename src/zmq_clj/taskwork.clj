(ns zmq-clj.taskwork
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ consumer (doto (zmq/socket context :pull)
                            (zmq/connect "tcp://127.0.0.1:5557"))
                 sender (doto (zmq/socket context :push)
                          (zmq/connect "tcp://127.0.0.1:5558")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (let [ task (-> consumer zmq/receive-str) ]
          (println task)
          (Thread/sleep 1000)
          (-> sender (zmq/send-str (format "Done %s" task))))))))
