#!/usr/bin/env bash

BASEDIR=$(cd `dirname $0`; pwd)

DIABLO_HOME=$BASEDIR/..

LIB_HOME=$DIABLO_HOME/lib

LOG_HOME=$RULE_HOME/logs

CONF_FILE=$DIABLO_HOME/conf/diablo.conf
. $CONF_FILE

JAR_FILE=$LIB_HOME/diablo-server.jar

PID_FILE=$DIABLO_HOME/diablo.pid

# JAVA_OPTS
JAVA_OPTS="-server -Duser.dir=$BASEDIR"
JAVA_OPTS="${JAVA_OPTS} $JAVA_HEAP_OPTS"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:HeapDumpPath=$LOG_HOME -Xloggc:$LOG_HOME/gc.log"

# CONFIG_OPTS
CONFIG_OPTS="--server.address=$BIND_ADDR --server.port=$LISTEN_PORT"
CONFIG_OPTS="$CONFIG_OPTS --spring.redis.host=$REDIS_HOST --spring.redis.port=$REDIS_PORT"
CONFIG_OPTS="$CONFIG_OPTS --diablo.pass=$TOWER_PASS"
CONFIG_OPTS="$CONFIG_OPTS --diablo.serverCheckInterval=$CHECK_SERVER_INTERVAL --diablo.clientAuth=$CLIENT_AUTH"

function start()
{
    java $JAVA_OPTS -jar $JAR_FILE $CONFIG_OPTS $1 > /dev/null 2>&1 &
    echo $! > $PID_FILE
}

function stop()
{
    pid=`cat $PID_FILE`
    echo "kill $pid ..."
    kill $pid
    rm -f $PID_FILE
}

args=($@)

case "$1" in

    'start')
        start
        ;;

    'stop')
        stop
        ;;

    'restart')
        stop
        start
        ;;

    'help')
        help $2
        ;;
    *)
        echo "Usage: $0 { start | stop | restart | help }"
        exit 1
        ;;
esac