INSERT INTO SupportEmails (ORGID, REPLY_NAME, ACTUAL_EMAIL, FWD_EMAIL, VERIFIED) VALUES (${orgId}, (SELECT ORGNAME FROM Organizations WHERE ORGID = ${orgId}), CONCAT('support@',CONCAT((SELECT FACILIODOMAINNAME FROM Organizations WHERE ORGID = ${orgId}),'.facilio.me')), CONCAT('support@',CONCAT((SELECT FACILIODOMAINNAME FROM Organizations WHERE ORGID = ${orgId}),'.facilio.me')), true);

INSERT INTO EmailSettings (ORGID, FLAGS) VALUES (${orgId}, 2);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'location', 'Locations', 'Locations');
SET @LOCATION_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @LOCATION_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, false, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'street', 'Street', 1, 'STREET', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'city', 'City', 3, 'CITY', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'state', 'State / Province', 3, 'STATE', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'zip', 'Zip / Postal Code', 1, 'ZIP', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'country', 'Country', 3, 'COUNTRY', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'lat', 'Latitude', 9, 'LAT', 3, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'lng', 'Longitude', 9, 'LNG', 3, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'contact', 'Contact', 11, 'CONTACT_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'users');
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'phone', 'Phone', 1, 'PHONE', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @LOCATION_MODULE_ID, 'faxPhone', 'Fax Phone', 1, 'FAX_PHONE', 1, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'skill', 'Skills', 'Skills');
SET @SKILL_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @SKILL_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, false, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SKILL_MODULE_ID, 'description', 'Description', 2, 'DESCRIPTION', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SKILL_MODULE_ID, 'active', 'Active', 5, 'ACTIVE', 4, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'campus', 'Campus', 'Campus');
SET @CAMPUS_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'baseSpaceId', 'Base Space ID', 9, 'BASE_SPACE_ID', 2, true, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 9, 'CURRENT_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 9, 'MAX_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'area', 'Area', 9, 'AREA', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'location', 'Location', 11, 'LOCATION_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @LOCATION_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'managedBy', 'Managed By', 11, 'MANAGED_BY', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'users');
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'description', 'Description', 2, 'DESCRIPTION', 1, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @CAMPUS_MODULE_ID, 'photoId', 'Photo', 9, 'PHOTO_ID', 2, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'building', 'Building', 'Building');
SET @BUILDING_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'baseSpaceId', 'Base Space ID', 9, 'BASE_SPACE_ID', 2, true, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @BUILDING_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'campus', 'Campus', 11, 'CAMPUS_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @CAMPUS_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'floors', 'Floors', 9, 'FLOORS', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'location', 'Location', 11, 'LOCATION_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @LOCATION_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'area', 'Area', 9, 'AREA', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 9, 'CURRENT_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 9, 'MAX_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @BUILDING_MODULE_ID, 'photoId', 'Photo', 9, 'PHOTO_ID', 2, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'floor', 'Floor', 'Floor');
SET @FLOOR_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @FLOOR_MODULE_ID, 'baseSpaceId', 'Base Space ID', 9, 'BASE_SPACE_ID', 2, true, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @FLOOR_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @FLOOR_MODULE_ID, 'building', 'Building', 11, 'BUILDING_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @BUILDING_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @FLOOR_MODULE_ID, 'mainLevel', 'Main Level', 5, 'MAIN_LEVEL', 4, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @FLOOR_MODULE_ID, 'area', 'Area', 9, 'AREA', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @FLOOR_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 9, 'CURRENT_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @FLOOR_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 9, 'MAX_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @FLOOR_MODULE_ID, 'photoId', 'Photo', 9, 'PHOTO_ID', 2, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'spacecategory', 'Space Category', 'Space_Category');
SET @SPACE_CATEGORY_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @SPACE_CATEGORY_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_CATEGORY_MODULE_ID, 'description', 'Description', 2, 'DESCRIPTION', 1, true, false, true);

