package com.facilio.bmsconsoleV3.commands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
public class MarkTenantunitAsVacantCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
		Boolean markVacant = false;
		if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("markVacant")) {
        	markVacant = true;
        }
		if(markVacant) {
			String moduleName = Constants.getModuleName(context);
	        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
	        List<V3TenantUnitSpaceContext> updateUnits = new ArrayList<>();
	        List<Long> tenantIds = new ArrayList<Long>();
	        List<Long> spaceIds = new ArrayList<Long>();
	        for(V3TenantUnitSpaceContext tenantunit : (List<V3TenantUnitSpaceContext>)recordMap.get(moduleName)) {
	        	if(tenantunit.getTenant() != null) {
		        	tenantIds.add(tenantunit.getTenant().getId());
	        	}
	        	if(tenantunit.getSpace() != null) {
	        		spaceIds.add(tenantunit.getSpace().getId());
	        	}
	        	tenantunit.setIsOccupied(false);
	        	tenantunit.setTenant(null);
	        	updateUnits.add(tenantunit);
	        }
	        recordMap.put(moduleName, updateUnits);
	        disassociateSpacesInTenantSpaces(tenantIds,spaceIds);
		}
		return false;
	}
	private void disassociateSpacesInTenantSpaces(List<Long> tenantIds,List<Long> spaceIds)  throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_SPACES);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> map = FieldFactory.getAsMap(fields);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(map.get("currentlyOccupied"), Boolean.toString(true), BooleanOperators.IS));
		criteria.addAndCondition(CriteriaAPI.getCondition(map.get("space"), StringUtils.join(spaceIds,","), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition(map.get("tenant"), StringUtils.join(tenantIds,","), NumberOperators.EQUALS));
		
		List<V3TenantSpaceContext> tenantSpaces= V3RecordAPI.getRecordsListWithSupplements(module.getName(), null, V3TenantSpaceContext.class, criteria, null);
		
		for( V3TenantSpaceContext tenantSpace : tenantSpaces){
			tenantSpace.setCurrentlyOccupied(Boolean.FALSE);
			tenantSpace.setDisassociatedTime(System.currentTimeMillis());
			V3RecordAPI.updateRecord(tenantSpace, module , fields);
		}
	}
}