package com.facilio.agentv2;

import com.facilio.agent.AgentKeys;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.cacheimpl.ControllerBean;

public class AgentConstants
{
    //general JSON constants
    public static final String CONTROLLERS = "controllers";
    public static final String ID = "id";
    public static final String AGENT_TYPE = "agentType";

    public static final String TYPE = "type";
    public static final String SEARCH_KEY = "searchKey";

    //controller constants
    public static final String NAME = "name";
    public static final String DATA_INTERVAL = "interval";
    public static final String POINT_ALARM_INTERVAL = "pointAlarmInterval";
    public static final String WRITABLE = "writable";
    public static final String AGENT_WRITABLE = "agentWritable";
    public static final String ACTIVE = "active";
    public static final String CONTROLLER_PROPS_AS_PROPSSTR = "propsStr";
    public static final String CONTROLLER_PROPS = "controllerProps";
    public static final String AVAILABLE_POINTS = "availablePoints";
    public static final String PORT = "port";
    public static final String PORT_NUMBER = "portNumber";
    public static final String MINOR_VERSION = "minorVersion";
    public static final String MAJOR_VERSION = "majorVersion";
    public static final String CREATED_TIME = "createdTime";
    public static final String LAST_MODIFIED_TIME = AgentKeys.LAST_MODIFIED_TIME;
    public static final String LAST_DATA_SENT_TIME = "lastDataSentTime";
    public static final String DELETED_TIME = "deletedTime";
    public static final String DATA = "data";
    public static final String TIMESERIES = "time_series";
    public static final String CONTROLLER_TYPE = "controllerType";
    public static final String IS_LATEST_VERSION = "isLatestVersion";
    public static final String WORKFLOW = "workflow";
    public static final String TRANSFORM_WORKFLOW = "transformWorkflow";
    public static final String COMMAND_WORKFLOW = "commandWorkflow";
    public static final String IS_ACTIVE_UPDATE_VALUE = "isActiveUpdateValue";

    //modbus constants
    public static final String SLAVE_ID = "slaveId";
    public static final String IP_ADDRESS = "ipAddress";
    public static final String NETWORK_ID = "networkId";
    public static final String REGISTER_TYPE = "registerType";
    public static final String MODBUS_DATA_TYPE = "modbusDataType";
    public static final String REGISTER_NUMBER = "registerNumber";
    public static final String DATA_TYPE = "dataType";
    public static final String CONTROLLER_ID = "controllerId";
    public static final String IN_USE = "inUse";
    public static final String SUBSCRIBED = "subscribed";
    public static final String THRESHOLD_JSON = "thresholdJson";
    public static final String UNIT = "unit";
    public static final String NETWORK = "network";

    // BACNET constants
    public static final String INSTANCE_NUMBER = "instanceNumber";
    public static final String NETWORK_NUMBER = "networkNumber";
    public static final String URL = "url";
    public static final String INSTANCE_TYPE = "instanceType";
    public static final String ACTUAL_UNIT = "actualUnit";
    public static final String POINT_UNIT = "pointUnit";
    public static final String VENDOR_ID = "vendorId";
    public static final String RANGE = "range";
    public static final String BACNET_PORT = "port";
    public static final String TIMEOUT_SEC = "timeout.sec";

    // opc-ua
    public static final String CERT_PATH = "certPath";
    public static final String SECURITY_MODE = "securityMode";
    public static final String SECURITY_POLICY = "securityPolicy";
    public static final String IDENTIFIER = "identifier";
    public static final String UA_POINT_IDENTIFIER = "uaPointIdentifier";
    public static final String NAMESPACE = "namespace";


    //opc-xmlda
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final String PATH = "path";

    //e2
    public static final String GROUP_NAME = "groupName";
    public static final String PROP_NAME = "propName";
    public static final String PARENT_TYPE = "parentType";
    public static final String APP_ID = "appId";
    public static final String APP_INDEX = "appIndex";
    public static final String PROP_STATUS = "propStatus";
    public static final String PROP_NUMBER = "propNumber";
    public static final String PROP_MODE = "propMode";
    public static final String PROP_TYPE = "propType";
    public static final String PROP_DATA_TYPE = "propDataType";