INSERT INTO Space_Category (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @SPACE_CATEGORY_MODULE_ID, 'SC1', 'DESCRIPTION1');
INSERT INTO Space_Category (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @SPACE_CATEGORY_MODULE_ID, 'SC2', 'DESCRIPTION2');
INSERT INTO Space_Category (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @SPACE_CATEGORY_MODULE_ID, 'SC3', 'DESCRIPTION3');

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'space', 'Space', 'Space');
SET @SPACE_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'baseSpaceId', 'Base Space ID', 9, 'BASE_SPACE_ID', 2, true, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @SPACE_MODULE_ID, 'displayName', 'Display Name', 1, 'DISPLAY_NAME', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, true, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'building', 'Building', 11, 'BUILDING_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @BUILDING_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'floor', 'Floor', 11, 'FLOOR_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @FLOOR_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'area', 'Area', 9, 'AREA', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'spaceCategory', 'Category', 10, 'SPACE_CATEGORY_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @SPACE_CATEGORY_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'availability', 'Availability', 9, 'AVAILABILITY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'currentOccupancy', 'Current Occupancy', 9, 'CURRENT_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'maxOccupancy', 'Max Occupancy', 9, 'MAX_OCCUPANCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'occupiable', 'Occupiable', 5, 'OCCUPIABLE', 4, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @SPACE_MODULE_ID, 'photoId', 'Photo', 9, 'PHOTO_ID', 2, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'zone', 'Zone', 'Zone');
SET @ZONE_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @ZONE_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ZONE_MODULE_ID, 'shortDescription', 'Short Description', 2, 'SHORT_DESCRIPTION', 1, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'ticketstatus', 'Ticket Status', 'TicketStatus');
SET @TICKET_STATUS_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'status', 'Status', 1, 'STATUS', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'typeCode', 'Type', 9, 'STATUS_TYPE', 2, true, false, true);

INSERT INTO TicketStatus (ORGID, MODULEID, STATUS, STATUS_TYPE) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'Submitted', 1);
SET @TICKET_STATUS_SUBMIT := (SELECT LAST_INSERT_ID());
INSERT INTO TicketStatus (ORGID, MODULEID, STATUS, STATUS_TYPE) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'Assigned', 1);
SET @TICKET_STATUS_ASSIGNED := (SELECT LAST_INSERT_ID());
INSERT INTO TicketStatus (ORGID, MODULEID, STATUS, STATUS_TYPE) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'Work in Progress', 1);
SET @TICKET_STATUS_WIP := (SELECT LAST_INSERT_ID());
INSERT INTO TicketStatus (ORGID, MODULEID, STATUS, STATUS_TYPE) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'Resolved', 1);
SET @TICKET_STATUS_RESOLVED := (SELECT LAST_INSERT_ID());
INSERT INTO TicketStatus (ORGID, MODULEID, STATUS, STATUS_TYPE) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'Closed', 2);
SET @TICKET_STATUS_CLOSED := (SELECT LAST_INSERT_ID());
INSERT INTO TicketStatus (ORGID, MODULEID, STATUS, STATUS_TYPE) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'On Hold', 1);
SET @TICKET_STATUS_ONHOLD := (SELECT LAST_INSERT_ID());
INSERT INTO TicketStatus (ORGID, MODULEID, STATUS, STATUS_TYPE) VALUES (${orgId}, @TICKET_STATUS_MODULE_ID, 'Incomplete', 1);
SET @TICKET_STATUS_INCOMPLETE := (SELECT LAST_INSERT_ID());

insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('Assign',@TICKET_STATUS_SUBMIT,@TICKET_STATUS_ASSIGNED);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('Resolve',@TICKET_STATUS_ASSIGNED,@TICKET_STATUS_RESOLVED);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('Resolve',@TICKET_STATUS_WIP,@TICKET_STATUS_RESOLVED);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('begin',@TICKET_STATUS_ASSIGNED,@TICKET_STATUS_WIP);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('begin',@TICKET_STATUS_SUBMIT,@TICKET_STATUS_WIP);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('close',@TICKET_STATUS_RESOLVED,@TICKET_STATUS_CLOSED);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('hold',@TICKET_STATUS_WIP,@TICKET_STATUS_ONHOLD);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('hold',@TICKET_STATUS_ASSIGNED,@TICKET_STATUS_ONHOLD);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('Re-Open',@TICKET_STATUS_CLOSED,@TICKET_STATUS_SUBMIT);
insert into TicketStateFlow (ACTIVITY_NAME,STATE_ID,NEXT_STATE_ID) VALUES ('Re-Open',@TICKET_STATUS_CLOSED,@TICKET_STATUS_ASSIGNED);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'ticketpriority', 'Ticket Priority', 'TicketPriority');
SET @TICKET_PRIORITY_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @TICKET_PRIORITY_MODULE_ID, 'priority', 'Priority', 1, 'PRIORITY', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_PRIORITY_MODULE_ID, 'sequenceNumber', 'Sequence', 1, 'SEQUENCE_NUMBER', 1, true, false, true);

