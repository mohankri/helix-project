#!/bin/bash

script_dir=`dirname $0`
LIB=$script_dir/lib
CLASSPATH=$script_dir/target/classes:"$LIB"/helix-core-0.7.1.jar:"$LIB"/rabbitmq-client.jar:"$LIB"/commons-cli-1.1.jar:"$LIB"/commons-io-1.2.jar:"$LIB"/commons-math-2.1.jar:"$LIB"/jackson-core-asl-1.8.5.jar:"$LIB"/jackson-mapper-asl-1.8.5.jar:"$LIB"/log4j-1.2.15.jar:"$LIB"/org.restlet-1.1.10.jar:"$LIB"/zkclient-0.1.jar:"$LIB"/zookeeper-3.3.4.jar:"$LIB"/google-collections-1.0.jar:"$LIB"/guava-r05.jar
echo $CLASSPATH

java -cp "$CLASSPATH" com.mohankri.replication.StartAgent $@
