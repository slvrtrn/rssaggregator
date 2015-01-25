# rssaggregator

This project is a simple RSS Aggregator written by me in Scala just for educational purpose.
Here is a complete list of technologies and instruments used:

- Scala [ http://www.scala-lang.org/ ]
- ScalaTest [ http://www.scalatest.org/ ]
- ScalaStyle
- SBT [ http://www.scala-sbt.org/ ]
- SBT Revolver [ https://github.com/spray/sbt-revolver ]
- Finatra [ http://finatra.info/ ]
- MongoDB [ http://www.mongodb.org/ ]
- SalatDAO [ https://github.com/novus/salat/wiki/SalatDAO ]
- Redis [ http://redis.io ]
- RedisReact [ https://github.com/debasishg/scala-redis-nb ]
- ScalDI [ http://scaldi.org/ ]
- Typesafe Config [ https://github.com/typesafehub/config ]
- Scala BCrypt [ https://github.com/t3hnar/scala-bcrypt ]
- Accord [ https://github.com/wix/accord ]
- Json4s [ https://github.com/json4s/json4s ]
- Jade [ http://jade-lang.com/ ]
- Jade4J [ https://github.com/neuland/jade4j ]
- AngularJS [ https://angularjs.org/ ]
- Less [ http://lesscss.org/ ]
- Bower [ http://bower.io/ ]
- Gulp [ http://gulpjs.com/ ]

I hope that I have listed all of them.
Pre-compiled scripts, stylesheets and templates are also available in repository.

---------------------------------------------------------------------------------

Setup

- For general use: create database "aggregator" in Mongo and run an instance of Redis server on 6379 port.
- For tests: create database "aggregator-test" in Mongo and run an instance of Redis server on 6380 port.

All ports, URI, etc are adjustable at config, that is located at "./src/main/resources/application.conf".
Make sure that SBT is installed on your system, then just type "sbt" in project's directory.
Then, to run the application type "run" in the console (or "~reStart", if you want to recompile and restart the application on the fly when source code changes); to run only the tests type "test".
