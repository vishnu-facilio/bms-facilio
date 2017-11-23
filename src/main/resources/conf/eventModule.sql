INSERT INTO Connected_App (ORGID, NAME, LINK_NAME) values (${orgId}, 'Events', 'event');
SET @CONNECTED_APP_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Tab_Widget (ORGID, MODULE_LINK_NAME, TAB_NAME, TAB_LINK_NAME, WIDGET_NAME, CONNECTED_APP_ID, RESOURCE_PATH) values (${orgId}, 'firealarm', 'Events', 'events', 'Events', @CONNECTED_APP_ID, 'api/event');

INSERT INTO Event_Property (ORGID, EVENTS_TOPIC_NAME) VALUES (${orgId}, 'FacilioEvents');

--INSERT INTO Criteria (ORGID, PATTERN) VALUES (${orgId}, '1');
--SET @CRITERIA_ID := (SELECT LAST_INSERT_ID());
--INSERT INTO Conditions (PARENT_CRITERIA_ID, SEQUENCE, FIELD_NAME, COLUMN_NAME, OPERATOR, VAL) VALUES (@CRITERIA_ID, 1, 'severity', 'Event.SEVERITY', 3, 'Idle');
--INSERT INTO Event_Rule (ORGID,NAME,RULE_ORDER,IGNORE_EVENT,BASE_CRITERIAID) values (${orgId},'Ignore Event', 1, true, @CRITERIA_ID);