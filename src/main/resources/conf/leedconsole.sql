CREATE TABLE IF NOT EXISTS LeedConfiguration (
	ID BIGINT PRIMARY KEY,
	ORGID BIGINT NOT NULL,
	MODULEID BIGINT NOT NULL,
	LEEDID BIGINT,
	BUILDINGSTATUS VARCHAR(50),
	LEEDSCORE BIGINT,
	ENERGYSCORE BIGINT,
	WATERSCORE BIGINT,
	WASTESCORE BIGINT,
	HUMANEXPERIENCESCORE BIGINT,
	TRANSPORTSCORE BIGINT,	
	CONSTRAINT LEEDCONFIGURATION_BUILDING_FK FOREIGN KEY(ID) REFERENCES Building(ID) ON DELETE CASCADE,
	CONSTRAINT LEEDCONFIGURATION_FK_ORGID FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID),
	CONSTRAINT LEEDCONFIGURATION_FK_MODULEID FOREIGN KEY (MODULEID) REFERENCES Modules(MODULEID)
);

CREATE TABLE IF NOT EXISTS LeedEnergyMeter (
	DEVICE_ID BIGINT PRIMARY KEY,
	FUELTYPE BIGINT,
  	METERID BIGINT,
  	CONSTRAINT LEEDENERGYMETER_DEVICE_FK FOREIGN KEY (DEVICE_ID) REFERENCES Device(DEVICE_ID)
);

CREATE TABLE IF NOT EXISTS ConsumptionInfo (
	ENERGYDATAID BIGINT PRIMARY KEY,
	CONSUMPTIONID BIGINT,
	STARTDATE BIGINT,
	CONSTRAINT CONSUMPTIONINFO_ENERGYDATA_FK FOREIGN KEY (ENERGYDATAID) REFERENCES Energy_Data(ENERGY_DATA_ID)
);

CREATE TABLE IF NOT EXISTS ArcCredential (
	ORGID BIGINT PRIMARY KEY,
	USERNAME VARCHAR(50),
	PASSWORD VARCHAR(50),
	SUBSCRIPTIONKEY VARCHAR(100),
	AUTHKEY VARCHAR(100),
	AUTHUPDATETIME BIGINT,
	ARCPROTOCOL VARCHAR(50),
	ARCHOST  VARCHAR(100),
	ARCPORT VARCHAR(50),
	CONSTRAINT ARCCREDENTIAL_ORG_FK FOREIGN KEY (ORGID) REFERENCES Organizations(ORGID)
);

