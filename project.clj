(defproject cryogen-asciidoc "1.0.0"
  :description "AsciiDoc parser for Cryogen"
  :url "https://github.com/cryogen-project/cryogen-asciidoc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cryogen-core "0.4.4"]
                 ;; ;; BEWARE: keep in sync with deps.edn
                 [org.asciidoctor/asciidoctorj "3.0.0-alpha.1"]])
