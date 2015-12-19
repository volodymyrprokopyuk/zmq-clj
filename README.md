# zmq-clj

[ZeroMQ Guide examples in Clojure](https://github.com/imatix/zguide)

[ZeroMQ Book](http://shop.oreilly.com/product/0636920026136.do)

## Installation

### Install [ZMQ](https://github.com/zeromq/libzmq)

```bash
$ tar -xzvf zmq.tar.gz (http://zeromq.org/)
$ cd zmq
$ ./configure --without-libsodium
$ make
$ sudo make install
```

### Install [JZMQ](https://github.com/zeromq/jzmq)

```bash
$ git clone https://github.com/zeromq/jzmq.git
$ cd jzmq
$ ./autogen.sh (sudo apt-get install libtool automake)
$ ./configure (sudo apt-get install openjdk-7-jdk)
$ make
$ sudo make install
```

### Configure ldconfig

```bash
$ sudo vim /etc/ld.so.conf (include /usr/local/lib)
$ sudo ldconfig -v | grep zmq
```
### Install [CLJZMQ](https://github.com/zeromq/cljzmq)

```clojure
(defproject zmq-clj "0.1.0-SNAPSHOT"
  :dependencies [ [ org.zeromq/cljzmq "0.1.4" ] ]
  :jvm-opts [ "-Djava.library.path=/usr/local/lib" ])
```

### Usage

```bash
$ git clone https://github.com/volodymyrprokopyuk/zmq-clj.git
$ cd zmq-clj
$ ninja
$ ./bin/run.sh bin/hwserver.jar
$ ./bin/run.sh bin/hwclient.jar
```

## Examples
- **hwserver** **hwclient** (REQREQ - sync stateless service, client/server)
- **wuserver** **wuclient** (PUBSUB - broadcasting, no distribution,
  publisher/subscriber)
- **taskvent** **taskwork** **tasksink** (PUSHPULL, PIPELINE - load balancing,
  distribution (PUSH) one-way dataflow with fair queueing (PULL), map/reduce)
- **wuserver**&**taskvent** **msreader** (DONTWAIT read
  from multiple sockets at the same time)
- **wuserver**&**taskvent** **mspoller** (POLL - sockets multiplexing, read
  from multiple sockets at the same time)
- **rrclient** **rrbroker** **rrworker** (ROUTER, DEALER - async REQREP
  fair queueing (ROUTER) sockets multiplexing (POLL) load balancing,
  distribution (DEALER))
- **rrclient** **msgqueue** **rrworker** (PROXY - connect frontend with
  backend via POLL)
- **wuserver** **wuproxy** **wuclient** (PUBSUB - broadcasting,
  publisher/subscriber, XPUBXSUB with PROXY)
- **taskvent** **taskwork2** **tasksink2** (PUSHPULL, PIPELINE - load
  balancing (PUSH) one-way dataflow with fair queueing (PULL), map/reduce,
  shutdown workers with PUBSUB)
- **mtserver** **hwclient** (REQ ROUTER DEALER REP - async REQREQ, collapse
  the broker and workders in a single process (INPROC))
- **mtrelay** (INPROC PAIR - exclusive connection between two threads only for
  coordination, no automatic reconnection)
- **syncpub** **syncsub** (PUBSUB - broadcasting, no distribution, REQREQ for
  node syncronization)

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
