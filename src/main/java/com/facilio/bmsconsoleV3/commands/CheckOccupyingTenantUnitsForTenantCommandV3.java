package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class CheckOccupyingTenantUnitsForTenantCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {        
	    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");        
		List<Long> tenantIdss = (List<Long>) context.get(Constants.RECORD_ID_LIST);
        if (CollectionUtils.isNotEmpty(tenantIdss)) {
        	String tenantUnitModuleName = FacilioConstants.ContextNames.TENANT_UNIT_SPACE;
            Criteria criteria = new Criteria();
            Map<String, FacilioField> fieldMapTenantUnits = FieldFactory.getAsMap(modBean.getAllFields(tenantUnitModuleName));
    		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMapTenantUnits.get("tenant"), StringUtils.join(tenantIdss,","), NumberOperators.EQUALS));
    		FacilioField aggregateField = FieldFactory.getIdField(modBean.getModule(tenantUnitModuleName));
    		List<Map<String, Object>> props = V3RecordAPI.getRecordsAggregateValue(tenantUnitModuleName, null, V3TenantUnitSpaceContext.class,criteria,BmsAggregateOperators.CommonAggregateOperator.COUNT, aggregateField, null);
    		if(props != null) {
    			Long count = (Long) props.get(0).get(aggregateField.getName());
    			if(count != null && count > 0) {
    	            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tenant(s) has units occupied. Please mark units vacant before deleting tenant(s)");
    			}
	        }

        }
		return false;
	}

}