    // tables
    public static final String AGENT_CONTROLLER_TABLE = "Agent_Controller";
    public static final String MODBUS_CONTROLLER_TABLE = "Modbus_Controller";


    // modbusnetwork
    public static final String COM_PORT = "comPort";
    public static final String BAUD_RATE = "baudRate";
    public static final String PARITY = "parity";
    public static final String STOP_BITS = "stopBits";
    public static final String DATA_BITS = "dataBits";


    public static final String ORGID = "orgId";
    public static final String DISPLAY_NAME = "displayName";
    public static final String DESCRIPTION = "description";
    public static final String POINT_TYPE = "pointType";
    public static final String DEVICE_NAME = "deviceName";
    public static final String ASSET_CATEGORY_ID = "categoryId";
    public static final String RESOURCE_ID ="resourceId";
    public static final String FIELD_ID = "fieldId";
    public static final String MAPPED_TIME = "mappedTime";
    public static final String AGENT_ID = "agentId";
    public static final String AGENT_NAME = "agentName";
    public static final String AGENT_PARAMS = "agentParams";
    public static final String AGENT_LIST = "agentList";




    public static final String FIELD_DEVICE_TABLE = "fieldDevice";
    public static final String SITE_ID = "siteId";
    public static final String POINTS = "points";
    public static final String CONTROLLER = "controller";
    public static final String EXCEPTION = "exception";
    public static final String AGENT = "agent";
    public static final String POINTS_TABLE = "Point";
    public static final String SUBSCRIBE_STATUS = "subscribeStatus";
    public static final String CONFIGURE_STATUS = "configureStatus";
    public static final String VERSION_ID = "versionId";
    public static final String CREATED_BY = "createdBy";
    public static final String PUBLISH_TYPE = "publishType";
    public static final String VALUE = "value";
    public static final String COMMAND = "command";
    public static final String PROPERTY = "property";
    public static final String PAYLOAD = "payload";
    public static final String PAYLOAD_STR = "payLoadString";
    public static final String UNIQUE_ID = "uniqueId";
    public static final String VERSION = "version";
    public static final String NEW_CONTROLLER_ID = "newControllerId";
    public static final String IS_NEW_AGENT = "isNewAgent";
    public static final String TIMESTAMP = "timestamp";
    public static final String ACTUAL_TIMESTAMP = "actual_timestamp";
    public static final String TIMESTAMP_SEC = "ts_sec";
    public static final String PUBLISH_TIMESTAMP_SEC = "publish_ts_sec";
    public static final String AGENT_TABLE = "Agent";
    public static final String CONNECTED = "connected";
    public static final String DEVICE_DETAILS = "deviceDetails";
    public static final String LAST_DATA_RECEIVED_TIME = "lastDataReceivedTime";
    public static final String STATE = "state";
    public static final String MESSAGE_ID = "msgid";
    public static final String STATUS = "status";
    public static final String IOT_DATA = "iotData";
    public static final String IOT_MESSAGE = "iotMessage";
    public static final String PARENT_ID = "parentId";
    public static final String SENT_TIME = "sentTime";
    public static final String ACK_TIME = "ackTime";
    public static final String COMPLETED_TIME = "completedTime";
    public static final String MSG_DATA = "msgData";
    public static final String FIELD_DEIVICES = "fieldDevices";
    public static final String MODULE_NAME = "moduleName";
    public static final String SITE_NAME = "siteName";
    public static final String RESULT = "result";
    public static final String RECORD_IDS = "recordIds";
    public static final String DEVICE_ID = "deviceId";
    public static final String CONFIGURED_DEVICES = "configuredDevices";
    public static final String DEVICE = "device";
    public static final String CONTROLLER_NAME = "controllerName";
    public static final String CONTROLLER_TABLE = "Controllers";
    public static final String POINT_IDS = "pointIds";
    public static final String POINT_NAMES = "pointNames";
    public static final String UPDATE_CHILD = "updateChild";
    public static final String RECORD_ID = "recordId";
    public static final String AGENT_IDS = "agentIds";
    public static final String JOB_PROPS="jobProps";
    public static final String DATA_MISSING = "dataMissing";
    public static final String ACTION_NAME = "actionName";
    public static final String OVERRIDE = "override";
    public static final String EMERGENCY_OVERRIDE = "emergencyOverride";
    public static final String OVERRIDE_MILLIS = "overrideMillis";
    public static final String OVERRIDE_SECONDS = "overrideSeconds";
    public static final String STATES = "states";
    public static final String POINT_ID = "pointId";
    public static final String INPUT_VALUE = "inputValue";
    public static final String INPUT_LABEL = "inputLabel";
    public static final String STATE_TEXT_ENUMS = "stateTextEnums";
    public static final String NUMBER_OF_MSGS = "numberOfMessages";
    public static final String SIZE = "size";
    public static final String CHILDJSON = "childJson";
    public static final String DB_FILE = "dbFile";
    public static final String MODBUS_RTU_NETWORK_MODULE = "modbusRtuNetworkModule";
    public static final String CONFIG_TYPE = "configType";
    public static final String NETWORK_CONFIGURATION = "networkConfiguration";
    public static final String CONTROLLER_CONFIGURATION = "controllerConfiguration";
    public static final String CONFIGURE = "configure";
    public static final String MODBUS_NETWORK = "modbusNetwork";
    public static final String RTU_NETWORK = "rtuNetwork";
    public static final String FILE_ID = "fileId";
    public static final String AGENT_V2_LOG_MODULE = "agentV2Log";
    public static final String ACTUAL_TIME = "actualTime";
    public static final String TIMEOUT = "timeout";
    public static final String TOTAL_COUNT = "total";
    public static final String ACTIVE_COUNT ="active";
    public static final String CONFIGURED_COUNT = "configured";
    public static final String COMMISSIONED_COUNT = "commissioned";
    public static final String SUBSCRIBED_COUNT = "subscribed";
    public static final String SUBNET_NODE = "subnetNode";
    public static final String NEURON_ID = "neuronId" ;
    public static final String LINK_TYPE = "linkType";
    public static final String TARGET_COMP = "targetComp";
    public static final String TARGET_NAME = "targetName";
    public static final String SITE_COUNT = "sites";
    public static final String CERT_FILE_DOWNLOAD_URL = "certFileDownloadUrl";
    public static final String DOWNLOAD_AGENT = "downloadAgent";
    public static final String PROCESSOR_VERSION = "processorVersion";

