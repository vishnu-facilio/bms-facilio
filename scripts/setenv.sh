FACILIO_OPTS="-Djava.io.tmpdir=/tmp -XX:-OmitStackTraceInFastThrow"
JMX_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=3000 -Dcom.sun.management.jmxremote.rmi.port=3000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
JMX_EXPORTER_OPTS="-javaagent:/home/facilio/facmon/prometheus/jmx_prometheus_javaagent-0.13.0.jar=9200:/home/facilio/facmon/prometheus/jmx-tomcat.yml"
JVM_OPTS="-Xms512M -Xmx4G -XX:+UseG1GC -server -XX:+IgnoreUnrecognizedVMOptions -XX:+UseStringDeduplication -XX:ParallelGCThreads=2 -XX:MaxGCPauseMillis=30"
JVM_OOM_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/facilio/tomcat/logs/ -XX:OnOutOfMemoryError=/home/facilio/oom.sh"
DATE=`date +%Y-%m-%d`
JAVA_GC_OPTS="-XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M -Xloggc:/home/facilio/tomcat/logs/gc.%t.log -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="$FACILIO_OPTS $JMX_OPTS $JMX_EXPORTER_OPTS $JVM_OPTS $JAVA_GC_OPTS $JVM_OOM_OPTS"

if [ "$DEPLOYMENT_GROUP_NAME" = "pre_production" ]; then
   export CATALINA_OPTS="$CATALINA_OPTS -javaagent:/home/facilio/opentelemetry-javaagent.jar"
   export OTEL_METRICS_EXPORTER=none
   export OTEL_EXPORTER_OTLP_ENDPOINT=http://172.31.72.166:4317
   export OTEL_RESOURCE_ATTRIBUTES=service.name=stage_app
fi
if [ "$DEPLOYMENT_GROUP_NAME" = "production_deployment" ]; then
   export CATALINA_OPTS="$CATALINA_OPTS -javaagent:/home/facilio/opentelemetry-javaagent.jar"
   export OTEL_METRICS_EXPORTER=none
   export OTEL_EXPORTER_OTLP_ENDPOINT=http://172.31.72.166:4317
   export OTEL_RESOURCE_ATTRIBUTES=service.name=production_app
fi
