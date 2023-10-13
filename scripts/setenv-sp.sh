FACILIO_OPTS="-Djava.io.tmpdir=/tmp -XX:-OmitStackTraceInFastThrow"
JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.rmi.port=3000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
JMX_EXPORTER_OPTS="-javaagent:/home/facilio/facmon/prometheus/jmx_prometheus_javaagent-0.13.0.jar=9200:/home/facilio/facmon/prometheus/jmx-tomcat.yml"
JVM_OPTS="-Xms512M -Xmx4G -XX:+UseG1GC -server -XX:+IgnoreUnrecognizedVMOptions -XX:+UseStringDeduplication -XX:ParallelGCThreads=2 -XX:MaxGCPauseMillis=30"
JVM_OOM_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/facilio/tomcat/logs/ -XX:OnOutOfMemoryError=/home/facilio/oom.sh"
DATE=`date +%Y-%m-%d`
JAVA_GC_OPTS="-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M -Xloggc:/home/facilio/tomcat/logs/gc.%t.log -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="$FACILIO_OPTS $JMX_OPTS $JMX_EXPORTER_OPTS $JVM_OPTS $JAVA_GC_OPTS $JVM_OOM_OPTS"