-- Create Organizations Table
CREATE TABLE IF NOT EXISTS Organizations (
	ORGID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGNAME VARCHAR(100),
	FACILIODOMAINNAME VARCHAR(50),
	LOGO_ID BIGINT,
	PHONE CHAR(20),
	MOBILE CHAR(20),
	FAX CHAR(50),
	STREET CHAR(255),
  	CITY CHAR(255),
  	STATE CHAR(255),
  	ZIP CHAR(255),
  	COUNTRY CHAR(255),
  	TIMEZONE CHAR(100)
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS Users (
	USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(255),
	COGNITO_ID VARCHAR(100),
	USER_VERIFIED BOOLEAN,
	EMAIL VARCHAR(150),
	PHOTO_ID BIGINT
);

-- Create Roles Table
CREATE TABLE IF NOT EXISTS Role (
	ROLE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	NAME VARCHAR(50),
	DESCRIPTION VARCHAR(500),
	PERMISSIONS VARCHAR(50),
	CONSTRAINT ROLE_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

-- Create ORG_Users Table
CREATE TABLE IF NOT EXISTS ORG_Users (
	ORG_USERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	USERID BIGINT,
	ORGID BIGINT,
	INVITEDTIME BIGINT,
	ISDEFAULT BOOLEAN,
	USER_STATUS BOOLEAN DEFAULT TRUE,
	INVITATION_ACCEPT_STATUS BOOLEAN,
	ROLE_ID BIGINT,
	CONSTRAINT ORG_USERS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT ORG_USERS_FK_USERID FOREIGN KEY (USERID) REFERENCES Users(USERID),
	CONSTRAINT ORG_USERS_FK_ROLE_ID FOREIGN KEY (ROLE_ID) REFERENCES Role(ROLE_ID)
);

-- Create GLobal_Domain_Index
CREATE TABLE IF NOT EXISTS Global_Domain_Index (
	DOMAINNAME VARCHAR(50),
	ORGID BIGINT 
);

-- Create Requester Table
CREATE TABLE IF NOT EXISTS Requester (
	REQUESTER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	NAME VARCHAR(255),
	COGNITO_ID VARCHAR(100),
	USER_VERIFIED BOOLEAN,
	EMAIL VARCHAR(150),
	PORTAL_ACCESS BOOLEAN,
	CONSTRAINT REQUESTER_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
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

-- Create Jobs Table
CREATE TABLE IF NOT EXISTS System_Jobs (
	JOBID BIGINT,
	LAST_EXECUTION_TIME BIGINT,
	CONSTRAINT SYSTEM_JOBS_FK_JOBID FOREIGN KEY (JOBID) REFERENCES Jobs(JOBID)
);

-- Create Assets Table
CREATE TABLE IF NOT EXISTS Assets (
	ASSETID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(50),
	ORGID BIGINT,
	CONSTRAINT ASSETS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Groups (
	GROUPID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	GROUP_NAME CHAR(255),
	GROUP_EMAIL CHAR(255),
	GROUP_DESC TEXT(1000),
	IS_ACTIVE BOOLEAN,
	CREATED_TIME BIGINT,
	CREATED_BY BIGINT,
	PARENT BIGINT,
	CONSTRAINT GROUPS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS GroupMembers (
	MEMBERID BIGINT AUTO_INCREMENT PRIMARY KEY,
	GROUPID BIGINT,
	ORG_USERID BIGINT,
	MEMBER_ROLE INT,
	CONSTRAINT GROUP_MEMBERS_FK_ORGID FOREIGN KEY (GROUPID) REFERENCES Groups(GROUPID) ON DELETE CASCADE,
	CONSTRAINT GROUP_MEMBERS_FK_USERID FOREIGN KEY (ORG_USERID) REFERENCES ORG_Users(ORG_USERID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Modules (
	MODULEID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	NAME VARCHAR(25) NOT NULL,
	DISPLAY_NAME VARCHAR(25),
	TABLE_NAME VARCHAR(25),
	CONSTRAINT MODULES_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT MODULES_UNQIUE UNIQUE(ORGID, NAME)
);

CREATE TABLE IF NOT EXISTS Fields (
	FIELDID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	NAME VARCHAR(25),
	DISPLAY_NAME VARCHAR(25),
	DISPLAY_TYPE TINYINT,
	COLUMN_NAME VARCHAR(25),
	SEQUENCE_NUMBER TINYINT,
	DATA_TYPE TINYINT,
	IS_DEFAULT BOOLEAN,
	IS_MAIN_FIELD BOOLEAN,
	REQUIRED BOOLEAN,
	DISABLED BOOLEAN,
	STYLE_CLASS VARCHAR(50),
	ICON VARCHAR(20),
	PLACE_HOLDER VARCHAR(50),
	CONSTRAINT FIELDS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT FIELDS_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT FIELDS_NAME_UNIQUE UNIQUE (ORGID, MODULEID, NAME)
);

CREATE TABLE IF NOT EXISTS LookupFields (
	FIELDID BIGINT PRIMARY KEY,
	LOOKUP_MODULE_ID BIGINT,
	SPECIAL_TYPE VARCHAR(25),
	CONSTRAINT LOOKUP_FIELDS_FK_FIELDID FOREIGN KEY (FIELDID) REFERENCES Fields(FIELDID),
	CONSTRAINT LOOKUP_FEILDS_FK_MODULE_ID FOREIGN KEY (LOOKUP_MODULE_ID) REFERENCES Modules(MODULEID)
);

CREATE TABLE IF NOT EXISTS Locations (
  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORGID BIGINT NOT NULL,
  MODULEID BIGINT NOT NULL,
  NAME CHAR(255),
  STREET CHAR(255),
  CITY CHAR(255),
  STATE CHAR(255),
  ZIP CHAR(255),
  COUNTRY CHAR(255),
  LAT DECIMAL(10, 8),
  LNG DECIMAL(11, 8),
  CONTACT_ID BIGINT NULL,
  PHONE CHAR(100),
  FAX_PHONE CHAR(100),
  CONSTRAINT LOCATIONS_ORGID_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
  CONSTRAINT LOCATIONS_CONTACT_FK FOREIGN KEY (CONTACT_ID) REFERENCES ORG_Users (ORG_USERID)
);

CREATE TABLE IF NOT EXISTS BaseSpace (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	CONSTRAINT SPACE_BASE_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Campus (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	BASE_SPACE_ID BIGINT NOT NULL,
	LOCATION_ID BIGINT,
	MODULEID BIGINT NOT NULL,
	NAME VARCHAR(50),
	CURRENT_OCCUPANCY BIGINT,
	MAX_OCCUPANCY BIGINT,
	AREA BIGINT,
	MANAGED_BY BIGINT,
	DESCRIPTION VARCHAR(1000),
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT CAMPUS_FK_BASE_SPACE_ID FOREIGN KEY (BASE_SPACE_ID) REFERENCES BaseSpace(ID),
	CONSTRAINT CAMPUS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT CAMPUS_FK_MANAGED_BY FOREIGN KEY (MANAGED_BY) REFERENCES ORG_Users (ORG_USERID),
	CONSTRAINT CAMPUS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT CAMPUS_FK_LOCATION_ID FOREIGN KEY (LOCATION_ID) REFERENCES Locations (ID)
);

CREATE TABLE IF NOT EXISTS Building (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	BASE_SPACE_ID BIGINT NOT NULL,
	LOCATION_ID BIGINT,
	MODULEID BIGINT NOT NULL,
	NAME VARCHAR(50),
	CAMPUS_ID BIGINT,
	FLOORS BIGINT,
	AREA BIGINT,
	CURRENT_OCCUPANCY BIGINT,
	MAX_OCCUPANCY BIGINT,
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT BUILDING_FK_BASE_SPACE_ID FOREIGN KEY (BASE_SPACE_ID) REFERENCES BaseSpace(ID),
	CONSTRAINT BUILDING_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT BUILDING_FK_CAMPUS_ID FOREIGN KEY (CAMPUS_ID) REFERENCES Campus (ID),
	CONSTRAINT BUILDING_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT BUILDING_FK_LOCATION_ID FOREIGN KEY (LOCATION_ID) REFERENCES Locations(ID)
);

CREATE TABLE IF NOT EXISTS Floor (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	BASE_SPACE_ID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	NAME VARCHAR(50),
	BUILDING_ID BIGINT,
	MAIN_LEVEL BOOLEAN,
	AREA BIGINT,
	CURRENT_OCCUPANCY BIGINT,
	MAX_OCCUPANCY BIGINT,
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT FLOOR_FK_BASE_SPACE_ID FOREIGN KEY (BASE_SPACE_ID) REFERENCES BaseSpace(ID),
	CONSTRAINT FLOOR_FK_BUILDING_ID FOREIGN KEY (BUILDING_ID) REFERENCES Building(ID),
	CONSTRAINT FLOOR_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT FLOOR_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Space_Category (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	NAME VARCHAR(50),
	DESCRIPTION VARCHAR(1000),
	CONSTRAINT SPACE_CATEGORY_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT SPACE_CATEGORY_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Space (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	BASE_SPACE_ID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	DISPLAY_NAME VARCHAR(50),
	NAME VARCHAR(50),
	BUILDING_ID BIGINT,
	FLOOR_ID BIGINT,
	AREA BIGINT,
	AVAILABILITY BIGINT,
	CURRENT_OCCUPANCY BIGINT,
	MAX_OCCUPANCY BIGINT,
	OCCUPIABLE BOOLEAN,
	SPACE_CATEGORY_ID BIGINT,
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT SPACE_FK_BASE_SPACE_ID FOREIGN KEY (BASE_SPACE_ID) REFERENCES BaseSpace(ID),
	CONSTRAINT SPACE_FK_BUILDING_ID FOREIGN KEY (BUILDING_ID) REFERENCES Building(ID),
	CONSTRAINT SPACE_FK_FLOOR_ID FOREIGN KEY (FLOOR_ID) REFERENCES Floor(ID),
	CONSTRAINT SPACE_FK_CATEGORY_ID FOREIGN KEY (SPACE_CATEGORY_ID) REFERENCES Space_Category(ID),
	CONSTRAINT SPACE_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT SPACE_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Zone (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	NAME VARCHAR(50),
	SHORT_DESCRIPTION VARCHAR(500),
	CONSTRAINT ZONE_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT ZONE_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID)
);

CREATE TABLE IF NOT EXISTS Zone_Space (
	ZONE_ID BIGINT,
	BASE_SPACE_ID BIGINT,
	CONSTRAINT ZONE_SPACE_FK_ZONE_ID FOREIGN KEY (ZONE_ID) REFERENCES Zone(ID),
	CONSTRAINT ZONE_SPACE_FK_BASE_SPACE_ID FOREIGN KEY (BASE_SPACE_ID) REFERENCES BaseSpace(ID)
);

-- Create Notes Table
CREATE TABLE IF NOT EXISTS Notes (
	NOTEID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	OWNERID BIGINT,
	CREATION_TIME BIGINT,
	TITLE VARCHAR(50),
	BODY VARCHAR(1000),
	CONSTRAINT NOTES_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT NOTES_FK_OWNER_ID FOREIGN KEY (OWNERID) REFERENCES ORG_Users(ORG_USERID)
);

-- Create Tickets Tables
CREATE TABLE IF NOT EXISTS TicketStatus (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	STATUS VARCHAR(20) NOT NULL,
	STATUS_TYPE TINYINT NOT NULL,
	CONSTRAINT TICKET_STATUS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT TICKET_STATUS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT TICKET_STATUS_UNIQUE UNIQUE (ORGID, STATUS)
);

CREATE TABLE IF NOT EXISTS TicketStateFlow (
	ACTIVITY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ACTIVITY_NAME VARCHAR(20) NOT NULL,
	STATE_ID BIGINT NOT NULL,
	NEXT_STATE_ID BIGINT NOT NULL,
	CONSTRAINT TICKET_STATEFLOW_FK_STATE FOREIGN KEY (STATE_ID) REFERENCES TicketStatus(ID),
	CONSTRAINT TICKET_STATEFLOW_FK_NEXT_STATE FOREIGN KEY (STATE_ID) REFERENCES TicketStatus(ID),
	CONSTRAINT TICKET_STATEFLOW_UNIQUE UNIQUE (STATE_ID, NEXT_STATE_ID)
);


CREATE TABLE IF NOT EXISTS TicketPriority (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	PRIORITY VARCHAR(20) NOT NULL,
	SEQUENCE_NUMBER TINYINT NOT NULL,
	CONSTRAINT TICKET_PRIORITY_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT TICKET_PRIORITY_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT TICKET_PRIORITY_UNIQUE UNIQUE (ORGID, PRIORITY)
);

CREATE TABLE IF NOT EXISTS TicketCategory (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	NAME VARCHAR(50),
	DESCRIPTION VARCHAR(1000),
	CONSTRAINT TICKET_CATEGORY_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT TICKET_CATEGORY_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Tickets (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	SUBJECT VARCHAR(50),
	DESCRIPTION VARCHAR(1000),
	STATUS_ID BIGINT,
	PRIORITY_ID BIGINT,
	CATEGORY_ID BIGINT,
	SOURCE_TYPE TINYINT,
	ASSIGNMENT_GROUP_ID BIGINT,
	ASSIGNED_TO_ID BIGINT,
	ASSET_ID BIGINT,
	SPACE_ID BIGINT,
	DUE_DATE BIGINT,
	SERIAL_NUMBER BIGINT,
	NO_OF_NOTES INT,
	NO_OF_ATTACHMENTS INT,
	NO_OF_TASKS INT,
	NO_OF_CLOSED_TASKS INT,
	SCHEDULED_START BIGINT,
	ESTIMATED_END BIGINT,
	ACTUAL_WORK_START BIGINT,
	ACTUAL_WORK_END BIGINT,
	CONSTRAINT TICKETS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT TICKETS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT TICKETS_FK_STATUS_ID FOREIGN KEY (STATUS_ID) REFERENCES TicketStatus(ID),
	CONSTRAINT TICKETS_FK_PRIORITY_ID FOREIGN KEY (PRIORITY_ID) REFERENCES TicketPriority(ID),
	CONSTRAINT TICKETS_FK_CATEGORY_ID FOREIGN KEY (CATEGORY_ID) REFERENCES TicketCategory(ID),
	CONSTRAINT TICKETS_FK_ASSIGNMENT_GROUP FOREIGN KEY (ASSIGNMENT_GROUP_ID) REFERENCES Groups(GROUPID),
	CONSTRAINT TICKETS_FK_ASSIGNED_TO_ID FOREIGN KEY (ASSIGNED_TO_ID) REFERENCES ORG_Users(ORG_USERID),
	CONSTRAINT TICKETS_FK_ASSETID FOREIGN KEY (ASSET_ID) REFERENCES Assets(ASSETID),
	CONSTRAINT TICKETS_FK_SPACE_ID FOREIGN KEY (SPACE_ID) REFERENCES BaseSpace(ID)
);

-- Create Note-Taskt relationship table
CREATE TABLE IF NOT EXISTS Ticket_Note (
	TICKET_NOTE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	TICKET_ID BIGINT,
	NOTEID BIGINT,
	CONSTRAINT TICKET_NOTES_FK_TICKET_ID FOREIGN KEY (TICKET_ID) REFERENCES Tickets(ID),
	CONSTRAINT TICKET_NOTES_FK_NOTE_ID FOREIGN KEY (NOTEID) REFERENCES Notes(NOTEID)
);

CREATE TABLE IF NOT EXISTS WorkOrders (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	TICKET_ID BIGINT NOT NULL,
	REQUESTER_ID BIGINT,
	CREATED_TIME BIGINT,
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT WORK_ORDERS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT WORK_ORDERS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT WORK_ORDERS_FK_TICKET_ID FOREIGN KEY (TICKET_ID) REFERENCES Tickets(ID),
	CONSTRAINT WORK_ORDERS_FK_REQUESTER_ID FOREIGN KEY (REQUESTER_ID) REFERENCES Requester(REQUESTER_ID)
);

CREATE TABLE IF NOT EXISTS WorkOrderRequests (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	TICKET_ID BIGINT NOT NULL,
	REQUESTER_ID BIGINT,
	STATUS TINYINT,
	URGENCY TINYINT,
	CREATED_TIME BIGINT,
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT WORK_ORDER_REQUESTS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT WORK_ORDER_REQUESTS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT WORK_ORDER_REQUESTS_FK_TICKET_ID FOREIGN KEY (TICKET_ID) REFERENCES Tickets(ID),
	CONSTRAINT WORK_ORDER_REQUESTS_FK_REQUESTER_ID FOREIGN KEY (REQUESTER_ID) REFERENCES Requester(REQUESTER_ID)
);

-- Create table for WorkOrderRequests creation from Email
CREATE TABLE IF NOT EXISTS WorkOrderRequest_EMail (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	S3_MESSAGE_ID VARCHAR(100) NOT NULL,
	IS_PROCESSED BOOLEAN
);

CREATE TABLE IF NOT EXISTS Tasks (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	PARENT_TICKET_ID BIGINT,
	TICKET_ID BIGINT NOT NULL,
	CREATED_TIME BIGINT,
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT TASKS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT TASKS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT TASKS_FK_PARENT_TICKET_ID FOREIGN KEY (PARENT_TICKET_ID) REFERENCES Tickets(ID),
	CONSTRAINT TASKS_FK_TICKET_ID FOREIGN KEY (TICKET_ID) REFERENCES Tickets(ID)
);

CREATE TABLE IF NOT EXISTS UserLocationCoverage (
  USER_LOCATION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORG_USERID BIGINT,
  LOCATION_ID BIGINT,
  CONSTRAINT USERLOCATIONS_USERID_FK FOREIGN KEY (ORG_USERID) REFERENCES ORG_Users(ORG_USERID) ON DELETE CASCADE,
  CONSTRAINT USERLOCATIONS_LOCATION_FK FOREIGN KEY (LOCATION_ID) REFERENCES Locations (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS GroupLocationCoverage (
  GROUP_LOCATION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  GROUPID BIGINT,
  LOCATION_ID BIGINT,
  CONSTRAINT GLC_GROUPID_FK FOREIGN KEY (GROUPID) REFERENCES Groups(GROUPID) ON DELETE CASCADE,
  CONSTRAINT GLC_LOCATION_FK FOREIGN KEY (LOCATION_ID) REFERENCES Locations (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Skills (
  ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORGID BIGINT NOT NULL,
  MODULEID BIGINT NOT NULL,
  NAME VARCHAR(255),
  ACTIVE BOOLEAN,
  DESCRIPTION VARCHAR(1000),
  CONSTRAINT SKILLS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
  CONSTRAINT SKILLS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID)
);

CREATE TABLE IF NOT EXISTS UserSkills (
  USER_SKILL_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORG_USERID BIGINT,
  SKILL_ID BIGINT,
  CONSTRAINT USERSKILLS_USERID_FK FOREIGN KEY (ORG_USERID) REFERENCES ORG_Users(ORG_USERID) ON DELETE CASCADE,
  CONSTRAINT USERSKILLS_SKILL_FK FOREIGN KEY (SKILL_ID) REFERENCES Skills (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS GroupSkills (
  GROUP_SKILL_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  GROUPID BIGINT,
  SKILL_ID BIGINT,
  CONSTRAINT GROUPSKILLS_GROUPID_FK FOREIGN KEY (GROUPID) REFERENCES Groups(GROUPID) ON DELETE CASCADE,
  CONSTRAINT GROUPSKILLS_SKILL_FK FOREIGN KEY (SKILL_ID) REFERENCES Skills (ID) ON DELETE CASCADE
);

-- Device Table

CREATE TABLE IF NOT EXISTS Service (
	SERVICE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	NAME VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS Controller (
	CONTROLLER_ID BIGINT,
	CONTROLLER_TYPE INT,
	IP_ADDRESS VARCHAR(50),
	POLL_TIME INT,
	JOBID BIGINT,
	JOB_STATUS BOOLEAN,
	STATUS INT,
	CONSTRAINT CONTROLLER_FK_ASSET_ID FOREIGN KEY (CONTROLLER_ID) REFERENCES Assets(ASSETID),
	CONSTRAINT CONTROLLER_FK_JOBID FOREIGN KEY (JOBID) REFERENCES Jobs(JOBID)
);

CREATE TABLE IF NOT EXISTS Device (
	DEVICE_ID BIGINT,
	SERVICE_ID BIGINT,
	ZONE_ID BIGINT,
	SPACE_ID BIGINT,
	CONTROLLER_ID BIGINT,
	PARENT_DEVICE_ID BIGINT,
	DEVICE_TYPE INT,
	STATUS INT,
	CONSTRAINT DEVICE_FK_DEVICE_ID FOREIGN KEY (DEVICE_ID) REFERENCES Assets(ASSETID),
	CONSTRAINT DEVICE_FK_SERVICE_ID FOREIGN KEY (SERVICE_ID) REFERENCES Service(SERVICE_ID),
	CONSTRAINT DEVICE_FK_ZONE_ID FOREIGN KEY (ZONE_ID) REFERENCES Zone(ID),
	CONSTRAINT DEVICE_FK_PARENT_DEVICE_ID FOREIGN KEY (PARENT_DEVICE_ID) REFERENCES Device(DEVICE_ID),
	CONSTRAINT DEVICE_FK_SPACE_ID FOREIGN KEY (SPACE_ID) REFERENCES BaseSpace(ID),
	CONSTRAINT DEVICE_FK_CONTROLLER_ID FOREIGN KEY (CONTROLLER_ID) REFERENCES Controller(CONTROLLER_ID)
);

CREATE TABLE IF NOT EXISTS Alarms (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	TICKET_ID BIGINT NOT NULL,
	DEVICE_ID BIGINT NOT NULL,
	STATUS TINYINT,
	IS_ACKNOWLEDGED BOOLEAN,
	ALARM_TYPE TINYINT,
	CREATED_TIME BIGINT,
	STRING_CF1 VARCHAR(50),
	STRING_CF2 VARCHAR(50),
	STRING_CF3 VARCHAR(50),
	STRING_CF4 VARCHAR(50),
	STRING_CF5 VARCHAR(50),
	NUMBER_CF1 BIGINT,
	NUMBER_CF2 BIGINT,
	NUMBER_CF3 BIGINT,
	NUMBER_CF4 BIGINT,
	NUMBER_CF5 BIGINT,
	DECIMAL_CF1 DOUBLE(9,3),
	DECIMAL_CF2 DOUBLE(9,3),
	DECIMAL_CF3 DOUBLE(9,3),
	DECIMAL_CF4 DOUBLE(9,3),
	DECIMAL_CF5 DOUBLE(9,3),
	BOOLEAN_CF1 BOOLEAN,
	BOOLEAN_CF2 BOOLEAN,
	BOOLEAN_CF3 BOOLEAN,
	BOOLEAN_CF4 BOOLEAN,
	BOOLEAN_CF5 BOOLEAN,
	DATE_CF1 BIGINT,
	DATE_CF2 BIGINT,
	DATE_CF3 BIGINT,
	DATE_CF4 BIGINT,
	DATE_CF5 BIGINT,
	DATETIME_CF1 BIGINT,
	DATETIME_CF2 BIGINT,
	DATETIME_CF3 BIGINT,
	DATETIME_CF4 BIGINT,
	DATETIME_CF5 BIGINT,
	CONSTRAINT ALARMS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT ALARMS_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT ALARMS_FK_TICKET_ID FOREIGN KEY (TICKET_ID) REFERENCES Tickets(ID),
	CONSTRAINT ALARMS_FK_DEVICE_ID FOREIGN KEY (DEVICE_ID) REFERENCES Device(DEVICE_ID)
);

CREATE TABLE IF NOT EXISTS AlarmFollowers (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ALARM_ID BIGINT NOT NULL,
	FOLLOWER_TYPE VARCHAR(50),
	FOLLOWER VARCHAR(255),
	CONSTRAINT ALARM_FOLLOWERS_FK_ALARMID FOREIGN KEY (ALARM_ID) REFERENCES Alarms(ID)
);

CREATE TABLE IF NOT EXISTS Controller_Instance (
	CONTROLLER_INSTANCE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	CONTROLLER_ID BIGINT,
	DEVICE_ID BIGINT,
	INSTANCE_ID INT,
	INSTANCE_NAME VARCHAR(50),
	COLUMN_NAME VARCHAR(50),
	CONSTRAINT DEVICE_INSTANCE_FK_CONTROLLER_ID FOREIGN KEY (CONTROLLER_ID) REFERENCES Controller(CONTROLLER_ID),
	CONSTRAINT DEVICE_INSTANCE_FK_DEVICE_ID FOREIGN KEY (DEVICE_ID) REFERENCES Device(DEVICE_ID)
);

CREATE TABLE IF NOT EXISTS Unmodelled_Data (
	UNMODELLED_DATA_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	CONTROLLER_INSTANCE_ID BIGINT,
	ADDED_TIME BIGINT,
	INSTANCE_VALUE DOUBLE,
	CONSTRAINT UNMODELLED_DATA_FK_CONTROLLER_INSTANCE_ID FOREIGN KEY (CONTROLLER_INSTANCE_ID) REFERENCES Controller_Instance(CONTROLLER_INSTANCE_ID)
);

CREATE TABLE IF NOT EXISTS Energy_Data (
	ENERGY_DATA_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	DEVICE_ID BIGINT,
	ADDED_TIME BIGINT,
	TOTAL_ENERGY_CONSUMPTION DOUBLE,
	TOTAL_ENERGY_CONSUMPTION_DELTA DOUBLE DEFAULT 0,
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
	PHASE_ENERGY_R_DELTA DOUBLE,
	PHASE_ENERGY_Y DOUBLE,
	PHASE_ENERGY_Y_DELTA DOUBLE,
	PHASE_ENERGY_B DOUBLE,
	PHASE_ENERGY_B_DELTA DOUBLE,
	ADDED_DATE DATE,
	ADDED_MONTH INT,
	ADDED_WEEK INT,
	ADDED_DAY VARCHAR(100),
	ADDED_HOUR INT,
	
	CONSTRAINT ENERGY_DATA_FK_DEVICE_ID FOREIGN KEY (DEVICE_ID) REFERENCES Device(DEVICE_ID)
);
-- Create indexes for Energy_Data table
CREATE INDEX time_Index on Energy_Data (ADDED_TIME);
CREATE INDEX date_Index on Energy_Data (ADDED_DATE);
CREATE INDEX device_Index on Energy_Data (DEVICE_ID);

-- Trigger change starts..
-- Create trigger for calculating TOTAL_ENERGY_CONSUMPTION_DELTA in Energy_Data table
DELIMITER $$
CREATE TRIGGER calculate_delta BEFORE INSERT ON Energy_Data FOR EACH ROW
BEGIN
DECLARE PREV_ENERGY_CONSUMPTION DOUBLE;
DECLARE PREV_PHASE_ENERGY_R DOUBLE;
DECLARE PREV_PHASE_ENERGY_Y DOUBLE;
DECLARE PREV_PHASE_ENERGY_B DOUBLE;
SELECT TOTAL_ENERGY_CONSUMPTION, PHASE_ENERGY_R , PHASE_ENERGY_Y , PHASE_ENERGY_B INTO PREV_ENERGY_CONSUMPTION, PREV_PHASE_ENERGY_R, PREV_PHASE_ENERGY_Y , 
PREV_PHASE_ENERGY_B FROM Energy_Data WHERE DEVICE_ID = NEW.DEVICE_ID ORDER BY ADDED_TIME DESC LIMIT 1;
IF PREV_ENERGY_CONSUMPTION IS NOT NULL THEN 
SET NEW.TOTAL_ENERGY_CONSUMPTION_DELTA = NEW.TOTAL_ENERGY_CONSUMPTION - PREV_ENERGY_CONSUMPTION, 
NEW.PHASE_ENERGY_R_DELTA = NEW.PHASE_ENERGY_R - PREV_PHASE_ENERGY_R, NEW.PHASE_ENERGY_Y_DELTA = NEW.PHASE_ENERGY_Y - PREV_PHASE_ENERGY_Y, 
NEW.PHASE_ENERGY_B_DELTA = NEW.PHASE_ENERGY_B - PREV_PHASE_ENERGY_B;
END IF;
END; $$
DELIMITER ;
-- Trigger change Ends..

CREATE TABLE IF NOT EXISTS File (
  FILE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ORGID BIGINT NOT NULL,
  FILE_NAME CHAR(255),
  FILE_PATH CHAR(255),
  FILE_SIZE BIGINT,
  CONTENT_TYPE CHAR(255),
  UPLOADED_BY BIGINT,
  UPLOADED_TIME BIGINT,
  CONSTRAINT FILE_ORGID_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

-- Create File-Ticket relationship table
CREATE TABLE IF NOT EXISTS Ticket_Attachment (
	TICKET_ATTACHMENT_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	TICKET_ID BIGINT,
	FILE_ID BIGINT,
	CONSTRAINT TICKET_ATTACHMENT_FK_TID FOREIGN KEY (TICKET_ID) REFERENCES Tickets(ID),
	CONSTRAINT TICKET_ATTACHMENT_FK_FID FOREIGN KEY (FILE_ID) REFERENCES File(FILE_ID)
);

CREATE TABLE IF NOT EXISTS Criteria (
	CRITERIAID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	PATTERN VARCHAR(100),
	CONSTRAINT CRITERIA_ORGID_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Conditions (
	CONDITIONID BIGINT AUTO_INCREMENT PRIMARY KEY,
	PARENT_CRITERIA_ID BIGINT NOT NULL,
	SEQUENCE TINYINT NOT NULL,
	FIELDID BIGINT NOT NULL,
	OPERATOR VARCHAR(15) NOT NULL,
	VAL VARCHAR(50),
	CRITERIA_VAL_ID  BIGINT,
	COMPUTED_WHERE_CLAUSE VARCHAR(100),
	CONSTRAINT CONDITIONS_PARENT_CRITERIA_ID_FK FOREIGN KEY (PARENT_CRITERIA_ID) REFERENCES Criteria(CRITERIAID),
	CONSTRAINT CONDITIONS_CRITERIA_VAL_ID_FK FOREIGN KEY (CRITERIA_VAL_ID) REFERENCES Criteria(CRITERIAID),
	CONSTRAINT CONDITIONS_FIELDID_FK FOREIGN KEY (FIELDID) REFERENCES Fields(FIELDID)
);

CREATE TABLE IF NOT EXISTS Views (
	VIEWID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	NAME VARCHAR(50) NOT NULL,
	DISPLAY_NAME VARCHAR(50),
	MODULEID BIGINT NOT NULL,
	CRITERIAID BIGINT NOT NULL,
	CONSTRAINT VIEWS_ORGID_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT VIEWS_MODULEID_FK FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT VIEWS_CRITERIAID_FK FOREIGN KEY (CRITERIAID) REFERENCES Criteria(CRITERIAID)
);

CREATE TABLE IF NOT EXISTS ImportProcess (
	IMPORTID_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORG_USERID BIGINT,
	INSTANCE_ID INT,
	STATUS INT,
	COLUMN_HEADING VARCHAR(5000),
	FILE_PATH VARCHAR(100),
	FILEID BIGINT,
	FILE_PATH_FAILED VARCHAR(100),
	FIELD_MAPPING VARCHAR(5000),
	IMPORT_TIME BIGINT,
	IMPORT_TYPE INT,
	CONSTRAINT IMPORT_ORGUSERS_FK_ORGID FOREIGN KEY (ORG_USERID) REFERENCES ORG_Users(ORG_USERID),
	CONSTRAINT IMPORT_ORGFILE_FK_ORGID FOREIGN KEY (FILEID) REFERENCES File(FILE_ID)
);

CREATE TABLE IF NOT EXISTS Event (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	EVENT_TYPE INT,
	CONSTRAINT EVENT_ORGID_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT EVENT_MODULEID_FK FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID)
);

CREATE TABLE IF NOT EXISTS Action (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	ACTION_TYPE INT,
	TEMPLATE_TYPE INT,
	TEMPLATE_ID BIGINT,
	CONSTRAINT ACTION_ORGID_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT ACTION_FILEID_FK FOREIGN KEY (TEMPLATE_ID) REFERENCES File(FILE_ID)
);

CREATE TABLE IF NOT EXISTS Workflow_Rule (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	NAME VARCHAR(100),
	DESCRIPTION VARCHAR(1000),
	EVENT_ID BIGINT NOT NULL,
	CRITERIAID BIGINT,
	EXECUTION_ORDER INT,
	STATUS BOOLEAN,
	RULE_TYPE INT,
	CONSTRAINT WORKFLOW_RULE_ORGID_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT WORKFLOW_RULE_EVENT_ID_FK FOREIGN KEY (EVENT_ID) REFERENCES Event(ID),
	CONSTRAINT WORKFLOW_RULE_CRITERIAID_FK FOREIGN KEY (CRITERIAID) REFERENCES Criteria(CRITERIAID)
);

CREATE TABLE IF NOT EXISTS Workflow_Rule_Action (
	WORKFLOW_RULE_ACTION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	WORKFLOW_RULE_ID BIGINT NOT NULL,
	ACTION_ID BIGINT NOT NULL,
	CONSTRAINT WORKFLOW_ACTION_RULE_WORKFLOW_ID_FK FOREIGN KEY (WORKFLOW_RULE_ID) REFERENCES Workflow_Rule(ID),
	CONSTRAINT WORKFLOW_ACTION_RULE_ACTION_ID_FK FOREIGN KEY (ACTION_ID) REFERENCES Action(ID)

);

CREATE TABLE IF NOT EXISTS SupportEmails (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	REPLY_NAME VARCHAR(50),
	ACTUAL_EMAIL VARCHAR(50) UNIQUE NOT NULL,
	FWD_EMAIL VARCHAR(50) UNIQUE NOT NULL,
	AUTO_ASSIGN_GROUP_ID BIGINT,
	VERIFIED BOOLEAN,
	PRIMARY_SUPPORT_EMAIL BOOLEAN,
	CONSTRAINT SUPPORT_EMAILS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT SUPPORT_EMAILS_FK_AUTO_ASSIGN_GROUP FOREIGN KEY (AUTO_ASSIGN_GROUP_ID) REFERENCES Groups(GROUPID)
);

CREATE TABLE IF NOT EXISTS EmailSettings (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT UNIQUE NOT NULL,
	BCC_EMAIL VARCHAR(50),
	FLAGS TINYINT,
	CONSTRAINT EMAIL_SETTINGS_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

-- service portal --
CREATE TABLE IF NOT EXISTS PortalInfo (
	PORTALID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	PORTALTYPE INT DEFAULT 0,
	SIGNUP_ALLOWED BOOLEAN,
	GMAILLOGIN_ALLOWED BOOLEAN,
	IS_PUBLIC_CREATE_ALLOWED BOOLEAN,
	IS_ANYDOMAIN_ALLOWED BOOLEAN,
	WHITELISTED_DOMAINS VARCHAR(255),
	SAML_ENABLED BOOLEAN,
	LOGIN_URL VARCHAR(50),
	LOGOUT_URL VARCHAR(50),
	PASSWORD_URL VARCHAR(50),
	UNIQUE KEY PortalInfo_type (ORGID,PORTALTYPE),
	CONSTRAINT PortalInfo_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

-- Triggers
DELIMITER $$
CREATE TRIGGER generateTicketSerialNumber BEFORE INSERT
ON Tickets FOR EACH ROW
BEGIN
DECLARE MAX_SERIAL_NUMBER INT;
SELECT COALESCE(MAX(SERIAL_NUMBER),0) INTO MAX_SERIAL_NUMBER FROM Tickets WHERE ORGID = NEW.ORGID;
SET NEW.SERIAL_NUMBER = MAX_SERIAL_NUMBER + 1;
END; $$

CREATE TRIGGER incrementTicketNoteCount AFTER INSERT
ON Ticket_Note FOR EACH ROW
BEGIN
UPDATE Tickets SET NO_OF_NOTES = COALESCE(NO_OF_NOTES,0)+1 WHERE ID = NEW.TICKET_ID;		
END; $$

CREATE TRIGGER ticketNoteCascadeDelete BEFORE DELETE
ON Notes FOR EACH ROW
BEGIN
DELETE FROM Ticket_Note WHERE NOTEID = OLD.NOTEID;		
END; $$

CREATE TRIGGER decrementTicketNoteCount AFTER DELETE
ON Ticket_Note FOR EACH ROW
BEGIN
UPDATE Tickets SET NO_OF_NOTES = NO_OF_NOTES-1 WHERE ID = OLD.TICKET_ID;		
END; $$

CREATE TRIGGER incrementTicketAttachmentCount AFTER INSERT
ON Ticket_Attachment FOR EACH ROW
BEGIN
UPDATE Tickets SET NO_OF_ATTACHMENTS = COALESCE(NO_OF_ATTACHMENTS,0)+1 WHERE ID = NEW.TICKET_ID;		
END; $$

CREATE TRIGGER ticketAttachmentCascadeDelete BEFORE DELETE
ON File FOR EACH ROW
BEGIN
DELETE FROM Ticket_Attachment WHERE FILE_ID = OLD.FILE_ID;		
END; $$

CREATE TRIGGER decrementTicketAttachmentCount AFTER DELETE
ON Ticket_Attachment FOR EACH ROW 
BEGIN
UPDATE Tickets SET NO_OF_ATTACHMENTS = NO_OF_ATTACHMENTS-1 WHERE ID = OLD.TICKET_ID;		
END; $$

CREATE TRIGGER incrementTaskCount AFTER INSERT
ON Tasks FOR EACH ROW
BEGIN
DECLARE TICKET_STATUS_TYPE TINYINT;
UPDATE Tickets SET NO_OF_TASKS = COALESCE(NO_OF_TASKS,0)+1 WHERE ID = NEW.PARENT_TICKET_ID;
SELECT STATUS_TYPE INTO TICKET_STATUS_TYPE FROM TicketStatus INNER JOIN Tickets ON TicketStatus.ID = Tickets.STATUS_ID WHERE Tickets.ID = NEW.TICKET_ID;
IF TICKET_STATUS_TYPE = 2 THEN
UPDATE Tickets SET NO_OF_CLOSED_TASKS = COALESCE(NO_OF_CLOSED_TASKS,0)+1 WHERE ID = NEW.PARENT_TICKET_ID;
END IF;
END; $$

CREATE TRIGGER decrementTaskCount AFTER DELETE
ON Tasks FOR EACH ROW
BEGIN
DECLARE TICKET_STATUS_TYPE TINYINT;
UPDATE Tickets SET NO_OF_TASKS = NO_OF_TASKS-1 WHERE ID = OLD.PARENT_TICKET_ID;
SELECT STATUS_TYPE INTO TICKET_STATUS_TYPE FROM TicketStatus INNER JOIN Tickets ON TicketStatus.ID = Tickets.STATUS_ID WHERE Tickets.ID = OLD.TICKET_ID;
IF TICKET_STATUS_TYPE = 2 THEN
UPDATE Tickets SET NO_OF_CLOSED_TASKS = NO_OF_CLOSED_TASKS-1 WHERE ID = OLD.PARENT_TICKET_ID;
END IF;
END; $$

DELIMITER ;

-- Insert Queries
-- Add entry in Jobs for workorderemail parser
INSERT INTO Jobs (JOBNAME, ISPERIODIC, PERIOD, NEXTEXECUTIONTIME, EXECUTORNAME) VALUES ('WorkOrderRequestEmailParser', true, 300, UNIX_TIMESTAMP()+30,'system');