CREATE TABLE IF NOT EXISTS Event_Property (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	IS_EVENT_ENABLED BOOLEAN,
	EVENTS_TOPIC_NAME VARCHAR(50),
	CONSTRAINT EVENT_PROPERTY_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Event_Rule (
	EVENT_RULE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	RULE_ORDER INT NOT NULL,
	BASE_CRITERIAID BIGINT NOT NULL,
	IGNORE_EVENT BOOLEAN,
	TRANSFORM_CRITERIAID BIGINT,
	TRANSFORM_ALARM_TEMPLATE_ID BIGINT,
	THRESHOLD_CRITERIAID BIGINT,
	THRESHOLD_OCCURS INT,
	THRESHOLD_OVER_SECONDS INT,
	CO_REL_WORKFLOW_ID BIGINT,
	CO_REL_ACTION TINYINT,
	CO_REL_TRANSFORM_TEMPLATE_ID BIGINT,
	CONSTRAINT EVENT_RULE_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT EVENT_RULE_BASE_CRITERIAID_FK FOREIGN KEY (BASE_CRITERIAID) REFERENCES Criteria(CRITERIAID),
	CONSTRAINT EVENT_RULE_TRANSFORM_CRITERIAID_FK FOREIGN KEY (TRANSFORM_CRITERIAID) REFERENCES Criteria(CRITERIAID),
	CONSTRAINT EVENT_RULE_THRESHOLD_CRITERIAID_FK FOREIGN KEY (THRESHOLD_CRITERIAID) REFERENCES Criteria(CRITERIAID),
	CONSTRAINT EVENT_RULE_TRANSFORM_TEMPLATE_ID_FK FOREIGN KEY (TRANSFORM_ALARM_TEMPLATE_ID) REFERENCES JSON_Template(ID),
	CONSTRAINT EVENT_RULE_CO_REL_TRANSFORM_TEMPLATE_ID_FK FOREIGN KEY (CO_REL_TRANSFORM_TEMPLATE_ID) REFERENCES JSON_Template(ID),
	CONSTRAINT EVENT_RULE_CO_REL_WORKFLOW_ID_FK FOREIGN KEY (CO_REL_WORKFLOW_ID) REFERENCES Workflow(ID)
);

CREATE TABLE IF NOT EXISTS Event_Rules (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	DESCRIPTION VARCHAR(1000),
	CRITERIA_ID BIGINT,
	WORKFLOW_ID BIGINT,
	EXECUTION_ORDER INT NOT NULL,
	SUCCESS_ACTION TINYINT NOT NULL,
	TRANSFORM_TEMPLATE_ID BIGINT,
	IS_ACTIVE BOOLEAN,
	CONSTRAINT EVENT_RULES_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT EVENT_RULES_FK_CRITERIA_ID FOREIGN KEY (CRITERIA_ID) REFERENCES Criteria(CRITERIAID),
	CONSTRAINT EVENT_RULES_FK_WORKFLOW_ID FOREIGN KEY (WORKFLOW_ID) REFERENCES Workflow(ID),
	CONSTRAINT EVENT_RULES_FK_TRANSFORM_TEMPLATE_ID FOREIGN KEY (TRANSFORM_TEMPLATE_ID) REFERENCES Templates(ID)
);

CREATE TABLE IF NOT EXISTS Event (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	SOURCE VARCHAR(100),
	NODE VARCHAR(100),
	RESOURCE_ID BIGINT,
	EVENT_MESSAGE VARCHAR(500),
	MESSAGE_KEY VARCHAR(1000),
	SEVERITY VARCHAR(100),
	CREATED_TIME BIGINT,
	EVENT_STATE TINYINT,
	INTERNAL_STATE TINYINT,
	EVENT_RULE_ID BIGINT,
	ALARM_ID BIGINT,
	PRIORITY VARCHAR(100),
	ALARM_CLASS VARCHAR(100),
	STATE VARCHAR(100),
	DESCRIPTION VARCHAR(1000),
	ADDITIONAL_INFO VARCHAR(10000),
	CONSTRAINT EVENT_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT EVENT_FK_RESOURCE_ID FOREIGN KEY (RESOURCE_ID) REFERENCES Resources(ID),
	CONSTRAINT EVENT_FK_ALARM_ID FOREIGN KEY (ALARM_ID) REFERENCES Alarms(ID),
	CONSTRAINT EVENT_FK_EVENT_RULE_ID FOREIGN KEY (EVENT_RULE_ID) REFERENCES Event_Rule(EVENT_RULE_ID)
);

CREATE TABLE IF NOT EXISTS Event_To_Alarm_Field_Mapping (
	EVENT_TO_ALARM_FIELD_MAPPING_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	TRANSFORM_TYPE TINYINT NOT NULL,
	FROM_FIELD VARCHAR(100),
	TO_FIELD VARCHAR(100),
	CONSTANT_VALUE VARCHAR(100),
	MAPPING_ORDER INT NOT NULL,
	MAPPING_PAIRS VARCHAR(1000),
	CONSTRAINT EVENT_FIELD_MAPPING_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Node_To_Resource_Mapping (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	NODE VARCHAR(100),
	RESOURCE_ID BIGINT,
	CONSTRAINT NODE_ASSET_MAPPING_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT NODE_ASSET_MAPPING_FK_RESOURCE_ID FOREIGN KEY (RESOURCE_ID) REFERENCES Resources(ID),
	CONSTRAINT NODE_ASSET_MAPPING_UNIQUE_NODE UNIQUE(ORGID, NODE)
);
  
  DELIMITER $$
  DROP TRIGGER IF EXISTS generateEventCount $$
  CREATE TRIGGER generateEventCount BEFORE UPDATE
  ON Event FOR EACH ROW
  BEGIN
  IF OLD.ALARM_ID IS NULL AND NEW.ALARM_ID IS NOT NULL THEN
  UPDATE Alarms SET NO_OF_EVENTS = COALESCE(NO_OF_EVENTS,0)+1 WHERE ID = NEW.ALARM_ID;
  END IF;
  END; $$
  DELIMITER ;