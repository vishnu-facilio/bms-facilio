INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME,IS_PREVILEGED_ROLE) VALUES(${orgId}, 'Administrator', 'Administrator', UNIX_TIMESTAMP(now()) *1000, true);
SET @ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME) VALUES(${orgId}, 'Tenant User', 'Tenant User', UNIX_TIMESTAMP(now()) *1000);
SET @TENANT_ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME) VALUES(${orgId}, 'Occupant User', 'Occupant User', UNIX_TIMESTAMP(now()) *1000);
SET @OCCUPANT_ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME) VALUES(${orgId}, 'Client User', 'Client User', UNIX_TIMESTAMP(now()) *1000);
SET @CLIENT_ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME) VALUES(${orgId}, 'Vendor User', 'Vendor User', UNIX_TIMESTAMP(now()) *1000);
SET @VENDOR_ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME,IS_PREVILEGED_ROLE) VALUES(${orgId}, 'Operations Admin', 'Operations Admin', UNIX_TIMESTAMP(now()) *1000, true);
SET @OPERATIONS_ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME,IS_PREVILEGED_ROLE) VALUES(${orgId}, 'Agent Admin', 'Agent Admin', UNIX_TIMESTAMP(now()) *1000, true);
SET @AGENT_ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());
INSERT INTO Role (ORGID, NAME, DESCRIPTION, CREATED_TIME, IS_PREVILEGED_ROLE) VALUES(${orgId}, 'Dev Admin', 'Dev Admin', UNIX_TIMESTAMP(now()) *1000, true);
SET @DEV_ADMIN_ROLE_ID := (SELECT LAST_INSERT_ID());


INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME) VALUES(${orgId}, 'Manager', 'Manager', UNIX_TIMESTAMP(now()) *1000);
SET @ADD_ROLE_ID := (SELECT LAST_INSERT_ID());

INSERT INTO Role (ORGID, NAME, DESCRIPTION,CREATED_TIME) VALUES(${orgId}, 'Technician', 'Technician', UNIX_TIMESTAMP(now()) *1000);
SET @ADD_ROLE_ID := (SELECT LAST_INSERT_ID());


INSERT INTO License(ORGID,LICENSE,TOTAL_LICENSE) VALUES (${orgId},1,50);
INSERT INTO License(ORGID,LICENSE,TOTAL_LICENSE) VALUES (${orgId},2,50);
INSERT INTO License(ORGID,LICENSE,TOTAL_LICENSE) VALUES (${orgId},3,50);
INSERT INTO License(ORGID,LICENSE,TOTAL_LICENSE) VALUES (${orgId},4,50);
INSERT INTO FeatureLicense(ORGID,MODULE) VALUES (${orgId}, 17592263656527);