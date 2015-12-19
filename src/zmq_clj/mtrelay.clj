(ns zmq-clj.mtrelay
  (:require [ zeromq.zmq :as zmq ])
  (:gen-class))

(defn step1 [ context ]
  (with-open [ emitter2 (doto (zmq/socket context :pair)
                          (zmq/connect "inproc://step2")) ]
    (println "Step 1")
    (-> emitter2 (zmq/send-str "Step 2"))))

(defn step2 [ context ]
  (with-open [ receiver2 (doto (zmq/socket context :pair)
                           (zmq/bind "inproc://step2"))
               emitter3 (doto (zmq/socket context :pair)
                          (zmq/connect "inproc://step3")) ]
    (-> (partial step1 context) Thread. .start)
    (-> receiver2 zmq/receive-str println)
    (-> emitter3 (zmq/send-str "Step 3"))))

(defn -main [ ]
  (let [ context (zmq/context 1) ]
    (with-open [ receiver3 (doto (zmq/socket context :pair)
                            (zmq/bind "inproc://step3")) ]
      (-> (partial step2 context) Thread. .start)
      (-> receiver3 zmq/receive-str println))))
