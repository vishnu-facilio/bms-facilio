package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ReturnAsssociatedAssetCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContractAssociatedAssetsContext> contractAssociatedAssets = (List<ContractAssociatedAssetsContext>)context.get(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		if(CollectionUtils.isNotEmpty(contractAssociatedAssets)) {
			StringJoiner ids = new StringJoiner(",");
			for(ContractAssociatedAssetsContext asset : contractAssociatedAssets) {
				ids.add(String.valueOf(asset.getId()));
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			Map<String, Object> updateMap = new HashMap<>();
			FacilioField statusField = modBean.getField("status", module.getName());
			updateMap.put("status", ContractAssociatedAssetsContext.Status.RETURNED.ordinal()+1);
			List<FacilioField> updatedfields = new ArrayList<FacilioField>();
			updatedfields.add(statusField);
			
			UpdateRecordBuilder<ContractAssociatedAssetsContext> updateBuilder = new UpdateRecordBuilder<ContractAssociatedAssetsContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(ids.toString(),module));
				    ;
			int updatedRowCount = updateBuilder.updateViaMap(updateMap);
			//update status in main asset table
			//update contract cost and quantity roll up
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updatedRowCount);
			
		}
		return false;
	}

}
