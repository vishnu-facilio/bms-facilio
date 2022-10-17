CREATE TABLE IF NOT EXISTS Work_Assets (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	BASESPACE_ID BIGINT,
	ASSET_ID BIGINT,
	SAFETY_PLAN_ID BIGINT NOT NULL,
	SYS_CREATED_TIME  BIGINT,
    SYS_MODIFIED_TIME BIGINT,
    SYS_CREATED_BY    BIGINT,
    SYS_MODIFIED_BY   BIGINT,
	CONSTRAINT Work_Assets_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID),
	CONSTRAINT Work_Assets_FK_SYS_CREATED_BY FOREIGN KEY (SYS_CREATED_BY) REFERENCES ORG_Users(ORG_USERID),
    CONSTRAINT Work_Assets_FK_SYS_MODIFIED_BY FOREIGN KEY (SYS_MODIFIED_BY) REFERENCES ORG_Users(ORG_USERID),
	CONSTRAINT Work_Assets_FK_PARENT_BaseSpace_ID FOREIGN KEY (BASESPACE_ID) REFERENCES BaseSpace (ID) ON DELETE CASCADE,
	CONSTRAINT Work_Assets_FK_PARENT_ASSET_ID FOREIGN KEY (ASSET_ID) REFERENCES Assets (ID) ON DELETE CASCADE,
	CONSTRAINT Work_Assets_FK_HAZARD_ID FOREIGN KEY (SAFETY_PLAN_ID) REFERENCES Safety_Plan (ID) ON DELETE CASCADE
);