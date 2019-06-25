package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class GetActiveContractAssociatedAssetsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule contractModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACTS);
		Integer contractType = (Integer)context.get(FacilioConstants.ContextNames.CONTRACT_TYPE);
		FacilioModule associatedAssetModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		List<FacilioField> associatedAssetFields = modBean.getAllFields(associatedAssetModule.getName());
		Map<String, FacilioField> associatedAssetFieldMap = FieldFactory.getAsMap(associatedAssetFields);
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.add(associatedAssetFieldMap.get("asset"));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields).table(contractModule.getTableName())
				.innerJoin(associatedAssetModule.getTableName()).on(contractModule.getTableName()+".ID = "+associatedAssetModule.getTableName()+".CONTRACT_ID")
				.andCondition(CriteriaAPI.getCondition(contractModule.getTableName()+".STATUS", "status", String.valueOf(ContractsContext.Status.APPROVED.getValue()) , NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(contractModule.getTableName()+".CONTRACT_TYPE", "contractType", String.valueOf(contractType) , NumberOperators.EQUALS))
			    
				;
		List<Map<String, Object>> list = selectBuilder.get();
		if(!CollectionUtils.isEmpty(list)) {
			context.put(FacilioConstants.ContextNames.ASSET_ID, list);
		}
		return false;
	}

}
