(defproject zmq-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ [ org.clojure/clojure "1.7.0" ]
                  [ org.zeromq/cljzmq "0.1.4" ] ]
  :jvm-opts [ "-Djava.library.path=/usr/local/lib" ]
  :main ^:skip-aot zmq-clj.core
  :target-path "target/%s"
  :profiles { :uberjar {:aot :all}
              :hwserver { :main zmq-clj.hwserver
                          :uberjar-name "hwserver.jar" }
              :hwclient { :main zmq-clj.hwclient
                          :uberjar-name "hwclient.jar" }
              :wuserver { :main zmq-clj.wuserver
                          :uberjar-name "wuserver.jar" }
              :wuclient { :main zmq-clj.wuclient
                          :uberjar-name "wuclient.jar" }
              :taskvent { :main zmq-clj.taskvent
                          :uberjar-name "taskvent.jar" }
              :taskwork { :main zmq-clj.taskwork
                          :uberjar-name "taskwork.jar" }
              :tasksink { :main zmq-clj.tasksink
                          :uberjar-name "tasksink.jar" } })
