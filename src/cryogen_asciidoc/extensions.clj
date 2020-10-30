(ns cryogen-asciidoc.extensions
  "Extensions for Asciidoctor such as macros.
  See https://asciidoctor.org/docs/user-manual/#extensions
  and https://github.com/asciidoctor/asciidoctorj/blob/master/docs/integrator-guide.adoc#writing-an-extension"
  (:import (org.asciidoctor.extension InlineMacroProcessor BlockMacroProcessor JavaExtensionRegistry)
           (org.asciidoctor.ast StructuralNode ContentNode)))

(def all-types #{:inline :block})

(defn register-extensions
  "Register extensions based on the `:asciidoctor :extensions` config map, that
  contains values such as `{\"gh\" 'my.ns/myfn}`"
  [adoc extensions]
  (let [^JavaExtensionRegistry registry (.javaExtensionRegistry adoc)]
    (run!
      (fn [[ext-name qualified-fn-symbol]]
        (let [macrofn   (requiring-resolve qualified-fn-symbol)
              types     (-> macrofn meta :extension/types)
              supports? (or types (constantly true))]
          (when types
            (assert (set? types) "The :extension/types must be a set")
            (assert (every? all-types types) (str "The only known types are " all-types)))
          ;; Register fnsym as both an inline and block macro; the user decides whether to support both
          ;; or not and uses it accordingly (with only `<name>:`, `<name>::`, or both)
          (when (supports? :inline)
            (.inlineMacro registry ^String ext-name
                          (proxy [InlineMacroProcessor] [ext-name]
                            (process [^ContentNode parent target attributes]
                              (macrofn this parent target attributes)))))
          (when (supports? :block)
            (.blockMacro registry ^String ext-name
                         (proxy [BlockMacroProcessor] [ext-name]
                           (process [^StructuralNode parent target attributes]
                             (macrofn this parent target attributes)))))))
      extensions))
  adoc)
