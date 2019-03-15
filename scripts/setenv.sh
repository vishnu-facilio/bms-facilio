FACILIO_OPTS="-XX:-OmitStackTraceInFastThrow"
JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.rmi.port=3000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
JMX_EXPORTER_OPTS="-javaagent:/home/ubuntu/facmon/prometheus/jmx_prometheus_javaagent-0.3.1.jar=9200:/home/ubuntu/facmon/prometheus/jmx-tomcat.yml"
JVM_OPTS="-Xms1G -Xmx2G -XX:+UseG1GC -server -XX:+UseStringDeduplication -XX:ParallelGCThreads=2 -XX:MaxGCPauseMillis=30"
DATE=`date +%Y-%m-%d`
JAVA_GC_OPTS="-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M -Xloggc:/home/ubuntu/tomcat/logs/gc.%t.log -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="$FACILIO_OPTS $JVM_OPTS $JAVA_GC_OPTS"