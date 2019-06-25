package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateAssociatedAssetContextCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContractAssociatedAssetsContext> contractAssociatedAssets = (List<ContractAssociatedAssetsContext>)context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		PurchaseOrderContext po = (PurchaseOrderContext)context.get(FacilioConstants.ContextNames.RECORD);
		StringJoiner ids = new StringJoiner(",");
		for(ContractAssociatedAssetsContext asset : contractAssociatedAssets) {
			ids.add(String.valueOf(asset.getId()));
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, Object> updateMap = new HashMap<>();
		FacilioField statusField = modBean.getField("status", module.getName());
		FacilioField poIdField = modBean.getField("poId", module.getName());
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		
		if(po != null) {
			updateMap.put("poId", po.getId());
			updatedfields.add(poIdField);
		}
		updateMap.put("status", ContractAssociatedAssetsContext.Status.PURCHASED.ordinal()+1);
		updatedfields.add(statusField);
		
		UpdateRecordBuilder<ContractAssociatedAssetsContext> updateBuilder = new UpdateRecordBuilder<ContractAssociatedAssetsContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(ids.toString(),module));
			    ;
		updateBuilder.updateViaMap(updateMap);
		
		return false;
	}

}
