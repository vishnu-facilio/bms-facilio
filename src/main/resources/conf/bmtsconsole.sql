CREATE TABLE IF NOT EXISTS Event (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	SOURCE VARCHAR(100),
	NODE VARCHAR(100),
	ASSETID BIGINT,
	EVENT_TYPE VARCHAR(100),
	MESSAGE_KEY VARCHAR(300),
	SEVERITY VARCHAR(100),
	CREATED_TIME BIGINT,
	STATE VARCHAR(100),
	ALARM_ID BIGINT,
	DESCRIPTION VARCHAR(1000),
	ADDITIONAL_INFO VARCHAR(5000),
	CONSTRAINT EVENT_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT EVENT_FK_ASSETID FOREIGN KEY (ASSETID) REFERENCES Assets(ASSETID),
	CONSTRAINT EVENT_FK_ALARM_ID FOREIGN KEY (ALARM_ID) REFERENCES Alarms(ID)
);

CREATE TABLE IF NOT EXISTS Event_Property (
	EVENT_PROPERTY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	HASEVENTRULE BOOLEAN,
	HASMAPPINGRULE BOOLEAN,
	CONSTRAINT EVENT_PROPERTY_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Event_Rule (
	EVENT_RULE_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	HASEVENTFILTER BOOLEAN,
	FILTER_CRITERIAID BIGINT,
	HASCUSTOMIZERULE BOOLEAN,
	CUSTOMIZE_CRITERIAID BIGINT,
	ALARM_TEMPLATE_ID BIGINT,
	HASTHRESHOLDRULE BOOLEAN,
	CONSTRAINT EVENT_RULE_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT EVENT_RULE_FILTER_CRITERIAID_FK FOREIGN KEY (FILTER_CRITERIAID) REFERENCES Criteria(CRITERIAID),
	CONSTRAINT EVENT_RULE_ALARM_TEMPLATE_ID_FK FOREIGN KEY (ALARM_TEMPLATE_ID) REFERENCES Alarm_Template(ID)
);