package com.facilio.bmsconsole.commands;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.pdf.PdfUtil;

public class AddNdaForVisitorLogCommand extends FacilioCommand implements Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> visitorLoggings = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(visitorLoggings)) {
			for(VisitorLoggingContext vL : visitorLoggings) {
				if(vL.isNdaSigned()) {
					File file = PdfUtil.exportUrlAsFile(FacilioProperties.getClientAppUrl() + "/app/pdf/visitornda/" + vL.getId(), vL.getId()+" - NDA");
					//VisitorManagementAPI.updateVisitorLogNDA(vL.getId(), fileId);
					VisitorManagementAPI.updateVisitorLogNDA(vL.getId(), file);
				}
			}
		}
	
		return false;
	}


}