INSERT INTO TicketPriority (ORGID, MODULEID, PRIORITY, SEQUENCE_NUMBER) VALUES (${orgId}, @TICKET_PRIORITY_MODULE_ID, 'High',1);
INSERT INTO TicketPriority (ORGID, MODULEID, PRIORITY, SEQUENCE_NUMBER) VALUES (${orgId}, @TICKET_PRIORITY_MODULE_ID, 'Medium',2);
INSERT INTO TicketPriority (ORGID, MODULEID, PRIORITY, SEQUENCE_NUMBER) VALUES (${orgId}, @TICKET_PRIORITY_MODULE_ID, 'Low',3);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'ticketcategory', 'Ticket Category', 'TicketCategory');
SET @TICKET_CATEGORY_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @TICKET_CATEGORY_MODULE_ID, 'name', 'Name', 1, 'NAME', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_CATEGORY_MODULE_ID, 'description', 'Description', 2, 'DESCRIPTION', 1, true, false, true);

INSERT INTO TicketCategory (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @TICKET_CATEGORY_MODULE_ID, 'Electrical', 'DESCRIPTION1');
INSERT INTO TicketCategory (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @TICKET_CATEGORY_MODULE_ID, 'Fire Safety', 'DESCRIPTION2');
INSERT INTO TicketCategory (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @TICKET_CATEGORY_MODULE_ID, 'House Keeping', 'DESCRIPTION3');
INSERT INTO TicketCategory (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @TICKET_CATEGORY_MODULE_ID, 'HVAC', 'DESCRIPTION4');
INSERT INTO TicketCategory (ORGID, MODULEID, NAME, DESCRIPTION) VALUES (${orgId}, @TICKET_CATEGORY_MODULE_ID, 'Plumbing', 'DESCRIPTION5');

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME) VALUES (${orgId}, 'ticket', 'Tickets', 'Tickets');
SET @TICKET_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT, IS_MAIN_FIELD) VALUES (${orgId}, @TICKET_MODULE_ID, 'subject', 'Subject', 1, 'SUBJECT', 1, true, false, true, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, PLACE_HOLDER, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'description', 'Description', 2, 'DESCRIPTION', 1, true, false, "More about the problem...", true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'status', 'Status', 10, 'STATUS_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @TICKET_STATUS_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'priority', 'Priority', 10, 'PRIORITY_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @TICKET_PRIORITY_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'category', 'Category', 10, 'CATEGORY_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @TICKET_CATEGORY_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'sourceType', 'Source Type', 9, 'SOURCE_TYPE', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'assignmentGroup', 'Assignment Group', 11, 'ASSIGNMENT_GROUP_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'groups');
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'assignedTo', 'Assigned To', 11, 'ASSIGNED_TO_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'users');
SET @TICKET_USER_FIELD_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'assetId', 'Asset', 9, 'ASSET_ID', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'space', 'Space', 11, 'SPACE_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'basespace');
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'dueDate', 'Due Date', 7, 'DUE_DATE', 6, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'serialNumber', 'Serial Number', 9, 'SERIAL_NUMBER', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'noOfNotes', 'Comments', 9, 'NO_OF_NOTES', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'noOfAttachments', 'Attachments', 9, 'NO_OF_ATTACHMENTS', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'noOfTasks', 'Tasks', 9, 'NO_OF_TASKS', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'noOfClosedTasks', 'Closed Tasks', 9, 'NO_OF_CLOSED_TASKS', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'scheduledStart', 'Scheduled Start', 7, 'SCHEDULED_START', 6, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'estimatedEnd', 'Estimated End', 7, 'ESTIMATED_END', 6, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'actualWorkStart', 'Actual Work Start', 7, 'ACTUAL_WORK_START', 6, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TICKET_MODULE_ID, 'actualWorkEnd', 'Actual Work End', 7, 'ACTUAL_WORK_END', 6, false, false, true);


