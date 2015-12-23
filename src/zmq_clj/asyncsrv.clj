(ns zmq-clj.asyncsrv
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn client [ client-id ]
  (let [ context (zmq/context 1)
         poller (zmq/poller context 1) ]
    (with-open [ client (doto (zmq/socket context :dealer)
                          (zmq/set-identity (-> client-id str .getBytes))
                          (zmq/connect "tcp://127.0.0.1:5670")) ]
      (zmq/register poller client :pollin)
      (dotimes [ i 5 ]
        (dotimes [ _ 10 ]
          (zmq/poll poller 100) ; poll for one second
          (when (zmq/check-poller poller 0 :pollin)
            (let [ reply (-> client zmq/receive-str) ]
              (println (format "Client %s: %s" client-id reply)))))
        (-> client (zmq/send-str ; send request every second
                     (format "Client %s, Request %s" client-id i)))))))

(defn worker [ context ]
  (with-open [ worker (doto (zmq/socket context :dealer)
                        (zmq/connect "tcp://127.0.0.1:5671")) ]
    (while (not (.. Thread currentThread isInterrupted))
      (let [ client-id (-> worker zmq/receive-str)
             request (-> worker zmq/receive-str) ]
        (Thread/sleep 1000)
        (-> worker (zmq/send-str client-id zmq/send-more))
        (-> worker (zmq/send-str (format "Done %s" request)))))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (doto (zmq/socket context :router)
                            (zmq/bind "tcp://*:5670"))
                 backend (doto (zmq/socket context :dealer)
                           (zmq/bind "tcp://*:5671")) ]
      (dotimes [ i 3 ]
        (-> (partial client i) Thread. .start))
      (dotimes [ _ 5 ]
        (-> (partial worker context) Thread. .start))
      (device/proxy context frontend backend))))
