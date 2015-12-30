(ns zmq-clj.asyncsrv
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ]
    [ zmq-clj.zmq-utils :as utils ])
  (:gen-class))

(defn client [ client-id ]
  (let [ context (zmq/context 1)
         poller (-> context (zmq/poller 1)) ]
    (with-open [ client (-> context (zmq/socket :dealer)
                          (zmq/set-identity (-> client-id str .getBytes)) ; for ROUTER
                          (zmq/connect "tcp://127.0.0.1:5670")) ] ; to frontend
      (let [ client-index (-> poller (zmq/register client :pollin)) ]
        (dotimes [ request-id 5 ]
          (-> client (zmq/send-str ; send request every second
                       (format "Client %s, Request %s" client-id request-id)))
          (dotimes [ _ 10 ] ; poll for one second (10 x 100)
            (-> poller (zmq/poll 100)) ; poll timeout
            (when (-> poller (zmq/check-poller client-index :pollin))
              (let [ reply (-> client zmq/receive-str) ]
                (println (format "Client %s: %s" client-id reply))))))))))

(defn worker [ context ]
  (with-open [ worker (-> context (zmq/socket :dealer)
                        (zmq/connect "tcp://127.0.0.1:5671")) ]
    (while (not (.. Thread currentThread isInterrupted))
      (let [ [ client-id request ] (-> worker utils/receive-all-str) ]
        (Thread/sleep 100)
        (-> worker (utils/send-all-str client-id (format "Done %s" request)))))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (-> context (zmq/socket :router)
                            (zmq/bind "tcp://*:5670"))
                 backend (-> context (zmq/socket :dealer)
                           (zmq/bind "tcp://*:5671")) ]
      (dotimes [ client-id 3 ]
        (-> (partial client client-id) Thread. .start))
      (dotimes [ _ 5 ]
        (-> (partial worker context) Thread. .start))
      (-> context (device/proxy frontend backend)))))
