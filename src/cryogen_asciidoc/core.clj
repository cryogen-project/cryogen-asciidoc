(ns cryogen-asciidoc.core
  (:require [cryogen-core.markup :refer [rewrite-hrefs markup-registry]]
            [clojure.string :as s])
  (:import org.asciidoctor.Asciidoctor$Factory
           org.asciidoctor.Options
           org.asciidoctor.SafeMode
           java.util.Collections
           cryogen_core.markup.Markup))

(def ^:private ^:static adoc (Asciidoctor$Factory/create))

(defn keys->strings
  "Change the keys in the given map to strings if they are keywords"
  [m]
  (into {}
        (map (fn [[k v]] [(name k) v]) m)))

(defn asciidoc
  "Returns an Asciidoc (http://asciidoc.org/) implementation of the
  Markup protocol."
  []
  (reify Markup
    (dir [this] "asc")
    (exts [this] #{".adoc" ".ad" ".asciidoc" ".asc"})
    (render-fn [this]
      (fn [rdr config]
        (->>
          (.convert adoc
                   (->> (java.io.BufferedReader. rdr)
                        (line-seq)
                        (s/join "\n"))
                   (merge
                     {Options/SAFE (.getLevel SafeMode/SAFE)}
                     (keys->strings
                       (:asciidoctor config))))
          (rewrite-hrefs (:blog-prefix config)))))))

(defn init []
  (swap! markup-registry conj (asciidoc)))
