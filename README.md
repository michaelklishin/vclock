# What is VClock

VClock is an implementation of vector clocks, roughly ported from [Riak Core](https://github.com/basho/riak_core/blob/master/src/vclock.erl).



## Artifacts

... artifacts are [released to Clojars](https://clojars.org/clojurewerkz/vclock). If you are using Maven, add the following repository
definition to your `pom.xml`:

``` xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```


### The Most Recent SNAPSHOT

With Leiningen:

    [clojurewerkz/vclock "1.0.0-SNAPSHOT"]


With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>vclock</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>



## Documentation & Examples

TBD


## Community

To subscribe for announcements of releases, important changes and so on, please follow [@ClojureWerkz](https://twitter.com/#!/clojurewerkz) on Twitter.


## Supported Clojure versions

VClock is built from the ground up for Clojure 1.3.0 and up.


## Continuous Integration Status

[![Continuous Integration status](https://secure.travis-ci.org/clojurewerkz/vclock.png)](http://travis-ci.org/clojurewerkz/vclock)



## VClock Is a ClojureWerkz Project

VClock is part of the [group of Clojure libraries known as ClojureWerkz](http://clojurewerkz.org), together with
[Monger](http://clojuremongodb.info), [Welle](http://clojureriak.info), [Langohr](https://github.com/michaelklishin/langohr), [Elastisch](https://github.com/clojurewerkz/elastisch), [Quartzite](https://github.com/michaelklishin/quartzite) and several others.


## Development

VClock uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests against
supported Clojure versions using

    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit a pull request
on Github.



## License

Copyright (C) 2012 Michael S. Klishin.

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
