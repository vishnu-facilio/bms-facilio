package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateQrCommand extends FacilioCommand {

	public static final String QR_PREFIX = "facilio_";

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<Long> resourceQr =(ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

		List<ResourceContext> resources = checkAndGetContextList(resourceQr);

		Map<Long,String> mappedqr = new HashMap<Long, String>();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		if (fields == null) {
			fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		}

		for(ResourceContext resourceContext : resources) {
			resourceContext.setQrVal(QR_PREFIX +  resourceContext.getId());
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(resourceContext.getId(), module));
			updateBuilder.update(resourceContext);
			mappedqr.put(resourceContext.getId(), resourceContext.getQrVal());
		}

		context.put(FacilioConstants.ContextNames.MAP_QR,mappedqr);
		return false;
	}

	private List<ResourceContext> checkAndGetContextList(List<Long> resourceQr) throws Exception {

		List<ResourceContext> resources = ResourceAPI.getResources(resourceQr, false);
		List<ResourceContext> returnResult = new ArrayList<>();
		for(ResourceContext resource : resources) {

			if(resource.getQrVal() == null) {
				returnResult.add(resource);
			}
		}
		return returnResult;
	}

}
