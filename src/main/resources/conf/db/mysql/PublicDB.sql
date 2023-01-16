-- Public Tables

-- Create Organizations Table
CREATE TABLE IF NOT EXISTS Organizations (
	ORGID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	ORGNAME VARCHAR(100),
	FACILIODOMAINNAME VARCHAR(50),
	LOGO_ID BIGINT,
	PHONE VARCHAR(20),
	MOBILE VARCHAR(20),
	FAX VARCHAR(50),
	STREET VARCHAR(255),
  	CITY VARCHAR(255),
  	STATE VARCHAR(255),
  	ZIP VARCHAR(255),
  	COUNTRY VARCHAR(255),
  	CURRENCY VARCHAR(100),
  	TIMEZONE VARCHAR(100),
  	CREATED_TIME BIGINT,
  	DELETED_TIME BIGINT DEFAULT -1,
  	DATASOURCE VARCHAR(500),
   	DATABASE_NAME VARCHAR(100),
  	LOGGER_LEVEL INT,
  	DATE_FORMAT VARCHAR(20),
  	TIME_FORMAT TINYINT,
  	BUSINESS_HOUR BIGINT,
  	LANGUAGE VARCHAR(200),
  	GROUP_NAME VARCHAR(255),
    ALLOW_USER_TIMEZONE BOOLEAN DEFAULT FALSE
);

-- Create OrgMFASettings Table
CREATE TABLE IF NOT EXISTS OrgMFASettings  (
    ORG_MFASETTINGS_ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ORGID BIGINT NOT NULL,
    IS_TOTP_ENABLED BOOLEAN,
    IS_MOTP_ENABLED BOOLEAN,
    APP_GROUP_TYPE TINYINT
);

