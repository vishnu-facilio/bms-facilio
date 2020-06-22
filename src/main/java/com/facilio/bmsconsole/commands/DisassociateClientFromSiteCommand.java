package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.InventoryRequestLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class DisassociateClientFromSiteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> siteIds = (List<Long>) context.get(FacilioConstants.ContextNames.SITE_LIST);

		if (siteIds != null && !siteIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField clientField = modBean.getField("client", module.getName());
			ClientContext client = new ClientContext();
			client.setId(-99);
			updateMap.put("client", FieldUtil.getAsProperties(client));
			List<FacilioField> fields = new ArrayList<FacilioField>();
			fields.add(clientField);
			
			UpdateRecordBuilder<InventoryRequestLineItemContext> updateBuilder = new UpdateRecordBuilder<InventoryRequestLineItemContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(siteIds, module))
					;
			updateBuilder.updateViaMap(updateMap);
			
		}
		return false;
	}

}
