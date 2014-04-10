(ns dat00.util)

(defn substring? [sub st]
  (if (not= (.indexOf st sub) -1)
    true
    false))

(defn eval-java-method [v]
  "v is a keyword and o the java object"
   (list (symbol (str "." (name v)))))

(comment (eval-java-method :getClass ))
