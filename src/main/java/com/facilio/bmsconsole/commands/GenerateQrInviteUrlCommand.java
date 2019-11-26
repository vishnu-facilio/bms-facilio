package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.pdf.PdfUtil;


public class GenerateQrInviteUrlCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<VisitorLoggingContext> inviteVisitors = (List<VisitorLoggingContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(inviteVisitors)) {
			for(VisitorLoggingContext inviteVisitor : inviteVisitors) {
				String qrCode = "visitorLog_" + inviteVisitor.getId();
				JSONObject size = new JSONObject();
				size.put("width", 200);
				size.put("height", 200);
				String originalUrl = PdfUtil.exportUrlAsPdf("https://app.facilio.com/app/qr?code=" + qrCode, true, null, size, FileFormat.IMAGE);
				inviteVisitor.setQrUrl(originalUrl);
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);
				
				
				Map<String, Object> updateMap = new HashMap<>();
				FacilioField qrUrlField = modBean.getField("qrUrl", module.getName());
				updateMap.put("qrUrl", originalUrl);
				List<FacilioField> updatedfields = new ArrayList<FacilioField>();
				updatedfields.add(qrUrlField);
				UpdateRecordBuilder<VisitorLoggingContext> updateBuilder = new UpdateRecordBuilder<VisitorLoggingContext>()
						.module(module)
						.fields(updatedfields)
						.andCondition(CriteriaAPI.getIdCondition(inviteVisitor.getId(), module));
						
					;
				updateBuilder.updateViaMap(updateMap);
			
			}
			
			
		}
		return false;
	}

}
