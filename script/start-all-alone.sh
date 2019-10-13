#!/bin/sh

./escheduler-daemon.sh start master-server
./escheduler-daemon.sh start worker-server
./escheduler-daemon.sh start api-server
./escheduler-daemon.sh start logger-server
./escheduler-daemon.sh start alert-server