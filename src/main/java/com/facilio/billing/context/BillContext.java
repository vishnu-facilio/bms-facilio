package com.facilio.billing.context;

import com.facilio.billing.command.GenerateUsageRecordCommand;
import com.facilio.billing.command.StoreExcelFileCommand;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

public class BillContext {

	public static class ContextNames {
		public static final String ORGID = "OrgId";
		public static final String FILE = "file";
		public static final String TEMPLATENAME = "templateName";
		public static final String CONTENTTYPE = "contentType";
		public static final String FILENAME = "filename";
		public static final String STARTTIME = "starttime";
		public static final String ENDTIME = "endtime";
		public static final String TEMPLATEID = "templateId";
		public static final String EXCEL_FILE_DOWNLOAD_URL = "excelfiledownloadurl";
		public static final String EXCELOBJECT = "excelObject";
		
	}
	
	public static Chain HandleExcelFileUploadChain()
	{
		Chain c = new ChainBase();
		c.addCommand(new StoreExcelFileCommand());
		return c;
	}
	
	public static Chain HandleBillGenerationChain()
	{
		Chain c = new ChainBase();
		c.addCommand(new GenerateUsageRecordCommand());
		return c;
	}
}
