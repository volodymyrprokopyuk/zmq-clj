(ns zmq-clj.taskwork2
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1)
         poller (-> context (zmq/poller 2))
         continue (atom true) ]
    (with-open [ consumer (-> context (zmq/socket :pull)
                            (zmq/connect "tcp://127.0.0.1:5557"))
                 sender (-> context (zmq/socket :push)
                          (zmq/connect "tcp://127.0.0.1:5558"))
                 subscriber (-> context (zmq/socket :sub) ; shutdown worker
                              (zmq/connect "tcp://127.0.0.1:5559")
                              (zmq/subscribe "")) ] ; subscribe to everything
      (let [ consumer-index (-> poller (zmq/register consumer :pollin))
             subscriber-index (-> poller (zmq/register subscriber :pollin)) ]
        (while @continue
          (-> poller zmq/poll)
          (when (-> poller (zmq/check-poller consumer-index :pollin))
            (let [ task (-> consumer zmq/receive-str) ]
              (println task)
              (Thread/sleep 1000)
              (-> sender (zmq/send-str (format "Done %s" task)))))
          (when (-> poller (zmq/check-poller subscriber-index :pollin))
            (-> subscriber zmq/receive-str println)
            (reset! continue false))))))) ; shutdown worker