    public static final String CONFIGURATION_INPROGRESS_COUNT = "confInProgress";
    public static final String SUBSCRIPTION_COUNT = "subsInProgress";
    public static final String INTEGRATIONS = "integrations";
    public static final String POINT = "point";
    public static final String LAST_UPDATED_TIME = "lastUpdatedTime";
    public static final String COUNT = "count";
    public static final String UPDATED_TIME = "updatedTime";
    public static final String AUTH_KEY = "authKey";
    public static final String HEADER = "header";
    public static final String IP = "ip";
    public static final String IDX = "idx";
    public static final String THREAD_DUMP = "threadDump";


    public static final String WRITABLE_SWITCH = "writableSwitch";
    public static final String STATS_MODULE_NAME = "agentStats";
    public static final String HEAP_MAX = "heapMax";
    public static final String HEAP_FREE = "heapFree";
    public static final String NON_HEAP_INITIAL = "nonHeapInitial";
    public static final String NON_HEAP_COMMITTED = "nonHeapCommitted";
    public static final String NON_HEAP_MAX = "nonHeapMax";
    public static final String NON_HEAP_FREE = "nonHeapFree";
    public static final String NUMNER_OF_PROCESSORS = "numberOfProcessors";
    public static final String JAVA_VERSION = "javaVersion";
    public static final String HEAP_INITIAL = "heapInitial";
    public static final String HEAP_COMMITTED = "heapCommitted";
    public static final String MAX_MEMORY = "maxMemory";
    public static final String FREE_MOMORY = "freeMemory";
    public static final String PROCESS_LOAD = "processLoad";
    public static final String SYSTEM_LOAD = "sysLoad";
    public static final String MISC_DATA = "miscData";


