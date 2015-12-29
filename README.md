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
   with no distribution, subscription by prefix)
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
   publisher/subscriber broadcasting with PROXY via POLL, subscription by
   prefix)
1. **taskvent taskwork2 tasksink2** (PUSH, PULL or PIPELINE - map/reduce with
   PUSH distributed load balancing and PULL fair queueing, shutdown workers with
   PUB, SUB)
1. **mtserver hwclient** (REQ, ROUTER, DEALER, REP - async client/server, INPROC
   collapse the broker and workders in a single process with shared context)
1. **mtrelay** (INPROC PAIR - for exclusive synchronization between two
   threads, no automatic reconnection)
1. **syncpub syncsub** (PUB, SUB - async publisher/subscriber broadcasting with
   no distribution, REQ, REP for node synchronization, subscription by prefix)
1. **psenvpub psenvsub** (PUB, SUB envelope with SENDMORE for message key
   (subscribe) and message data, subscription by frame)
1. **rtreq** (ROUTER broker, REQ Least Recently Used sync workers, emulate REQ
   (/), data)
1. **rtdealer** (ROUTER broker, DEALER Least Recently Used async workers)
1. **lbbroker** (REQ, ROUTER, PROXY, ROUTER, REQ - load balancing message
   broker, async clients, Least Recently Used async workers)
1. **asyncsrv** (DEALER, ROUTER, DEALER, DEALER - async client/server)

## Socket types
- **REQ**
    - wraps (/), data
    - strips (/) only
    - sync
    - distributed load balancing
    - initiates commutication
- **REP**
    - strips including (/) and saves the envelope
    - wraps with previously saved envelope
    - sync
    - fair queueing
    - waits for requests
- **DEALER**
    - pass through
    - async (like PUSH and PULL combined)
    - fair queueing
    - distributed load balancing
    - like async REQ
    - async client that talks to multiple REP servers
    - when talking to REP server must emulate REQ (/), data
- **ROUTER**
    - wraps identity, (/), data
    - strips identity only (knows nothing about (/))
    - async
    - fair queueing
    - distributed load balancing
    - like async REP
    - async server that talks to multips REQ clients
- **PUSH**
    - distributed load balancing (round-robin)
- **PULL**
    - fair queueing
- **PAIR**
    - exclusive connection between two points

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
