(ns zmq-clj.taskvent
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ producer (doto (zmq/socket context :push) ; distributed load-balancing
                            (zmq/bind "tcp://*:5557"))
                 start (doto (zmq/socket context :push)
                         (zmq/connect "tcp://127.0.0.1:5558")) ]
      (-> start (zmq/send-str "Start")) ; synchronize batch
      (dotimes [ i 10 ]
        (let [ task (format "Task %s" i) ]
          (println task)
          (Thread/sleep 100)
          (-> producer (zmq/send-str task)))))))
