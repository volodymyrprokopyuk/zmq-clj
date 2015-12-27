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
- **hwserver hwclient** (REQ, REP - sync client/server service)
- **wuserver wuclient** (PUB, SUB - async publisher/subscriber broadcasting with
  no distribution)
- **taskvent taskwork tasksink** (PUSH, PULL or PIPELINE - map/reduce with PUSH
  distributed load balancing and PULL fair queueing)
- **wuserver/taskvent msreader** (DONTWAIT - read from multiple sockets at the
  same time)
- **wuserver/taskvent mspoller** (POLL - sockets multiplexing, read from
  multiple sockets at the same time)
- **rrclient rrbroker rrworker** (REQ, ROUTER, DEALER, REP - async
  client/server, ROUTER fair queueing, POLL sockets multiplexing, DEALER
  distributed load balancing, SENDMORE multipart messages)
- **rrclient msgqueue rrworker** (PROXY - connect frontend with backend via
  POLL)
- **wuserver wuproxy wuclient** (PUBSUB - broadcasting, publisher/subscriber,
  XPUBXSUB with PROXY)
- **taskvent taskwork2 tasksink2** (PUSHPULL, PIPELINE - load balancing (PUSH)
  one-way dataflow with fair queueing (PULL), map/reduce, shutdown workers with
  PUBSUB)
- **mtserver hwclient** (REQ ROUTER DEALER REP - async REQREQ, collapse the
  broker and workders in a single process (INPROC))
- **mtrelay** (INPROC PAIR - exclusive connection between two threads only for
  coordination, no automatic reconnection)
- **syncpub syncsub** (PUBSUB - broadcasting, no distribution, REQREQ for node
  syncronization)
- **psenvpub psenvsub** (PUBSUB envelope with SNDMORE for message key
  (subscribe) and message data)
- **rtreq** (ROUTER broker (round-robin distribution), REQ worker, Least
  Recently Used worker)
- **rtdealer** (ROUTER broker (round-robin distribution), DEALER worker, Least
  Recently Used worker)
- **lbbroker** (REQ ROUTER PROXY ROUTER REQ - load balancing message broker,
  Least Recently Used worker)
- **asyncsrv** (DEALER ROUTER DEALER DEALER - async client/server)

## Sockets types
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
