(ns zmq-clj.zmq-utils
  (:require [ zeromq.zmq :as zmq ]))

(defn send-all-str [ socket frame1 frame2 & frames ]
  (let [ all-frames (apply vector frame1 frame2 frames)
         [ head-frames [ last-frame ] ]
         (partition-all (-> all-frames count dec) all-frames) ]
    (doseq [ frame head-frames ]
      (-> socket (zmq/send-str frame zmq/send-more)))
    (-> socket (zmq/send-str last-frame))))

(defn receive-all-str [ socket ]
  (loop [ frames (transient [ ]) ]
    (let [ new-frames (conj! frames (-> socket zmq/receive-str)) ]
      (if (-> socket zmq/receive-more?)
        (recur new-frames)
        (persistent! new-frames)))))
