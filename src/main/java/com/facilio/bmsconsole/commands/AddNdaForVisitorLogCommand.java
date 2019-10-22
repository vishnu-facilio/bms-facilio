package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.pdf.PdfUtil;

public class AddNdaForVisitorLogCommand extends FacilioCommand implements PostTransactionCommand{

	Context context = null;
	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLoggings = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLoggings)) {
			for(VisitorLoggingContext vL : visitorLoggings) {
				long fileId = PdfUtil.exportUrlAsPdf(FacilioProperties.getClientAppUrl() + "/app/pdf/visitornda/" + vL.getId(), vL.getId()+" - NDA");
				VisitorManagementAPI.updateVisitorLogNDA(vL.getId(), fileId);
			}
		}
		
		return false;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		this.context = context;
		return false;
	}


}