INSERT INTO PortalInfo (ORGID,PORTALTYPE) VALUES (${orgId},0);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME, EXTENDS_ID) VALUES (${orgId}, 'workorder', 'Work Orders', 'WorkOrders', @TICKET_MODULE_ID);
SET @WORK_ORDER_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_MODULE_ID, 'ticket', 'Ticket', 1, 'TICKET_ID', 7, true, false, true);
SET @WORK_ORDER_TICKET_FIELD_ID := (SELECT LAST_INSERT_ID());
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES (@WORK_ORDER_TICKET_FIELD_ID, @TICKET_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_MODULE_ID, 'requester', 'Requester', 11, 'REQUESTER_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'requester');
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_MODULE_ID, 'createdTime', 'Created Time', 7, 'CREATED_TIME', 6, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME, EXTENDS_ID) VALUES (${orgId}, 'workorderrequest', 'Work Order Requests', 'WorkOrderRequests', @TICKET_MODULE_ID);
SET @WORK_ORDER_REQUEST_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_REQUEST_MODULE_ID, 'ticket', 'Ticket', 1, 'TICKET_ID', 7, true, false, true);
SET @WORK_ORDER_REQUEST_TICKET_FIELD_ID := (SELECT LAST_INSERT_ID());
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES (@WORK_ORDER_REQUEST_TICKET_FIELD_ID, @TICKET_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_REQUEST_MODULE_ID, 'requester', 'Requester', 11, 'REQUESTER_ID', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'requester');
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_REQUEST_MODULE_ID, 'status', 'Status', 9, 'STATUS', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_REQUEST_MODULE_ID, 'urgency', 'Urgency', 9, 'URGENCY', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @WORK_ORDER_REQUEST_MODULE_ID, 'createdTime', 'Created Time', 7, 'CREATED_TIME', 6, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME, EXTENDS_ID) VALUES (${orgId}, 'task', 'Tasks', 'Tasks', @TICKET_MODULE_ID);
SET @TASK_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TASK_MODULE_ID, 'ticket', 'Ticket', 1, 'TICKET_ID', 7, true, true, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @TICKET_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TASK_MODULE_ID, 'parentTicketId', 'Parent', 1, 'PARENT_TICKET_ID', 2, false, true, true);
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES ((SELECT LAST_INSERT_ID()), @WORK_ORDER_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @TASK_MODULE_ID, 'createdTime', 'Created Time', 7, 'CREATED_TIME', 6, false, false, true);

INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME, EXTENDS_ID) VALUES (${orgId}, 'alarm', 'Alarms', 'Alarms', @TICKET_MODULE_ID);
SET @ALARM_MODULE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'ticket', 'Ticket', 1, 'TICKET_ID', 7, true, false, true);
SET @ALARM_TICKET_FIELD_ID := (SELECT LAST_INSERT_ID());
INSERT INTO LookupFields (FIELDID, LOOKUP_MODULE_ID) VALUES (@ALARM_TICKET_FIELD_ID, @TICKET_MODULE_ID);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'status', 'Status', 9, 'STATUS', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'type', 'Type', 9, 'ALARM_TYPE', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'isAcknowledged', 'Is Acknowledged', 5, 'IS_ACKNOWLEDGED', 4, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'acknowledgedBy', 'Acknowledged By', 11, 'ACKNOWLEDGED_BY', 7, false, false, true);
INSERT INTO LookupFields (FIELDID, SPECIAL_TYPE) VALUES ((SELECT LAST_INSERT_ID()), 'users');
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'acknowledgedTime', 'Acknowledged Time', 7, 'ACKNOWLEDGED_TIME', 6, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'deviceId', 'Device', 9, 'DEVICE_ID', 2, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'createdTime', 'Created Time', 7, 'CREATED_TIME', 6, false, false, true);
INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, DISPLAY_TYPE, COLUMN_NAME, DATA_TYPE, REQUIRED, DISABLED, IS_DEFAULT) VALUES (${orgId}, @ALARM_MODULE_ID, 'clearedTime', 'Cleared Time', 7, 'CLEARED_TIME', 6, false, false, true);

-- Workflow Rules
INSERT INTO Criteria (ORGID, PATTERN) VALUES (${orgId}, '1');
SET @MY_TICKET_CRITERIA_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Conditions (PARENT_CRITERIA_ID, SEQUENCE, FIELDID, OPERATOR) VALUES (@MY_TICKET_CRITERIA_ID, 1, @TICKET_USER_FIELD_ID, 'is not empty');
INSERT INTO Criteria (ORGID, PATTERN) VALUES (${orgId}, '1');
SET @MY_WORKORDER_CRITERIA_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Conditions (PARENT_CRITERIA_ID, SEQUENCE, FIELDID, OPERATOR, CRITERIA_VAL_ID) VALUES (@MY_WORKORDER_CRITERIA_ID, 1, @WORK_ORDER_TICKET_FIELD_ID, 'lookup', @MY_TICKET_CRITERIA_ID);
INSERT INTO Workflow_Event (ORGID, MODULEID, EVENT_TYPE) VALUES (${orgId}, @WORK_ORDER_MODULE_ID, 1);
SET @ADD_WORKORDER_EVENT_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule (ORGID, NAME, DESCRIPTION, EVENT_ID, CRITERIAID, EXECUTION_ORDER, STATUS, RULE_TYPE) VALUES (${orgId}, 'Assign Workorder', 'Assign Workorder', @ADD_WORKORDER_EVENT_ID, @MY_WORKORDER_CRITERIA_ID, 1, true, 1);
SET @ASSIGN_WORKORDER_WORKFLOW_RULE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Action (ORGID, ACTION_TYPE, DEFAULT_TEMPLATE_ID) VALUES (${orgId}, 1, 1);
SET @SEND_EMAIL_ACTION_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule_Action (WORKFLOW_RULE_ID, ACTION_ID) VALUES (@ASSIGN_WORKORDER_WORKFLOW_RULE_ID, @SEND_EMAIL_ACTION_ID);