    public static final String SYSTEM_POINT_MODULE_NAME = "systemPoint";
    
    public static final String DATA_TIME = "dataTime";
    
    public static final String MESSAGE_TOPIC = "topic";
    public static final String IS_DISABLE = "isDisable";
    public static final String LAST_DISABLED_TIME = "lastDisabledTime";
    public static final String LAST_ENBLED_TIME = "lastEnabledTime";
    
    public static final String SELECT_QUERIES = "selectQueries";
    public static final String INSERT_QUERIES = "insertQueries";
    public static final String UPDATE_QUERIES = "updateQueries";
    public static final String DELETE_QUERIES = "deleteQueries";
    public static final String REDIS_QUERIES = "redisQueries";
    public static final String REDIS_GET_COUNT = "redisGetCount";
    public static final String REDIS_PUT_COUNT = "redisPutCount";
    public static final String REDIS_DELETE_COUNT = "redisDeleteCount";
    public static final String SELECT_QUERIES_TIME = "selectQueriesTime";
    public static final String INSERT_QUERIES_TIME = "insertQueriesTime";
    public static final String UPDATE_QUERIES_TIME = "updateQueriesTime";
    public static final String DELETE_QUERIES_TIME = "deleteQueriesTime";
    public static final String REDIS_TIME = "redisTime";
    public static final String REDIS_GET_TIME = "redisGetTime";
    public static final String REDIS_PUT_TIME = "redisPutTime";
    public static final String REDIS_DELETE_TIME = "redisDeleteTime";
    public static final String PUBLIC_SELECT_QUERIES = "publicSelectQueries";
    public static final String PUBLIC_INSERT_QUERIES = "publicInsertQueries";
    public static final String PUBLIC_UPDATE_QUERIES = "publicUpdateQueries";
    public static final String PUBLIC_DELETE_QUERIES = "publicDeleteQueries";
    public static final String PUBLIC_REDIS_QUERIES = "publicRedisQueries";
    public static final String PUBLIC_REDIS_GET_COUNT = "publicRedisGetCount";
    public static final String PUBLIC_REDIS_PUT_COUNT = "publicRedisPutCount";
    public static final String PUBLIC_REDIS_DELETE_COUNT = "publicRedisDeleteCount";
    public static final String PUBLIC_SELECT_QUERIES_TIME = "publicSelectQueriesTime";
    public static final String PUBLIC_INSERT_QUERIES_TIME = "publicInsertQueriesTime";
    public static final String PUBLIC_UPDATE_QUERIES_TIME = "publicUpdateQueriesTime";
    public static final String PUBLIC_DELETE_QUERIES_TIME = "publicDeleteQueriesTime";
    public static final String PUBLIC_REDIS_TIME = "publicRedisTime";
    public static final String PUBLIC_REDIS_GET_TIME = "publicRedisGetTime";
    public static final String PUBLIC_REDIS_PUT_TIME = "publicRedisPutTime";
    public static final String PUBLIC_REDIS_DELETE_TIME = "publicRedisDeleteTime";
    
    public static final String MESSAGE = "message";
    public static final String AGENT_ACTION = "agentAction";

    public static final String COL_NAME = "NAME";
    public static final String LOGICAL = "logical";

    public static final String API_KEY = "apiKey";
    public static final String INBOUND_CONNECTION_ID="inboundConnectionId";
    public static final String SOURCE = "source";
    public static final String AGENT_CONNECTION_LOST_WORKFLOW_ID = "connectionLostWorkflowId";
    public static final String AGENT_CONNECTION_REGAIN_WORKFLOW_ID = "connectionRegainWorkflowId";

