(ns zmq-clj.lpclient
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn request [ context ]
  (with-open [ client (-> context (zmq/socket :req)
              (zmq/connect "tcp://127.0.0.1:5555")) ]
    (-> client (zmq/send-str "Hello"))
    (let [ poller (-> context (zmq/poller 1))
           client-index (-> poller (zmq/register client :pollin)) ]
      (-> poller (zmq/poll 2500)) ; request timeout
      (when (-> poller (zmq/check-poller client-index :pollin))
        (-> client zmq/receive-str)))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (dotimes [ _ 5 ]
      (loop [ attempts 0 ]
        (if-let [ reply (request context) ]
          (println reply)
          (if (< attempts 3)
            (do
              (println "Request timeout. Retrying...")
              (recur (inc attempts)))
            (println "Request timeout. Aborted")))))))
