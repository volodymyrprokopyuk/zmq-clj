(ns zmq-clj.espresso
  (:require [ zeromq.zmq :as zmq ]
    [ zeromq.device :as device ])
  (:gen-class))

(defn publisher [ context ]
  (with-open [ publisher (-> context (zmq/socket :pub)
                           (zmq/bind "tcp://*:6000")) ]
    (while (not (.. Thread currentThread isInterrupted))
      (-> publisher (zmq/send-str (-> 26 rand-int (+ 65) char str))) ; random char
      (Thread/sleep 100))))

(defn subscriber [ context ]
  (with-open [ subscriber (-> context (zmq/socket :sub)
                            (zmq/connect "tcp://127.0.0.1:6001")
                            (zmq/subscribe "A") ; set subscriptions
                            (zmq/subscribe "Z")) ]
    (while (not (.. Thread currentThread isInterrupted))
      (let [ message (-> subscriber zmq/receive-str) ]
        (println (format "Subscriber %s" message))))))

(defn listener [ context ]
  (with-open [ listener (-> context (zmq/socket :pair)
                          (zmq/connect "tcp://127.0.0.1:6002")) ]
    (while (not (.. Thread currentThread isInterrupted))
      (let [ message (-> listener zmq/receive-str) ]
        (println (format "Listener %s" message))))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ frontend (-> context (zmq/socket :xsub)
                            (zmq/connect "tcp://127.0.0.1:6000"))
                 backend (-> context (zmq/socket :xpub)
                           (zmq/bind "tcp://*:6001"))
                 capture (-> context (zmq/socket :pair)
                           (zmq/bind "tcp://*:6002")) ]
      (-> (partial publisher context) Thread. .start)
      (-> (partial subscriber context) Thread. .start)
      (-> (partial listener context) Thread. .start)
      (-> context (device/proxy frontend backend capture))))) ; PROXY runs in current thread
