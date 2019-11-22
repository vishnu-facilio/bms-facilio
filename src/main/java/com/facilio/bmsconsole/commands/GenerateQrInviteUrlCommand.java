package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.pdf.PdfUtil;


public class GenerateQrInviteUrlCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> inviteVisitors = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(inviteVisitors)) {
			for(VisitorLoggingContext inviteVisitor : inviteVisitors) {
				if(inviteVisitor.isPreregistered()) {
					String qrCode = "visitorLog_" + inviteVisitor.getId();
					String originalUrl = PdfUtil.exportUrlAsPdf("https://app.facilio.com/app/qr?code=" + qrCode, true, null, FileFormat.IMAGE);
					inviteVisitor.setQrUrl(originalUrl);
				}
			}
			
			
		}
		return false;
	}

}
