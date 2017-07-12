INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME) VALUES (${orgId}, 'Tickets', 'Tickets');
SET @TICKET_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'ticketId', 'Ticket ID', 'TICKETID', 0);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'requester', 'Requester', 'REQUESTER', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'subject', 'Subject', 'SUBJECT', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'description', 'Description', 'DESCRIPTION', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'statusCode', 'Status', 'STATUS', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'assignedToId', 'Assigned To ID', 'ASSIGNED_TO_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'assetId', 'Asset ID', 'ASSET_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 'dueDate', 'Due Date', 'DUE_DATE', 7);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME) VALUES (${orgId}, 'Tasks', 'Tasks');
SET @TASK_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TASK_MODULE_ID, 'taskId', 'Task ID', 'TASKID', 0);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TASK_MODULE_ID, 'parent', 'Parent', 'PARENT', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TASK_MODULE_ID, 'subject', 'Subject', 'SUBJECT', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TASK_MODULE_ID, 'description', 'Description', 'DESCRIPTION', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TASK_MODULE_ID, 'assignmentGroupId', 'Assignment Group ID', 'ASSIGNMENT_GROUP_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TASK_MODULE_ID, 'assignedToId', 'Assigned To ID', 'ASSIGNED_TO_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @TASK_MODULE_ID, 'scheduleId', 'Schedule ID', 'SCHEDULE_ID', 3);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME) VALUES (${orgId}, 'Campus', 'Campus');
SET @CAMPUS_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'campusId', 'Campus ID', 'CAMPUS_ID', 0);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'name', 'Name', 'NAME', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 'CURRENT_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 'MAX_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'percentOccupied', 'Percent Occupied', 'PERCENT_OCCUPIED', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'grossArea', 'Gross Area', 'GROSS_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'usableArea', 'Usable Area', 'USABLE_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'assignableArea', 'Assignable Area', 'ASSIGNABLE_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'areaUnit', 'Area Unit', 'AREA_UNIT', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'locationId', 'Location ID', 'LOCATION_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'managedBy', 'Managed By', 'MANAGED_BY', 3);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME) VALUES (${orgId}, 'Building', 'Building');
SET @BUILDING_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'buildingId', 'Building ID', 'BUILDING_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'name', 'Name', 'NAME', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'campusId', 'Campus ID', 'CAMPUS_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'floors', 'Floors', 'FLOORS', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'locationId', 'Location ID', 'LOCATION_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'assignableArea', 'Assignable Area', 'ASSIGNABLE_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'usableArea', 'Usable Area', 'USABLE_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'grossArea', 'Gross Area', 'GROSS_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'areaUnit', 'Area Unit', 'AREA_UNIT', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 'CURRENT_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 'MAX_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'percentOccupied', 'Percent Occupied', 'PERCENT_OCCUPIED', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'utilizationMin', 'Utilization Min', 'UTILIZATION_MIN', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @BUILDING_MODULE_ID, 'utilizationMax', 'Utilization Max', 'UTILIZATION_MAX', 2);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME) VALUES (${orgId}, 'Floor', 'Floor');
SET @FLOOR_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'floorId', 'Floor ID', 'FLOOR_ID', 0);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'name', 'Name', 'NAME', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'buildingId', 'Building ID', 'BUILDING_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'mainLevel', 'Main Level', 'MAIN_LEVEL', 5);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'assignableArea', 'Assignable Area', 'ASSIGNABLE_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'usableArea', 'Usable Area', 'USABLE_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'grossArea', 'Gross Area', 'GROSS_AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'areaUnit', 'Area Unit', 'AREA_UNIT', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 'CURRENT_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 'MAX_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'percentOccupied', 'Percent Occupied', 'PERCENT_OCCUPIED', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'utilizationMin', 'Utilization Min', 'UTILIZATION_MIN', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @FLOOR_MODULE_ID, 'utilizationMax', 'Utilization Max', 'UTILIZATION_MAX', 2);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME) VALUES (${orgId}, 'Space', 'Space');
SET @SPACE_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'spaceId', 'Space ID', 'SPACE_ID', 0);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'displayName', 'Display Name', 'DISPLAY_NAME', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'name', 'Name', 'NAME', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'buildingId', 'Building ID', 'BUILDING_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'floorId', 'Floor ID', 'FLOOR_ID', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'area', 'Area', 'AREA', 3);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'areaUnit', 'Area Unit', 'AREA_UNIT', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'status', 'Status', 'STATUS', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'availability', 'availability', 'AVAILABILITY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 'CURRENT_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 'MAX_OCCUPANCY', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'percentOccupied', 'Percent Occupied', 'PERCENT_OCCUPIED', 2);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @SPACE_MODULE_ID, 'occupiable', 'Occupiable', 'OCCUPIABLE', 5);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME) VALUES (${orgId}, 'Zone', 'Zone');
SET @ZONE_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @ZONE_MODULE_ID, 'zoneId', 'Zone ID', 'ZONE_ID', 0);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @ZONE_MODULE_ID, 'name', 'Name', 'NAME', 1);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, DATA_TYPE) VALUES (${orgId}, @ZONE_MODULE_ID, 'shortDescription', 'Short Description', 'SHORT_DESCRIPTION', 1);