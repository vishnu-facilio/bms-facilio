CREATE TABLE IF NOT EXISTS UtilityProvider (
	PROVIDER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	ORGID BIGINT,
	NAME VARCHAR(100),
	DISPLAYNAME VARCHAR(50),
  	COUNTRY CHAR(255),
  	CONSTRAINT PROVIDER_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

CREATE TABLE IF NOT EXISTS Building_Provider (
	BUILDING_PROVIDER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	PROVIDER_ID BIGINT,
	BUILDING_ID BIGINT,
	CONSTRAINT BUILDING_PROVIDER_FK_PROVIDER_ID FOREIGN KEY (PROVIDER_ID) REFERENCES UtilityProvider(PROVIDER_ID),
	CONSTRAINT BUILDING_PROVIDER_FK_BUILDING_ID FOREIGN KEY (BUILDING_ID) REFERENCES Building(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ElectricalEnergy (
	ELECTRICAL_ENERGY_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	PROVIDER_ID BIGINT,
	ADDED_DATE BIGINT,
	KWH DOUBLE,
  	CONSTRAINT ENERGY_PROVIDER_FK_PROVIDER_ID FOREIGN KEY (PROVIDER_ID) REFERENCES UtilityProvider(PROVIDER_ID)
);

CREATE TABLE IF NOT EXISTS ConsumptionInfo (
	ID BIGINT AUTO_INCREMENT PRIMARY KEY,
	CONSUMPTION_ID BIGINT,
	ENERGY_DATA_ID BIGINT,
	START_DATE BIGINT,
	ADDED_DATE BIGINT,
	CONSTRAINT CONSUMPTIONINFO_FK_ENERGY_DATA_ID FOREIGN KEY (ENERGY_DATA_ID) REFERENCES Energy_Data(ENERGY_DATA_ID) ON DELETE CASCADE
);