INSERT INTO Workflow_Event (ORGID, MODULEID, EVENT_TYPE) VALUES (${orgId}, @WORK_ORDER_MODULE_ID, 16);
SET @ADD_WORKORDER_EVENT_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule (ORGID, NAME, DESCRIPTION, EVENT_ID, CRITERIAID, EXECUTION_ORDER, STATUS, RULE_TYPE) VALUES (${orgId}, 'Assign Workorder', 'Assign Workorder', @ADD_WORKORDER_EVENT_ID, @MY_WORKORDER_CRITERIA_ID, 1, true, 1);
SET @ASSIGN_WORKORDER_WORKFLOW_RULE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule_Action (WORKFLOW_RULE_ID, ACTION_ID) VALUES (@ASSIGN_WORKORDER_WORKFLOW_RULE_ID, @SEND_EMAIL_ACTION_ID);

INSERT INTO Workflow_Event (ORGID, MODULEID, EVENT_TYPE) VALUES (${orgId}, @TICKET_MODULE_ID, 32);
SET @ADD_TICKET_COMMENT_EVENT_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule (ORGID, NAME, DESCRIPTION, EVENT_ID, EXECUTION_ORDER, STATUS, RULE_TYPE) VALUES (${orgId}, 'Ticket Comment', 'Comment added in Ticket', @ADD_TICKET_COMMENT_EVENT_ID, 1, true, 1);
SET @TICKET_COMMENT_WORKFLOW_RULE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Action (ORGID, ACTION_TYPE, DEFAULT_TEMPLATE_ID) VALUES (${orgId}, 1, 4);
SET @SEND_EMAIL_ACTION_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule_Action (WORKFLOW_RULE_ID, ACTION_ID) VALUES (@TICKET_COMMENT_WORKFLOW_RULE_ID, @SEND_EMAIL_ACTION_ID);

INSERT INTO Workflow_Event (ORGID, MODULEID, EVENT_TYPE) VALUES (${orgId}, @ALARM_MODULE_ID, 1);
SET @ADD_ALARM_EVENT_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule (ORGID, NAME, DESCRIPTION, EVENT_ID, EXECUTION_ORDER, STATUS, RULE_TYPE) VALUES (${orgId}, 'Create Alarm - EMail', 'Create Alarm', @ADD_ALARM_EVENT_ID, 1, true, 1);
SET @ADD_ALARM_WORKFLOW_RULE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Action (ORGID, ACTION_TYPE, DEFAULT_TEMPLATE_ID) VALUES (${orgId}, 3, 5);
SET @SEND_BULK_EMAIL_ACTION_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule_Action (WORKFLOW_RULE_ID, ACTION_ID) VALUES (@ADD_ALARM_WORKFLOW_RULE_ID, @SEND_BULK_EMAIL_ACTION_ID);

INSERT INTO Workflow_Rule (ORGID, NAME, DESCRIPTION, EVENT_ID, EXECUTION_ORDER, STATUS, RULE_TYPE) VALUES (${orgId}, 'Create Alarm - SMS', 'Create Alarm', @ADD_ALARM_EVENT_ID, 1, true, 1);
SET @ADD_ALARM_WORKFLOW_RULE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Action (ORGID, ACTION_TYPE, DEFAULT_TEMPLATE_ID) VALUES (${orgId}, 4, 6);
SET @SEND_BULK_SMS_ACTION_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Workflow_Rule_Action (WORKFLOW_RULE_ID, ACTION_ID) VALUES (@ADD_ALARM_WORKFLOW_RULE_ID, @SEND_BULK_SMS_ACTION_ID);