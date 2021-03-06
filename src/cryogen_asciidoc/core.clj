(ns cryogen-asciidoc.core
  (:require [cryogen-core.markup :refer [rewrite-hrefs markup-registry]]
            [cryogen-asciidoc.extensions :as ext]
            [clojure.string :as s])
  (:import org.asciidoctor.Asciidoctor$Factory
           org.asciidoctor.Options
           org.asciidoctor.SafeMode
           cryogen_core.markup.Markup))

(def ^:private ^:static adoc (atom nil))

(defn get-or-init-adoc
  "Initialize adoc based on the configuration"
  [adoc {:keys [extensions]}]
  (compare-and-set! adoc nil (delay (doto (Asciidoctor$Factory/create)
                                      (ext/register-extensions extensions))))
  @@adoc)


(defn keys->strings
  "Change the keys in the given map to strings if they are keywords"
  [m]
  (into {}
        (map (fn [[k v]] [(name k) v]) m)))

(defn deep-merge
  "Recursively merges maps together. If all the maps supplied have nested maps
  under the same keys, these nested maps are merged. Otherwise the value is
  overwritten, as in `clojure.core/merge`.

  SOURCE: https://github.com/weavejester/medley/blob/master/src/medley/core.cljc"
  {:arglists '([& maps])
   :added    "1.1.0"}
  ([])
  ([a] a)
  ([a b]
   (when (or a b)
     (letfn [(merge-entry [m e]
               (let [k  (key e)
                     v' (val e)]
                 (if (contains? m k)
                   (assoc m k (let [v (get m k)]
                                (if (and (map? v) (map? v'))
                                  (deep-merge v v')
                                  v')))
                   (assoc m k v'))))]
       (reduce merge-entry (or a {}) (seq b)))))
  ([a b & more]
   (reduce deep-merge (or a {}) (cons b more))))

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
          (.convert (get-or-init-adoc adoc (:asciidoctor config))
                   (->> (java.io.BufferedReader. rdr)
                        (line-seq)
                        (s/join "\n"))
                   (deep-merge
                     {Options/SAFE (.getLevel SafeMode/SAFE)}
                     (keys->strings
                       (:asciidoctor config))
                     (keys->strings
                       (-> config :page-meta :asciidoctor))))
          (rewrite-hrefs (:blog-prefix config)))))))

(defn init []
  (swap! markup-registry conj (asciidoc)))
