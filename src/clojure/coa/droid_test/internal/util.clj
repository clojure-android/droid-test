(ns coa.droid-test.internal.util
  (:require [clojure.java.io :as io])
  (:import java.io.PushbackReader))

(defn- ns-name-to-file [^String ns-name]
  (str (.. ns-name (replace "-" "_") (replace "." "/")) ".clj"))

(defn extract-imports [loader namespace]
  (->> (ns-name-to-file namespace)
       identity
       (.getResourceAsStream loader)
       io/reader
       PushbackReader.
       read
       (filter #(and (sequential? %) (= (first %) :import)))
       (mapcat rest)
       (map #(if (sequential? %)
               (map (partial str (first %) ".") (rest %))
               (str %)))
       flatten
       (filter #(.startsWith ^String % "android."))))

