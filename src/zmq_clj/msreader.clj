(ns zmq-clj.msreader
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ subscriber (-> context (zmq/socket :sub) ; to wuserver.clj
                              (zmq/connect "tcp://127.0.0.1:5556")
                              (zmq/subscribe "28921") ; set subscriptions
                              (zmq/subscribe "28042"))
                 consumer (-> context (zmq/socket :pull) ; of taskvent.clj
                            (zmq/connect "tcp://127.0.0.1:5557")) ]
      (while (not (.. Thread currentThread isInterrupted))
        (loop [ ]
          (when-let [ weather (-> subscriber (zmq/receive-str zmq/dont-wait)) ]
            (println weather)
            (recur)))
        (loop [ ]
          (when-let [ task (-> consumer (zmq/receive-str zmq/dont-wait)) ]
            (println task)
            (recur)))
        (Thread/sleep 10)))))
