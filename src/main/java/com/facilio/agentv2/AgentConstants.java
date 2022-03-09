package com.facilio.agentv2;

import com.facilio.agent.AgentKeys;

public class AgentConstants
{
    //general JSON constants
    public static final String CONTROLLERS = "controllers";
    public static final String ID = "id";
    public static final String AGENT_TYPE = "type";

    public static final String TYPE = "type";
    public static final String SEARCH_KEY = "searchKey";

    //controller constants
    public static final String NAME = "name";
    public static final String DATA_INTERVAL = "interval";
    public static final String WRITABLE = "writable";
    public static final String AGENT_WRITABLE = "agentWritable";
    public static final String ACTIVE = "active";
    public static final String CONTROLLER_PROPS_AS_PROPSSTR = "propsStr";
    public static final String CONTROLLER_PROPS = "controllerProps";
    public static final String AVAILABLE_POINTS = "availablePoints";
    public static final String PORT_NUMBER = "portNumber";
    public static final String CREATED_TIME = "createdTime";
    public static final String LAST_MODIFIED_TIME = AgentKeys.LAST_MODIFIED_TIME;
    public static final String LAST_DATA_SENT_TIME = "lastDataSentTime";
    public static final String DELETED_TIME = "deletedTime";
    public static final String DATA = "data";
    public static final String CONTROLLER_TYPE = "controllerType";
    public static final String IS_LATEST_VERSION = "isLatestVersion";
    public static final String WORKFLOW = "workflow";
    public static final String TRANSFORM_WORKFLOW = "transformWorkflow";

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

    // opc-ua
    public static final String CERT_PATH = "certPath";
    public static final String SECURITY_MODE = "securityMode";
    public static final String SECURITY_POLICY = "securityPolicy";
    public static final String IDENTIFIER = "identifier";
    public static final String UA_POINT_IDENTIFIER = "uaPointIdentifier";
    public static final String NAMESPACE = "namespace";


    //opc-xmlda
    public static final String USER_NAME = "userName";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PATH = "path";

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
    public static final String VERSION = "version";
    public static final String NEW_CONTROLLER_ID = "newControllerId";
    public static final String IS_NEW_AGENT = "isNewAgent";
    public static final String TIMESTAMP = "timestamp";
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
    public static final String UPDATE_CHILD = "updateChild";
    public static final String RECORD_ID = "recordId";

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
    public static final String DISCOVER_CONTROLLERS_TIMEOUT="discoverControllersTimeOut";
    public static final String DISCOVER_POINTS_TIMEOUT="discoverPointsTimeOut";
}
