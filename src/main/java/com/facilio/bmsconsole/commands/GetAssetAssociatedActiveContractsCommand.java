package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class GetAssetAssociatedActiveContractsCommand extends FacilioCommand {
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	    Long assetId = (Long)context.get(FacilioConstants.ContextNames.ID);
	    String contractModuleName = FacilioConstants.ContextNames.CONTRACTS;
		FacilioModule contractModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACTS);
		List<FacilioField> contractFields = modBean.getAllFields(contractModule.getName());
		Map<String, FacilioField> contractFieldMap = FieldFactory.getAsMap(contractFields);
		FacilioModule associatedAssetModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);

		//List<LookupField>fetchLookup = Arrays.asList((LookupField) contractFieldMap.get("vendor"));


		SelectRecordsBuilder<ContractsContext> builder = new SelectRecordsBuilder<ContractsContext>()
			
				.moduleName(contractModuleName)
				.select(contractFields)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(contractModuleName))
				.innerJoin(associatedAssetModule.getTableName())
				.on(associatedAssetModule.getTableName() + ".CONTRACT_ID = " + contractModule.getTableName()+".ID")
                .andCustomWhere(associatedAssetModule.getTableName()+".ASSET_ID = ?", assetId)
				.andCustomWhere("(" + contractModule.getTableName() + ".SYS_DELETED IS NULL") 
                .orCustomWhere(contractModule.getTableName() + ".SYS_DELETED = 0" + ")")
                .andCustomWhere(contractModule.getTableName() + ".FROM_DATE <= ?", startTime)
                .andCustomWhere(contractModule.getTableName() + ".END_DATE >= ?", startTime)
                .andCustomWhere(contractModule.getTableName() + ".STATUS = 2 ")
				.fetchSupplements(Arrays.asList((LookupField) contractFieldMap.get("vendor")) )
                ;
		List<ContractsContext> list = builder.get();
		if(!CollectionUtils.isEmpty(list)) {
			context.put(FacilioConstants.ContextNames.CONTRACTS, list);
						
	}	
		return false;
	}
}
