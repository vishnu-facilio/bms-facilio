package com.facilio.datamigration.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.componentpackage.command.UnzipPackageFileCommand;

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

	public static FacilioChain getCopyDataMigrationChain() {

		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateCopyDataMigrationCommand());
		c.addCommand(new CheckAndAddDataMigrationStatusCommand());
		c.addCommand(new FillDataMigrationModuleDetailsCommand());
		c.addCommand(new DataMigrationCreateDataFileCommand());
		c.addCommand(new FillLookupModuleDataMigrationDetailsCommand());

		return c;
	}

	public static FacilioChain getInstallDataMigrationChain(){

		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateInsertDataMigrationCommand());
		c.addCommand(new UnzipPackageFileCommand());
		c.addCommand(new DataMigrationInsertRecordCommand());
		c.addCommand(new FillDataMigrationModuleDetailsCommand());
		c.addCommand(new DataMigrationUpdateInsertDataLookupDetails());
		c.addCommand(new DataMigrationInsertModuleAttachmentsCommand());
		c.addCommand(new DataMigrationInsertModuleNotesCommand());

		return c;
	}
}



