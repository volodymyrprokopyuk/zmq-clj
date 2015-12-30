(ns zmq-clj.spworker
  (:require [ zeromq.zmq :as zmq ]
    [ zmq-clj.zmq-utils :as utils ])
  (:gen-class))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ worker (-> context (zmq/socket :req)
                          (zmq/set-identity (-> 1e5 rand-int str .getBytes)) ; for ROUTER
                          (zmq/connect "tcp://127.0.0.1:5556")) ] ; to backend
      (-> worker (zmq/send-str "Ready"))
      (while (not (.. Thread currentThread isInterrupted))
        (let [ [ client-id _ request ] (-> worker utils/receive-all-str) ]
          (println request)
          (Thread/sleep 1000)
          (-> worker (utils/send-all-str
                       client-id "" (format "Done %s" request))))))))
