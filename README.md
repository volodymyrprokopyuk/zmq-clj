# zmq-clj

[ZeroMQ](https://github.com/zeromq/libzmq) is a network library and concurrency
framework with async IO

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
1. **hwserver hwclient** (REQ, REP - sync client/server service)
1. **wuserver wuclient** (PUB, SUB - async publisher/subscriber broadcasting
   with no distribution)
1. **taskvent taskwork tasksink** (PUSH, PULL or PIPELINE - map/reduce with PUSH
   distributed load balancing and PULL fair queueing)
1. **wuserver/taskvent msreader** (DONTWAIT - read from multiple sockets at the
   same time)
1. **wuserver/taskvent mspoller** (POLL - sockets multiplexing, read from
   multiple sockets at the same time)
1. **rrclient rrbroker rrworker** (REQ, ROUTER, DEALER, REP - async
   client/server, ROUTER fair queueing, POLL sockets multiplexing, DEALER
   distributed load balancing, SENDMORE multipart messages)
1. **rrclient msgqueue rrworker** (REQ, ROUTER, DEALER, REP - async
   client/server, PROXY - connect frontend with backend via POLL)
1. **wuserver wuproxy wuclient** (PUB, XSUB, XPUB, SUB - async
   publisher/subscriber broadcasting with PROXY via POLL)
1. **taskvent taskwork2 tasksink2** (PUSH, PULL or PIPELINE - map/reduce with
   PUSH distributed load balancing and PULL fair queueing, shutdown workers with
   PUB, SUB)
1. **mtserver hwclient** (REQ ROUTER DEALER REP - async REQREQ, collapse the
   broker and workders in a single process (INPROC))
1. **mtrelay** (INPROC PAIR - exclusive connection between two threads only for
   coordination, no automatic reconnection)
1. **syncpub syncsub** (PUBSUB - broadcasting, no distribution, REQREQ for node
   syncronization)
1. **psenvpub psenvsub** (PUBSUB envelope with SNDMORE for message key
   (subscribe) and message data)
1. **rtreq** (ROUTER broker (round-robin distribution), REQ worker, Least
   Recently Used worker)
1. **rtdealer** (ROUTER broker (round-robin distribution), DEALER worker, Least
   Recently Used worker)
1. **lbbroker** (REQ ROUTER PROXY ROUTER REQ - load balancing message broker,
   Least Recently Used worker)
1. **asyncsrv** (DEALER ROUTER DEALER DEALER - async client/server)

## Socket types
- **REQ**
    - (/), data
    - sync
    - distributed load balancing
    - initiates commutication
- **REP**
    - strip/wrap
    - sync
    - fair queueing
    - waits for requests
- **DEALER**
    - pass through
    - async
    - fair queueing
    - distributed load balancing
    - like async REQ (async client)
- **ROUTER**
    - identity, (/), data
    - async
    - fair queueing
    - like async REP (async server)
- **PUSH**
    - distributed load balancing (round-robin)
- **PULL**
    - fair queueing

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
