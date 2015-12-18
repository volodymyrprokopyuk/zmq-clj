(ns zmq-clj.taskwork2
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1)
         poller (zmq/poller context 2)
         continue (atom true) ]
    (with-open [ consumer (doto (zmq/socket context :pull)
                            (zmq/connect "tcp://127.0.0.1:5557"))
                 sender (doto (zmq/socket context :push)
                          (zmq/connect "tcp://127.0.0.1:5558"))
                 subscriber (doto (zmq/socket context :sub) ; shutdown worker
                              (zmq/connect "tcp://127.0.0.1:5559")
                              (zmq/subscribe "")) ]
      (zmq/register poller consumer :pollin)
      (zmq/register poller subscriber :pollin)
      (while @continue
        (zmq/poll poller)
        (when (zmq/check-poller poller 0 :pollin)
          (let [ task (-> consumer zmq/receive-str) ]
            (println task)
            (Thread/sleep 1000)
            (-> sender (zmq/send-str (format "Done %s" task)))))
        (when (zmq/check-poller poller 1 :pollin)
          (-> subscriber zmq/receive-str println)
          (reset! continue false)))))) ; shutdown worker
