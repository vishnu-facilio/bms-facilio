INSERT INTO Connected_App (ORGID, NAME, LINK_NAME) values (${orgId}, 'Events', 'event');
SET @CONNECTED_APP_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Event_Property (ORGID,HASEVENTRULE,HASMAPPINGRULE) values (${orgId},false,false);

INSERT INTO Tab_Widget (ORGID, MODULE_LINK_NAME, TAB_NAME, TAB_LINK_NAME, WIDGET_NAME, CONNECTED_APP_ID, RESOURCE_PATH) values (${orgId}, 'firealarm', 'Events', 'events', 'Events', @SKILL_MODULE_ID, 'api/event');

INSERT INTO Jobs (JOBID, JOBNAME, IS_ACTIVE, IS_PERIODIC, PERIOD, NEXT_EXECUTION_TIME, EXECUTOR_NAME) VALUES (${orgId}, 'EventSync', true, true, 60, UNIX_TIMESTAMP()+30,'priority');