CREATE TABLE IF NOT EXISTS SecurityPolicies (
    ORGID BIGINT NOT NULL,
    SECURITY_POLICY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(500),
    IS_MFA_ENABLED BOOLEAN,
    IS_DEFAULT BOOLEAN NOT NULL,
    IS_TOTP_ENABLED BOOLEAN,
    IS_MOTP_ENABLED BOOLEAN,
    IS_PWD_POLICY_ENABLED BOOLEAN,
    PWD_MIN_LENGTH INTEGER,
    PWD_IS_MIXED BOOLEAN,
    PWD_MIN_SPL_CHARS INTEGER,
    PWD_MIN_NUM_DIGITS INTEGER,
    PWD_MIN_AGE INTEGER,
    PWD_PREV_USE_REFUSAL INTEGER,
    IS_WEB_SESS_MANAGEMENT_ENABLED BOOLEAN,
    WEB_SESSION_LIFE_TIME INTEGER,
    IDLE_SESSION_TIME_OUT INTEGER,
    WEB_SESSION_LIFE_TIME_SEC INTEGER
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS Account_Users (
	USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(255),
	USER_VERIFIED BOOLEAN,
	EMAIL VARCHAR(150),
	USERNAME VARCHAR(500),
	PASSWORD VARCHAR(150),
	PWD_LAST_UPDATED_TIME BIGINT,
	PHOTO_ID BIGINT,
	TIMEZONE VARCHAR(100),
	LANGUAGE VARCHAR(50),
	PHONE VARCHAR(20),
	MOBILE VARCHAR(20),
	STREET VARCHAR(255),
	CITY VARCHAR(255),
	STATE VARCHAR(255),
	ZIP VARCHAR(255),
	COUNTRY VARCHAR(255),
	IDENTIFIER VARCHAR(100),
	APP_TYPE TINYINT,
	SECURITY_POLICY_ID BIGINT,
    CONSTRAINT ACCOUNT_USERS_SEC_POL_ID FOREIGN KEY (SECURITY_POLICY_ID) REFERENCES SecurityPolicies(SECURITY_POLICY_ID) ON DELETE SET NULL,
	UNIQUE(USERNAME,PASSWORD,IDENTIFIER)
);

CREATE TABLE IF NOT EXISTS UserPrevPwds (
    USERID BIGINT NOT NULL,
    PASSWORD VARCHAR(150) NOT NULL,
    CHANGED_TIME BIGINT NOT NULL,
    UNIQUE(USERID, CHANGED_TIME),
    CONSTRAINT USER_PREV_PWD_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE
);


-- CREATE UserMfaSettings Table
CREATE TABLE IF NOT EXISTS UserMfaSettings (
    USERID BIGINT NOT NULL,
    USERMFASETTINGS BIGINT AUTO_INCREMENT PRIMARY KEY,
    TOTP_SECRET VARCHAR(40),
    TOTP_STATUS BOOLEAN,
    CONSTRAINT USERMFASETTINGS_ID_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS UserPrevPwds (
    USERID BIGINT NOT NULL,
    PASSWORD VARCHAR(150) NOT NULL,
    CHANGED_TIME BIGINT NOT NULL,
    UNIQUE(USERID, CHANGED_TIME),
    CONSTRAINT USER_PREV_PWD_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS UserSessions (
	SESSIONID BIGINT AUTO_INCREMENT PRIMARY KEY,
	USERID BIGINT,
	SESSION_TYPE INT,
	TOKEN VARCHAR(1000),
	START_TIME BIGINT DEFAULT -1,
	END_TIME BIGINT DEFAULT -1,
	IS_ACTIVE BOOLEAN,
	IPADDRESS VARCHAR(200),
	USER_AGENT VARCHAR(3000),
	SESSION_INFO VARCHAR(5000),
	USER_TYPE VARCHAR(100),
	IS_PROXY_SESSION BOOLEAN,
	CONSTRAINT USERSESSIONS_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE
);

-- CREATE UserMfaSettings Table
CREATE TABLE IF NOT EXISTS UserMfaSettings (
    USERID BIGINT NOT NULL,
    USERMFASETTINGS BIGINT AUTO_INCREMENT PRIMARY KEY,
    TOTP_SECRET VARCHAR(40),
    TOTP_STATUS BOOLEAN,
    CONSTRAINT USERMFASETTINGS_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS User_Mobile_Setting (
	USER_MOBILE_SETTING_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	USERID BIGINT NOT NULL,
	MOBILE_INSTANCE_ID VARCHAR(500) NOT NULL,
	CREATED_TIME BIGINT,
	IS_FROM_PORTAL BOOLEAN DEFAULT FALSE,
	APP_LINK_NAME VARCHAR(100),
	CONSTRAINT UNIQUE_INSTANCE_ID UNIQUE(USERID, MOBILE_INSTANCE_ID),
	CONSTRAINT USER_MOBILE_SETTING_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID)
);

-- Create ORG_Users Table
CREATE TABLE IF NOT EXISTS Account_ORG_Users (
	ORG_USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	USERID BIGINT,
	ORGID BIGINT NOT NULL,
	ISDEFAULT BOOLEAN,
	DELETED_TIME BIGINT DEFAULT -1,
	USER_STATUS BOOLEAN,
	UNIQUE(USERID,ORGID,DELETED_TIME),
	CONSTRAINT Account_ORG_USERS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT Account_ORG_USERS_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON UPDATE CASCADE
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS Users (
	USERID BIGINT PRIMARY KEY,
	NAME VARCHAR(255),
	USER_VERIFIED BOOLEAN,
	DOMAIN_NAME VARCHAR(200),
	EMAIL VARCHAR(150),
	PHOTO_ID BIGINT,
	TIMEZONE CHAR(100),
	LANGUAGE CHAR(50),
	PHONE CHAR(20),
	MOBILE CHAR(20),
	STREET VARCHAR(255),
	CITY VARCHAR(255),
	STATE VARCHAR(255),
	ZIP VARCHAR(255),
	COUNTRY VARCHAR(255),
	UNIQUE(EMAIL,DOMAIN_NAME)
);

-- Create Jobs Table
CREATE TABLE IF NOT EXISTS Jobs (
	JOBID BIGINT,
	ORGID BIGINT,
	JOBNAME VARCHAR(50),
	ADDED_TIME BIGINT(20) NOT NULL DEFAULT 0,
	TIMEZONE VARCHAR(100),
	IS_ACTIVE BOOLEAN,
	TRANSACTION_TIMEOUT INT,
	IS_PERIODIC BOOLEAN,
	PERIOD INT,
	SCHEDULE_INFO VARCHAR(5000),
	NEXT_EXECUTION_TIME BIGINT,
	EXECUTOR_NAME VARCHAR(50),
	END_EXECUTION_TIME BIGINT,
	MAX_EXECUTION INT,
	CURRENT_EXECUTION_COUNT INT,
	STATUS tinyint(4) NOT NULL DEFAULT 3,
    JOB_SERVER_ID bigint(20) NOT NULL DEFAULT 0,
    CURRENT_EXECUTION_TIME bigint(20) NOT NULL DEFAULT 0,
    EXECUTION_ERROR_COUNT tinyint(4) NOT NULL DEFAULT 0,
    LOGGER_LEVEL INT,
	CONSTRAINT JOBS_PK PRIMARY KEY (ORGID, JOBID, JOBNAME)
);

-- CREATE INDEX JOBS_EXECUTOR_NAME_IS_ACTIVE_STATUS_NEXT_EXECUTION_TIME ON Jobs (EXECUTOR_NAME, IS_ACTIVE, STATUS, NEXT_EXECUTION_TIME);

CREATE TABLE IF NOT EXISTS Instant_Job_Deletion_Props (
	JOBID BIGINT NOT NULL PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	EXECUTOR_NAME VARCHAR(50) NOT NULL,
	START_TIME BIGINT,
	END_TIME BIGINT
);

CREATE TABLE IF NOT EXISTS Screen (
  ID BIGINT NOT NULL AUTO_INCREMENT,
  ORGID BIGINT NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  REFRESH_INTERVAL INT DEFAULT NULL,
  SCREEN_SETTING VARCHAR(3000) DEFAULT NULL,
  SITEID BIGINT DEFAULT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS Remote_Screens (
  ID BIGINT NOT NULL AUTO_INCREMENT,
  ORGID BIGINT NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  SCREEN_ID BIGINT,
  TOKEN VARCHAR(500) DEFAULT NULL,
  SESSION_START_TIME BIGINT DEFAULT NULL,
  SESSION_INFO VARCHAR(5000),
  PRIMARY KEY (ID),
  CONSTRAINT REMOTE_SCREEN_SCREEN_ID FOREIGN KEY (SCREEN_ID) REFERENCES Screen(ID) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS TVPasscodes (
  CODE VARCHAR(10) NOT NULL,
  GENERATED_TIME BIGINT DEFAULT NULL,
  EXPIRY_TIME BIGINT DEFAULT NULL,
  CONNECTED_SCREEN_ID BIGINT DEFAULT NULL,
  INFO VARCHAR(5000),
  PRIMARY KEY (CODE),
  CONSTRAINT CONNECTED_SCREEN_ID_FK FOREIGN KEY (CONNECTED_SCREEN_ID) REFERENCES Remote_Screens (ID) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS server_info (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  private_ip varchar(50) DEFAULT NULL,
  environment varchar(18) DEFAULT NULL,
  status tinyint(4) DEFAULT NULL,
  pingtime bigint(20) DEFAULT NULL,
  in_use tinyint(4) DEFAULT NULL,
  leader tinyint(4) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY private_ip (private_ip)
);

CREATE TABLE IF NOT EXISTS SupportEmails (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	SITE_ID BIGINT NOT NULL,
	REPLY_NAME VARCHAR(255),
	ACTUAL_EMAIL VARCHAR(255) UNIQUE NOT NULL,
	FWD_EMAIL VARCHAR(255) UNIQUE NOT NULL,
	LEGACY_EMAIL VARCHAR(255),
	AUTO_ASSIGN_GROUP_ID BIGINT,
	VERIFIED BOOLEAN,
	PRIMARY_SUPPORT_EMAIL BOOLEAN,
	IS_CUSTOM_MAIL TINYINT,
	MAIL_SERVER VARCHAR(255),
	PORT BIGINT,
	USER_NAME VARCHAR(255),
	PASSWORD VARCHAR(255),
	AUTHENTICATION_TYPE INT DEFAULT NULL,
	LATEST_MESSAGE_UID BIGINT,
	UID_VALIDAITY BIGINT,
	MODULEID BIGINT,
	WORKFLOW_RULE_ID BIGINT,
	IMAP_SERVICE_PROVIDER_TYPE VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS FacilioFile (
  FILE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORGID BIGINT,
  FILE_NAME VARCHAR(255),
  FILE_PATH VARCHAR(255),
  FILE_SIZE BIGINT,
  CONTENT_TYPE VARCHAR(255),
  COMPRESSED_FILE_PATH VARCHAR(255),
  COMPRESSED_FILE_SIZE BIGINT,
  UPLOADED_BY BIGINT,
  UPLOADED_TIME BIGINT,
  IS_DELETED BOOLEAN,
  DELETED_BY BIGINT,
  DELETED_TIME BIGINT,
  IS_ORPHAN BOOLEAN
);
-- CREATE INDEX FACILIOFILE_ORGID_DELETED_TIME ON FacilioFile (ORGID, DELETED_TIME); //Uncomment if you are using 2 DB setup

CREATE TABLE IF NOT EXISTS Queue_Data_File (
  FILE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORGID BIGINT,
  FILE_NAME VARCHAR(255),
  FILE_PATH VARCHAR(255),
  FILE_SIZE BIGINT,
  CONTENT_TYPE VARCHAR(255),
  COMPRESSED_FILE_PATH VARCHAR(255),
  COMPRESSED_FILE_SIZE BIGINT,
  UPLOADED_BY BIGINT,
  UPLOADED_TIME BIGINT,
  IS_DELETED BOOLEAN,
  DELETED_BY BIGINT,
  DELETED_TIME BIGINT,
  IS_ORPHAN BOOLEAN
);
CREATE INDEX QUEUE_DATA_FILE_ORGID_DELETED_TIME ON Queue_Data_File (ORGID, DELETED_TIME);

CREATE TABLE IF NOT EXISTS Queue_Data_ResizedFile (
  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  FILE_ID BIGINT NOT NULL,
  ORGID BIGINT NOT NULL,
  WIDTH INT,
  HEIGHT INT,
  FILE_PATH VARCHAR(255),
  FILE_SIZE BIGINT,
  CONTENT_TYPE VARCHAR(255),
  GENERATED_TIME BIGINT,
  URL VARCHAR(8000),
  EXPIRY_TIME BIGINT,
  CONSTRAINT QUEUE_DATA_RESIZEDFILE_FK_ID FOREIGN KEY (FILE_ID) REFERENCES Queue_Data_File(FILE_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Public_Files (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  ORGID BIGINT NOT NULL,
  FILEID BIGINT NOT NULL,
  EXPIRES_ON BIGINT,
  IS_FROM_FILES_TABLE BOOLEAN
);

CREATE TABLE IF NOT EXISTS ClientApp_Org_Grouping ( -- Separate table to maintain org grouping separately
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL UNIQUE -- This is just to take the default entry using name
);

INSERT INTO ClientApp_Org_Grouping (NAME) VALUES ('default');

CREATE TABLE IF NOT EXISTS ClientApp_Org_Grouping_Children (
    -- Making orgId as pk to make sure one org is present only in one org grouping.
    -- Caveat is we can't have separate orgs in different org grouping for different app client version.
    -- But that's okay I guess, otherwise I can't control one org being in 2 orgGroups and for same app name.
    -- Unless we introduce app name in org_grouping which I think is not needed now :D
    ORGID BIGINT PRIMARY KEY,
    GROUP_ID BIGINT NOT NULL,
    CONSTRAINT CLIENTAPP_ORG_GROUPING_CHILDREN_FK_GROUP_ID FOREIGN KEY (GROUP_ID) REFERENCES ClientApp_Org_Grouping (ID),
    CONSTRAINT CLIENTAPP_ORG_GROUPING_CHILDREN_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations (ORGID)
);

CREATE TABLE IF NOT EXISTS ClientApp (
     ID INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
     ENVIRONMENT VARCHAR(255) NOT NULL,
     APP_NAME VARCHAR(255) NOT NULL, -- In case of null, it should be empty string to handle unique constraint. MySQL won't handle unique constraint if one of the columns is empty
     ORG_GROUPING BIGINT NOT NULL, -- In case of null, it'll have default grouping entry again to handle unique constraint.
     VERSION VARCHAR(255) NOT NULL,
     UPDATED_TIME BIGINT DEFAULT NULL,
     UPDATED_BY BIGINT DEFAULT NULL,
     CONSTRAINT UNIQUE_CLIENT_BUILD_COMBO UNIQUE (ENVIRONMENT, APP_NAME, ORG_GROUPING),
     CONSTRAINT CLIENTAPP_FK_ORG_GROUPING FOREIGN KEY (ORG_GROUPING) REFERENCES ClientApp_Org_Grouping (ID)
);

CREATE TABLE IF NOT EXISTS Weather_Stations (
  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  LAT DOUBLE NOT NULL,
  LNG DOUBLE NOT NULL,
  CONSTRAINT Weather_Stations_UNIQUE_ID UNIQUE(LAT,LNG)
);

-- Create table for WorkOrderRequests creation from Email
CREATE TABLE IF NOT EXISTS WorkOrderRequest_EMail (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	S3_MESSAGE_ID VARCHAR(100) NOT NULL,
	TO_ADDR VARCHAR(500) NOT NULL,
	RECIPIENT VARCHAR(100),
	CREATED_TIME BIGINT NOT NULL,
	STATE BIGINT NOT NULL,
	REQUEST_ID BIGINT,
	ORGID BIGINT
);

CREATE TABLE IF NOT EXISTS MobileDetails (
    ID INT(11) AUTO_INCREMENT PRIMARY KEY,
    TYPE VARCHAR(20) NOT NULL,
    MIN_VERSION DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS ConnectedDevices (
  ID BIGINT NOT NULL AUTO_INCREMENT,
  ORGID BIGINT NOT NULL,
  DEVICE_ID BIGINT,
  SESSION_START_TIME BIGINT DEFAULT NULL,
  PRIMARY KEY (ID)
);
CREATE TABLE IF NOT EXISTS DevicePasscodes(
  CODE VARCHAR(10) NOT NULL,
  GENERATED_TIME BIGINT DEFAULT NULL,
  EXPIRY_TIME BIGINT DEFAULT NULL,
  CONNECTED_DEVICE_ID BIGINT DEFAULT NULL,
  INFO VARCHAR(5000),
  DC INTEGER,
  PRIMARY KEY (CODE)
);

CREATE TABLE IF NOT EXISTS ExceptionQueue (
  ID BIGINT PRIMARY KEY AUTO_INCREMENT,
  ORGID BIGINT(20),
  ADDED_TIME BIGINT ,
  VISIBILITY_TIMEOUT BIGINT NOT NULL,
  LAST_CLIENT_RECEIVED_TIME BIGINT,
  MAX_CLIENT_RECEIPT_COUNT INT,
  CLIENT_RECEIPT_COUNT INT,
  DELETED_TIME BIGINT,
  FILE_ID BIGINT(20) NOT NULL
);

CREATE INDEX EXCEPTION_QUEUE_DELETED_TIME_ADDED_TIME ON ExceptionQueue(`DELETED_TIME`,`ADDED_TIME`);

CREATE TABLE IF NOT EXISTS InstantJobQueue (
  ID BIGINT PRIMARY KEY AUTO_INCREMENT,
  ORGID BIGINT(20),
  ADDED_TIME BIGINT ,
  VISIBILITY_TIMEOUT BIGINT NOT NULL,
  LAST_CLIENT_RECEIVED_TIME BIGINT,
  MAX_CLIENT_RECEIPT_COUNT INT,
  CLIENT_RECEIPT_COUNT INT,
  DELETED_TIME BIGINT,
  FILE_ID BIGINT(20) NOT NULL

);
CREATE INDEX INSTANTJOBQUEUE_DELETED_TIME_ADDED_TIME ON InstantJobQueue (DELETED_TIME, ADDED_TIME, ORGID);

CREATE TABLE IF NOT EXISTS RuleInstantJobQueue (
  ID BIGINT PRIMARY KEY AUTO_INCREMENT,
  ORGID BIGINT(20),
  ADDED_TIME BIGINT ,
  VISIBILITY_TIMEOUT BIGINT NOT NULL,
  LAST_CLIENT_RECEIVED_TIME BIGINT,
  MAX_CLIENT_RECEIPT_COUNT INT,
  CLIENT_RECEIPT_COUNT INT,
  DELETED_TIME BIGINT,
  FILE_ID BIGINT(20) NOT NULL
);

CREATE INDEX RULEINSTANTJOBQUEUE_DELETED_TIME_ADDED_TIME ON RuleInstantJobQueue (DELETED_TIME, ADDED_TIME, ORGID);

CREATE TABLE IF NOT EXISTS FormulaInstantJobQueue (
  ID BIGINT PRIMARY KEY AUTO_INCREMENT,
  ORGID BIGINT(20),
  ADDED_TIME BIGINT ,
  VISIBILITY_TIMEOUT BIGINT NOT NULL,
  LAST_CLIENT_RECEIVED_TIME BIGINT,
  MAX_CLIENT_RECEIPT_COUNT INT,
  CLIENT_RECEIPT_COUNT INT,
  DELETED_TIME BIGINT,
  FILE_ID BIGINT(20) NOT NULL
);

CREATE INDEX FORMULAINSTANTJOBQUEUE_DELETED_TIME_ADDED_TIME ON FormulaInstantJobQueue (DELETED_TIME, ADDED_TIME, ORGID);

CREATE TABLE IF NOT EXISTS ReadingDataMigrationInstantJobQueue (
  ID BIGINT PRIMARY KEY AUTO_INCREMENT,
  ORGID BIGINT(20),
  ADDED_TIME BIGINT ,
  VISIBILITY_TIMEOUT BIGINT NOT NULL,
  LAST_CLIENT_RECEIVED_TIME BIGINT,
  MAX_CLIENT_RECEIPT_COUNT INT,
  CLIENT_RECEIPT_COUNT INT,
  DELETED_TIME BIGINT,
  FILE_ID BIGINT(20) NOT NULL
);

CREATE INDEX READINGDATAMIGINSTANTJOBQUEUE_DELETED_TIME_ADDED_TIME ON ReadingDataMigrationInstantJobQueue (DELETED_TIME, ADDED_TIME, ORGID);

CREATE TABLE IF NOT EXISTS ReadingDataMigrationInstantJobQueue_Data(
  QUEUE_ID BIGINT PRIMARY KEY,
  DATA MEDIUMTEXT
);

CREATE TABLE IF NOT EXISTS  Agent_Version(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    VERSION VARCHAR(20) NOT NULL UNIQUE,
    DESCRIPTION VARCHAR(200),
    CREATED_BY VARCHAR(20),
    CREATED_TIME bigint(20),
    URL VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS  FacilioAuditData(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ORG_ID BIGINT NOT NULL ,
    STATUS INT DEFAULT NULL ,
    USER_ID BIGINT NOT NULL ,
    ORG_USER_ID BIGINT NOT NULL ,
    THREAD BIGINT NOT NULL ,
    START_TIME BIGINT NOT NULL ,
    END_TIME BIGINT ,
    QUERY_COUNT BIGINT ,
    SESSION_ID BIGINT,
    SERVER_ID BIGINT DEFAULT NULL,
    METHOD_ID BIGINT,
    REFERER_ID BIGINT,
    REMOTE_IP VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS  FacilioAuditModuleData(
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	MODULE VARCHAR(200) NOT NULL,
	UNIQUE(MODULE)
);

CREATE TABLE IF NOT EXISTS FacilioAuditRefererData(
	REFERER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	REFERER VARCHAR(300) NOT NULL
);

CREATE TABLE IF NOT EXISTS  FacilioAuditActionData(
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ACTION VARCHAR(200) NOT NULL,
	MODULE_ID BIGINT NOT NULL,
	UNIQUE(MODULE_ID,ACTION),
	CONSTRAINT FK_AuditDataAction FOREIGN KEY (MODULE_ID) REFERENCES FacilioAuditModuleData(ID)
);

CREATE TABLE IF NOT EXISTS  FacilioAuditMethodData(
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	METHOD VARCHAR(200) NOT NULL,
	ACTION_ID BIGINT NOT NULL,
	UNIQUE(ACTION_ID,METHOD),
	CONSTRAINT FK_AuditDataMethod FOREIGN KEY (ACTION_ID) REFERENCES FacilioAuditActionData(ID)
);

CREATE TABLE IF NOT EXISTS Agent_Message_Integration (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	NAME VARCHAR(50),
	URL VARCHAR(500),
	TOPIC VARCHAR(50),
	QUEUE_TYPE INT,
	PREPROCESSOR_TYPE INT,
	CLIENT_ID VARCHAR(500),
	AUTH_TYPE INT,
	USERNAME VARCHAR(100),
	PASSWORD VARCHAR(100),
	HEADER VARCHAR(100),
	UNIQUE(ORGID,QUEUE_TYPE,PREPROCESSOR_TYPE),
	UNIQUE(ORGID,NAME)
);
CREATE TABLE IF NOT EXISTS  Agent_VersionLog(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ORGID BIGINT NOT NULL,
    AGENT_ID BIGINT(20) NOT NULL,
    VERSION_ID BIGINT(20) NOT NULL,
    UPDATED_TIME bigint(20),
    AUTH_KEY VARCHAR(40) NOT NULL,
    CREATED_TIME BIGINT(20) NOT NULL
);
CREATE TABLE IF NOT EXISTS Agent_Files_Update_Log(
      ID BIGINT AUTO_INCREMENT PRIMARY KEY,
      ORGID BIGINT NOT NULL,
      AGENT_ID BIGINT(20) NOT NULL,
      FILE_NAME VARCHAR(100) NOT NULL,
      CREATED_TIME bigint(20) NOT NULL,
      UPDATED_TIME bigint(20),
      AUTH_KEY VARCHAR(40) NOT NULL,
      FILE_ID BIGINT NOT NULL
  );

CREATE TABLE IF NOT EXISTS Secret_File (
    FILE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    FILE_NAME VARCHAR(255) NOT NULL,
    FILE_PATH VARCHAR(255),
    FILE_SIZE BIGINT,
    CONTENT_TYPE VARCHAR(255),
    UPLOADED_TIME BIGINT NOT NULL,
    IS_DELETED BOOLEAN,
    UNIQUE (FILE_NAME)
   );
   
 CREATE TABLE IF NOT EXISTS App_Domain (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    DOMAIN VARCHAR(500),
    APP_DOMAIN_TYPE TINYINT NOT NULL,
    APP_GROUP_TYPE TINYINT,
    ORGID BIGINT,
    DOMAIN_TYPE TINYINT,
    FAV_ICO_FILE_ID BIGINT,
    SECURITY_POLICY_ID BIGINT,
    CONSTRAINT APP_DOMAIN_SEC_POL_ID FOREIGN KEY (SECURITY_POLICY_ID) REFERENCES SecurityPolicies(SECURITY_POLICY_ID) ON DELETE SET NULL,
     UNIQUE (DOMAIN)
   );
   

-- Insert Queries
-- Add entry in Jobs for workorderemail parser
INSERT INTO Jobs (ORGID, JOBID, JOBNAME, IS_ACTIVE, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME) VALUES (-1, 1, 'WorkOrderRequestEmailParser', true, true, 60, UNIX_TIMESTAMP()+30,'priority');

CREATE TABLE IF NOT EXISTS Notification_Logger (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	NOTIFICATION_TYPE TINYINT NOT NULL,
	TO_ADDR VARCHAR(200) NOT NULL,
	INFO VARCHAR(5000),
	THREAD_NAME  VARCHAR(200) NOT NULL,
	CREATED_TIME BIGINT NOT NULL
);

-- add entry for default app domain for development
INSERT IGNORE INTO App_Domain (DOMAIN, APP_DOMAIN_TYPE, APP_GROUP_TYPE, DOMAIN_TYPE) VALUES ('${appDomain}', 1, 1, 1);
INSERT IGNORE INTO App_Domain (DOMAIN, APP_DOMAIN_TYPE, APP_GROUP_TYPE, DOMAIN_TYPE) VALUES ('${devDomain}', 6, 1, 1);


CREATE TABLE IF NOT EXISTS Domain_SSO (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	APP_DOMAIN_ID BIGINT NOT NULL,
	NAME VARCHAR(255) NOT NULL,
	TYPE TINYINT,
	CONFIG TEXT,
	IS_ACTIVE BOOLEAN,
	CREATE_USER BOOLEAN,
	CREATED_TIME BIGINT NOT NULL,
	CREATED_BY BIGINT NOT NULL,
	MODIFIED_TIME BIGINT NOT NULL,
	MODIFIED_BY BIGINT NOT NULL,
	SHOW_SSO_LINK BOOLEAN,
	CONSTRAINT APP_DOMAIN_FK FOREIGN KEY (APP_DOMAIN_ID) REFERENCES App_Domain(ID)
);


CREATE TABLE IF NOT EXISTS Account_SSO (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	NAME VARCHAR(255) NOT NULL,
	TYPE TINYINT,
	CONFIG TEXT,
	IS_ACTIVE BOOLEAN,
	CREATED_TIME BIGINT NOT NULL,
	CREATED_BY BIGINT NOT NULL,
	MODIFIED_TIME BIGINT NOT NULL,
	MODIFIED_BY BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS Domain_SSO (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    APP_DOMAIN_ID BIGINT NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    TYPE TINYINT,
    CONFIG TEXT,
    IS_ACTIVE BOOLEAN,
    CREATED_TIME BIGINT NOT NULL,
    CREATED_BY BIGINT NOT NULL,
    MODIFIED_TIME BIGINT NOT NULL,
    MODIFIED_BY BIGINT NOT NULL,
    CONSTRAINT APP_DOMAIN_FK FOREIGN KEY (APP_DOMAIN_ID) REFERENCES App_Domain(ID)
);

CREATE TABLE IF NOT EXISTS Account_Social_Login (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    USERID BIGINT NOT NULL,
    IS_GOOGLE BOOLEAN,
    CONSTRAINT ACCOUNT_SOCIAL_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Org_Weather_Station (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT(20),
	SITE_ID BIGINT(20),
	WEATHER_STATION_ID BIGINT(20)
);

CREATE TABLE IF NOT EXISTS MessageTopic (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT(20) NOT NULL,
	TOPIC VARCHAR(50) NOT NULL,
	MESSAGE_SOURCE VARCHAR(20) NOT NULL,
	PARTITION_ID INT DEFAULT 0,
	MAX_CONSUMERS INT DEFAULT 1,
	MAX_CONSUMERS_PER_INSTANCE INT DEFAULT 1,
	IS_DISABLE BOOLEAN,
	CREATED_TIME BIGINT(20),
	LAST_MODIFIED_TIME BIGINT(20),
	LAST_ENABLED_TIME BIGINT(20),
	LAST_DISABLED_TIME BIGINT(20),
	CONSTRAINT MESSAGE_TOPIC_UNIQUE UNIQUE (MESSAGE_SOURCE,TOPIC)
);

CREATE TABLE IF NOT EXISTS Agent_Disable (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT(20),
	AGENT_ID BIGINT(20),
	IS_DISABLE BOOLEAN,
	RECORD_ID BIGINT(20),
	ENABLED_TIME BIGINT(20),
	DISABLED_TIME BIGINT(20)
);

-- Inbound Connections
CREATE TABLE IF NOT EXISTS Inbound_Connections(
     ID BIGINT PRIMARY KEY AUTO_INCREMENT,
     ORGID BIGINT NOT NULL,
     NAME VARCHAR(100) NOT NULL,
     SENDER VARCHAR (100) NOT NULL,
     AUTH_TYPE INTEGER NOT NULL,
     API_KEY VARCHAR(500),
     CREATED_TIME BIGINT,
     UNIQUE(ORGID,NAME),
     UNIQUE(NAME,SENDER),
     UNIQUE(API_KEY,AUTH_TYPE)
 );

CREATE TABLE IF NOT EXISTS DC_Lookup (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    USERNAME VARCHAR(500),
    DC INTEGER NOT NULL,
    APP_TYPE TINYINT NOT NULL,
    UNIQUE(USERNAME, DC, APP_TYPE)
);

CREATE TABLE IF NOT EXISTS Dev_Client (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(255),
    AUTH_TYPE TINYINT NOT NULL,
    OAUTH2_CLIENT_ID VARCHAR(50),
    USERID BIGINT NOT NULL,
    ORGID BIGINT NOT NULL,
    API_KEY VARCHAR(300),
    CREATED_TIME BIGINT,
    CONSTRAINT DEV_CLIENT_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE,
    CONSTRAINT DEV_CLIENT_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ProxySessions (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    PROXIED_SESSIONID BIGINT NOT NULL,
	EMAIL VARCHAR(150),
	TOKEN VARCHAR(1000) NOT NULL,
	IS_ACTIVE BOOLEAN NOT NULL,
	CREATED_TIME BIGINT,
	CONSTRAINT ProxySessions_FK_PSESSIONID FOREIGN KEY (PROXIED_SESSIONID) REFERENCES UserSessions(SESSIONID) ON DELETE CASCADE
);

-- added Outgoing_Mail_Mapper table
CREATE TABLE IF NOT EXISTS Outgoing_Mail_Mapper (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ORGID BIGINT,
    SYS_CREATED_TIME BIGINT NOT NULL
);

-- added Outgoing_Mail_Responses table
CREATE TABLE IF NOT EXISTS Outgoing_Mail_Responses (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    MAPPER_ID BIGINT,
    EVENT_TYPE VARCHAR(40),
    RESPONSE VARCHAR(4000),
    SYS_CREATED_TIME BIGINT NOT NULL
);
CREATE INDEX OG_RESPONSE_INDEX on Outgoing_Mail_Responses(MAPPER_ID);

CREATE TABLE IF NOT EXISTS Domain_Links (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    APP_DOMAIN_ID BIGINT NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    LINK_TYPE TINYINT,
    IS_EXTERNAL_URL BOOLEAN,
    CONTENT TEXT,
    EXTERNAL_URL VARCHAR(255),
    SHOW_IN_MENU BOOLEAN,
    CREATED_TIME BIGINT NOT NULL,
    CREATED_BY BIGINT NOT NULL,
    MODIFIED_TIME BIGINT NOT NULL,
    MODIFIED_BY BIGINT NOT NULL,
    CONSTRAINT APP_DOMAIN_LINK_FK FOREIGN KEY (APP_DOMAIN_ID) REFERENCES App_Domain(ID)
);