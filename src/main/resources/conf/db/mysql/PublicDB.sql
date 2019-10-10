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
  	LOGGER_LEVEL INT
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS Account_Users (
	USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(255),
	USER_VERIFIED BOOLEAN,
	EMAIL VARCHAR(150),
	DOMAIN_NAME VARCHAR(200),
	PASSWORD VARCHAR(150),
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
	UNIQUE(EMAIL,DOMAIN_NAME)
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
	CONSTRAINT USERSESSIONS_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS User_Mobile_Setting (
	USER_MOBILE_SETTING_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	USERID BIGINT NOT NULL,
	MOBILE_INSTANCE_ID VARCHAR(500) NOT NULL,
	CREATED_TIME BIGINT,
	IS_FROM_PORTAL BOOLEAN DEFAULT FALSE,
	CONSTRAINT UNIQUE_INSTANCE_ID UNIQUE(USERID, MOBILE_INSTANCE_ID),
	CONSTRAINT USER_MOBILE_SETTING_FK_USERID FOREIGN KEY (USERID) REFERENCES Account_Users(USERID)
);

-- Create ORG_Users Table
CREATE TABLE IF NOT EXISTS Account_ORG_Users (
	ORG_USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	USERID BIGINT,
	ORGID BIGINT NOT NULL,
	ISDEFAULT BOOLEAN,
	USER_STATUS BOOLEAN,
	DELETED_TIME BIGINT DEFAULT -1,
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
	TIMEZONE VARCHAR(100),
	IS_ACTIVE BOOLEAN,
	TRANSACTION_TIMEOUT INT,
	IS_PERIODIC BOOLEAN,
	PERIOD INT,
	SCHEDULE_INFO VARCHAR(1000),
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
	CONSTRAINT JOBS_PK PRIMARY KEY (JOBID, JOBNAME)
);

CREATE TABLE IF NOT EXISTS Screen (
  ID BIGINT NOT NULL AUTO_INCREMENT,
  ORGID BIGINT NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  REFRESH_INTERVAL INT DEFAULT NULL,
  SCREEN_SETTING VARCHAR(3000) DEFAULT NULL,
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
	REPLY_NAME VARCHAR(50),
	ACTUAL_EMAIL VARCHAR(50) UNIQUE NOT NULL,
	FWD_EMAIL VARCHAR(50) UNIQUE NOT NULL,
	AUTO_ASSIGN_GROUP_ID BIGINT,
	VERIFIED BOOLEAN,
	PRIMARY_SUPPORT_EMAIL BOOLEAN
);

CREATE TABLE IF NOT EXISTS FacilioFile (
  FILE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORGID BIGINT,
  FILE_NAME VARCHAR(255),
  FILE_PATH VARCHAR(255),
  FILE_SIZE BIGINT,
  CONTENT_TYPE VARCHAR(255),
  UPLOADED_BY BIGINT,
  UPLOADED_TIME BIGINT,
  IS_DELETED BOOLEAN
);

CREATE TABLE IF NOT EXISTS ClientApp (
     id int(11) NOT NULL AUTO_INCREMENT,
     environment varchar(50) NOT NULL,
     version varchar(20) NOT NULL,
	is_new_client_build BOOLEAN DEFAULT 0,
     updatedTime bigint(20) DEFAULT NULL,
     updatedBy bigint(20) DEFAULT NULL,
     PRIMARY KEY (id)
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
	CREATED_TIME BIGINT NOT NULL,
	IS_PROCESSED BOOLEAN,
	REQUEST_ID BIGINT
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
  PRIMARY KEY (CODE),
  CONSTRAINT CONNECTED_DEVICE_ID_ConnectedDevices_ID FOREIGN KEY(CONNECTED_DEVICE_ID) REFERENCES ConnectedDevices(ID) ON DELETE CASCADE
);



-- Insert Queries
-- Add entry in Jobs for workorderemail parser
INSERT INTO Jobs (JOBID, JOBNAME, IS_ACTIVE, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME) VALUES (1, 'WorkOrderRequestEmailParser', true, true, 60, UNIX_TIMESTAMP()+30,'priority');

