# cryogen-asciidoc

[![Clojars Project](http://clojars.org/cryogen-asciidoc/latest-version.svg)](http://clojars.org/cryogen-asciidoc)

A Clojure library to provide AsciiDoc rendering to the cryogen-core compiler

## Usage

Add the latest `cryogen-asciidoc` dependency to the `project.clj` of your generated Cryogen blog.

## Configuration

You can set [AsciidoctorJ Options][1] in Cryogen's `config.edn` or in the page's metadata under the key
 `:asciidoctor` (with either string or keyword keys). Options from the two will be deep-merged
 (with the page taking precedence when there is a conflict) and passed directly to
AsciidoctorJ's `.convert` method. The most interesting option is `:attributes` that enables
you to set [AsciiDoctor attributes](https://asciidoctor.org/docs/user-manual/#attributes)
via [the API](https://asciidoctor.org/docs/user-manual/#attribute-assignment-precedence).
 
 Examples:

```clojure
;; config.edn
{:site-title  "AsciiDoctor test"
 ;...
 :asciidoctor {:attributes {"icons" "font"}}}
```

```clojure
;; my-ppost.asc
{:title "My awesome post"
 :asciidoctor {:attributes {"abbr-imho" "<abbr title='in my humble opinion'>IMHO</abbr>"}}
 ...}

I would, {abbr-imho}, not ...
```

Notice that you need to add additional resources to your site for some of
the attributes to have the desired effect. For example for `:icons: font`
you likely need to add some parts [asciidoctor.css](https://github.com/darshandsoni/asciidoctor-skins/blob/gh-pages/css/asciidoctor.css)
or a [variant of it](https://github.com/darshandsoni/asciidoctor-skins/tree/gh-pages/css)
(but it will crash with your Cryogen theme so you might want to extract / create a [minimal subset of your own](https://github.com/holyjak/blog.jakubholy.net/blob/d0dd499becf001687c8fb0143c10955a924f43aa/themes/lotus/css/asciidoctor-custom-subset.css))
and [FontAwesome](https://github.com/darshandsoni/asciidoctor-skins/blob/71ce8dcd401600985dcce7b78d5b5d8b20a0a52d/index.html#L13).

[1]: https://github.com/asciidoctor/asciidoctorj/blob/master/asciidoctorj-api/src/main/java/org/asciidoctor/Options.java

## License

Copyright © 2015 Dmitri Sotnikov <yogthos@gmail.com>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