    public static final String RDM_POINT_CLASS = "rdmPointClass";
    public static final String DETAILS = "details";
    public static final String PARTITION_ID = "partitionId";
    public static final String IS_TDB = "isTdb";
    public static final String MAX_CONSUMERS = "maxConsumers";
    public static final String MAX_CONSUMERS_PER_INSTANCE = "maxConsumersPerInstance";
    public static final String MESSAGE_SOURCE = "messageSource";
    public static final String DISCOVER_CONTROLLERS_TIMEOUT = "discoverControllersTimeOut";
    public static final String DISCOVER_POINTS_TIMEOUT = "discoverPointsTimeOut";
    public static final String LAST_RECORDED_TIME = "lastRecordedTime";
    public static final String LAST_RECORDED_VALUE = "lastRecordedValue";
    public static final String TTIME = "ttime";
    public static final String INSTANCE_ID = "instanceId";
    public static final String UNMODELED_DATA_TABLE = "Unmodeled_Data";
    public static final String MESSAGE_SOURCES = "messageSources";
    public static final String WORKFLOW_SYNTAX_ERROR = "workflowSyntaxError";
    public static final String WORKFLOW_RESPONSE = "workflowResponse";
    public static final String MESSAGE_PARTITION = "messagePartition";

    public static final String EVENT_VERSION = "eventVersion";
    public static final String COMMAND_MAX_RETRY_COUNT = "commandMaxRetryCount";

    public static final String RECORDS = "records";
    public static final String CONTROLLERIDS = "controllerIds";
    public static final String COMMISSIONINGLOG_CONTROLLER = "commissioninglogcontroller";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String AGENT_SOURCE_TYPE = "agentSourceType";
    public static final String FILTER_INSTANCE_TYPES = "filterInstanceTypes";
    public static final String FILTERS = "filters";
    public static final String CONFIGURE_POINTS_COUNT = "configurePointsCount";

    //Points status
    public static final String UNCONFIGURED = "UNCONFIGURED";
    public static final String CONFIGURED = "CONFIGURED";
    public static final String COMMISSIONED = "COMMISSIONED";
    public static final String ERROR_POINTS = "errorPoints";


    public static AgentBean getAgentBean() throws Exception {
        return (AgentBean) BeanFactory.lookup("AgentBean");
    }
    public static ControllerBean getControllerBean() throws Exception {
        return (ControllerBean) BeanFactory.lookup("ControllerBean");
    }
    //point modules
    public static final String OPC_XML_DA_POINT_MODULE = "opcXmlDAPoint";
    public static final String MODBUS_RTU_POINT_MODULE = "modbusRtuPoint";
    public static final String MODBUS_TCP_POINT_MODULE = "modbusTcpPoint";
    public static final String NIAGARA_POINT_MODULE = "niagaraPoint";
    public static final String MISC_POINT_MODULE = "miscPoint";
    public static final String OPC_UA_POINT_MODULE = "opcUAPoint";
    public static final String LON_WORKS_POINT_MODULE = "lonworksPoint";
    public static final String RDM_POINT_MODULE = "rdmPoint";
    public static final String E2_POINT_MODULE = "e2Point";
    public static final String BACNET_IP_POINT_MODULE = "bacnetIpPoint";

    public static class PointConfigStatus{
        public static final String UNCONFIGURED = "unconfigured";
        public static final String CONFIGURED = "configured";
        public static final String COMMISSIONED = "commissioned";
        public static final String SUBSCRIBED = "subscribed";
    }

    public static FacilioModule getPointModule() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        return moduleBean.getModule(POINT);
    }
    public static List<FacilioField> getPointFields() throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        return moduleBean.getAllFields(POINT);
    }

    public enum AgentSourceType implements FacilioIntEnum {
        WEB("Web"),
        AGENT("Agent");

        private String name;
        AgentSourceType(String name) {
            this.name = name;
        }

        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static AgentSourceType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public static String getStringValue(Object var) {
        return var == null ? null : var.toString();
    }

    public static Integer getIntValue(Object var) {
        return var == null ? null : Integer.parseInt(var.toString());
    }

    public static Long getLongValue(Object var) {
        return var == null ? null : Long.parseLong(var.toString());
    }
}
