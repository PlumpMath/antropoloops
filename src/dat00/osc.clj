(ns dat00.osc
    (:import
     [oscP5 OscP5 OscMessage ]
     [netP5 NetAddress Logger]))

(declare my-remote-location my-oscP5)

(def in-port 9001)

(def out-port 9000)

(defn make-osc-message [path]
  (OscMessage. path))

(def example-message (let [m (OscMessage. "/the-message")]
                       (.add m 90)
                       (.add m "hola")
                       ))
(def example-message-st (let [m (OscMessage. "/the-message")]
                          (.add m "90")
                          (.add m 80)

  ))

(.intValue (.get example-message 0))





(defn send-osc-message [message]
  (.send my-oscP5 message my-remote-location))


(defn init-oscP5 [papplet]
  (comment
      (. OscP5 setLogStatus 5 1)
  (. OscP5 setLogStatus  1)
(. OscP5 setLogStatus 4 1)

           (. OscP5 setLogStatus 0 1)
           (. OscP5 setLogStatus 3 1)
           (. OscP5 setLogStatus 2 1)
           (. OscP5 setLogStatus 1 1))
  (def my-oscP5 (OscP5. papplet in-port))
  (def my-remote-location (NetAddress. "localhost" out-port))

 )
