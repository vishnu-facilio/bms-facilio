package com.facilio.datamigration.commands;

import com.facilio.chain.FacilioChain;

public class DataMigrationChainFactory {

	private static FacilioChain getDefaultChain() {
		//setting transaction timeout as 100 minutes
		return FacilioChain.getTransactionChain(6000000);
    }

		public static FacilioChain getDataMigrationChain() {
			FacilioChain c = getDefaultChain();
			c.addCommand(new ValidateDataMigrationRequestCommand());
			c.addCommand(new CheckAndAddDataMigrationStatusCommand());
			c.addCommand(new PreFillCustomizationMappingCommand());
			c.addCommand(new ComputeModuleSequenceCommand());
			c.addCommand(new DataMigrationCreateRecordCommand());
			c.addCommand(new DataMigrationUpdateRecordCommand());

			return c;
		}

}



