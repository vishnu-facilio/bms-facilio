-- Create Organizations Table
CREATE TABLE IF NOT EXISTS Organizations (
	ORGID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGNAME VARCHAR(50),
	FACILIODOMAINNAME VARCHAR(50)
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS Users (
	USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	COGNITO_ID BIGINT ,
	USER_VERIFIED BOOLEAN,
	EMAIL VARCHAR(150)
);

-- Create ORG_Users Table
CREATE TABLE IF NOT EXISTS ORG_Users (
	ORG_USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	USERID BIGINT,
	ORGID BIGINT,
	INVITEDTIME BIGINT,
	ISDEFAULT BOOLEAN,
	INVITATION_ACCEPT_STATUS BOOLEAN,
	CONSTRAINT ORG_USERS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT ORG_USERS_FK_USERID FOREIGN KEY (USERID) REFERENCES Users(USERID)
);

-- Create GLobal_Domain_Index
CREATE TABLE IF NOT EXISTS Global_Domain_Index (
	DOMAINNAME VARCHAR(50),
	ORGID BIGINT 
);

-- Create Jobs Table
CREATE TABLE IF NOT EXISTS Jobs (
	JOBID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	JOBNAME VARCHAR(50),
	ISPERIODIC BOOLEAN,
	PERIOD INT,
	NEXTEXECUTIONTIME BIGINT,
	EXECUTORNAME VARCHAR(50),
	CONSTRAINT JOBS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

-- Create Assets Table
CREATE TABLE IF NOT EXISTS Assets (
	ASSETID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(50),
	ORGID BIGINT,
	CONSTRAINT ASSETS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

-- Create Tickets Table
CREATE TABLE IF NOT EXISTS Tickets (
	TICKETID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	REQUESTOR VARCHAR(50),
	SUBJECT VARCHAR(50),
	DESCRIPTION VARCHAR(1000),
	STATUS TINYINT,
	AGENTID BIGINT,
	FAILED_ASSET_ID BIGINT,
	DUE_DATE BIGINT,
	CONSTRAINT TICKETS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT TICKETS_FK_AGENTID FOREIGN KEY (AGENTID) REFERENCES ORG_Users(ORG_USERID),
	CONSTRAINT TICKETS_FK_ASSETID FOREIGN KEY (FAILED_ASSET_ID) REFERENCES Assets(ASSETID)
);

-- Device Table

CREATE TABLE IF NOT EXISTS Service (
	SERVICE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Building (
	BUILDING_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(50),
	AREA BIGINT
);

CREATE TABLE IF NOT EXISTS Zone (
	ZONE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(50),
	AREA BIGINT,
	BUILDING_ID BIGINT,
	CONSTRAINT ZONE_FK_BUILDING_ID FOREIGN KEY (BUILDING_ID) REFERENCES Building(BUILDING_ID)
);

CREATE TABLE IF NOT EXISTS Controller (
	CONTROLLER_ID BIGINT,
	CONTROLLER_TYPE INT,
	IP_ADDRESS VARCHAR(50),
	CERTIFICATE BLOB,
	POLL_TIME INT,
	JOBID BIGINT,
	JOB_STATUS BOOLEAN,
	CONSTRAINT CONTROLLER_FK_ASSET_ID FOREIGN KEY (CONTROLLER_ID) REFERENCES Assets(ASSETID),
	CONSTRAINT CONTROLLER_FK_JOBID FOREIGN KEY (JOBID) REFERENCES Jobs(JOBID)
);

CREATE TABLE IF NOT EXISTS Device (
	DEVICE_ID BIGINT,
	SERVICE_ID BIGINT,
	ZONE_ID BIGINT,
	BUILDING_ID BIGINT,
	CONTROLLER_ID BIGINT,
	PARENT_DEVICE_ID BIGINT,
	DEVICE_TYPE INT,
	STATUS INT,
	CONSTRAINT DEVICE_FK_DEVICE_ID FOREIGN KEY (DEVICE_ID) REFERENCES Assets(ASSETID),
	CONSTRAINT DEVICE_FK_SERVICE_ID FOREIGN KEY (SERVICE_ID) REFERENCES Service(SERVICE_ID),
	CONSTRAINT DEVICE_FK_ZONE_ID FOREIGN KEY (ZONE_ID) REFERENCES Zone(ZONE_ID),
	CONSTRAINT DEVICE_FK_PARENT_DEVICE_ID FOREIGN KEY (PARENT_DEVICE_ID) REFERENCES Device(DEVICE_ID),
	CONSTRAINT DEVICE_FK_BUILDING_ID FOREIGN KEY (BUILDING_ID) REFERENCES Building(BUILDING_ID),
	CONSTRAINT DEVICE_FK_CONTROLLER_ID FOREIGN KEY (CONTROLLER_ID) REFERENCES Controller(CONTROLLER_ID)
);

CREATE TABLE IF NOT EXISTS Device_Instance (
	DEVICE_INSTANCE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	DEVICE_ID BIGINT,
	INSTANCE_ID INT,
	INSTANCE_NAME VARCHAR(50),
	COLUMN_NAME VARCHAR(50),
	CONSTRAINT DEVICE_INSTANCE_FK_DEVICE_ID FOREIGN KEY (DEVICE_ID) REFERENCES Device(DEVICE_ID)
);

CREATE TABLE IF NOT EXISTS Energy_Data (
	ENERGY_DATA_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	DEVICE_ID BIGINT,
	ADDED_TIME BIGINT,
	SOURCE INT,
	TOTAL_ENERGY_CONSUMPTION DOUBLE,
	TOTAL_ENERGY_CONSUMPTION_DELTA DOUBLE,
	LINE_VOLTAGE_R DOUBLE,
	LINE_VOLTAGE_Y DOUBLE,
	LINE_VOLTAGE_B DOUBLE,
	PHASE_VOLTAGE_R DOUBLE,
	PHASE_VOLTAGE_Y DOUBLE,
	PHASE_VOLTAGE_B DOUBLE,
	LINE_CURRENT_R DOUBLE,
	LINE_CURRENT_Y DOUBLE,
	LINE_CURRENT_B DOUBLE,
	POWER_FACTOR_R DOUBLE,
	POWER_FACTOR_Y DOUBLE,
	POWER_FACTOR_B DOUBLE,
	FREQUENCY_R DOUBLE,
	FREQUENCY_Y DOUBLE,
	FREQUENCY_B DOUBLE,
	ACTIVE_POWER_R DOUBLE,
	ACTIVE_POWER_Y DOUBLE,
	ACTIVE_POWER_B DOUBLE,
	REACTIVE_POWER_R DOUBLE,
	REACTIVE_POWER_Y DOUBLE,
	REACTIVE_POWER_B DOUBLE,
	APPARENT_POWER_R DOUBLE,
	APPARENT_POWER_Y DOUBLE,
	APPARENT_POWER_B DOUBLE,
	PHASE_ENERGY_R DOUBLE,
	PHASE_ENERGY_Y DOUBLE,
	PHASE_ENERGY_B DOUBLE,
	CONSTRAINT ENERGY_DATA_FK_DEVICE_ID FOREIGN KEY (DEVICE_ID) REFERENCES Device(DEVICE_ID)
);