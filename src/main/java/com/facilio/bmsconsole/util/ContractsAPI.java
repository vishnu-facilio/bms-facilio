package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ContractsAPI {

	public static List<Long> fetchAssociatedContractIds(Long assetId) throws Exception {
		FacilioModule module = ModuleFactory.getContractAssociatedAssetsModule();
		List<FacilioField> facilioFields = FieldFactory.getContractAssociatedAssetModuleFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(facilioFields)
			    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition("ASSET_ID", "assetId", String.valueOf(assetId),NumberOperators.EQUALS));
		List<Map<String,Object>> list = selectBuilder.get();
		List<Long> contractIds = new ArrayList<Long>();
		for(Map<String,Object> map : list) {
			contractIds.add((Long)map.get("contractId"));
		}
		return contractIds;
			                 
	}
	
	public static List<Long> fetchAssociatedAssets(Long contractId) throws Exception {
		FacilioModule module = ModuleFactory.getContractAssociatedAssetsModule();
		List<FacilioField> facilioFields = FieldFactory.getContractAssociatedAssetModuleFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(facilioFields)
			    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(contractId),NumberOperators.EQUALS));
		List<Map<String,Object>> list = selectBuilder.get();
		List<Long> assetIds = new ArrayList<Long>();
		for(Map<String,Object> map : list) {
			assetIds.add((Long)map.get("assetId"));
		}
		return assetIds;
			                 
	}
}
