# zmq-clj

[ZeroMQ Guide examples in Clojure](https://github.com/imatix/zguide)

## Installation

### Install ZMQ

```bash
$ tar -xzvf zmq.tar.gz (http://zeromq.org/)
$ cd zmq
$ ./configure --without-libsodium
$ make
$ sudo make install
```

### Install JZMQ

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

### Usage

```bash
$ git clone https://github.com/volodymyrprokopyuk/zmq-clj.git
$ cd zmq-clj
$ ninja
$ ./bin/run.sh bin/hwserver.jar
$ ./bin/run.sh bin/hwclient.jar
```

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
