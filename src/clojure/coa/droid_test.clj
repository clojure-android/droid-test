(ns coa.droid-test
  (:import android.content.Context))

(defn unstub [classname]
  (Class/forName (.getName classname) true (.getClassLoader Context)))
