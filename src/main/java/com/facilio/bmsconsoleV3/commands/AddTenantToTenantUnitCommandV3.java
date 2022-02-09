package com.facilio.bmsconsoleV3.commands;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddTenantToTenantUnitCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);

        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        Boolean spacesUpdate = false;
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("spacesUpdate")) {
            spacesUpdate = true;
        }
        if (CollectionUtils.isNotEmpty(tenants)) {
            for(V3TenantContext tenant : tenants) {
        		if(spacesUpdate != null && spacesUpdate && CollectionUtils.isNotEmpty(tenant.getSpaces())) {

        			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        			List<FacilioField> fields = modBean.getAllFields(module.getName());
        			List<V3TenantUnitSpaceContext> tenantUnits = V3RecordAPI.getRecordsList(module.getName(), tenant.getSpaces().stream().map(BaseSpaceContext::getId).collect(Collectors.toList()),V3TenantUnitSpaceContext.class);
        			if(CollectionUtils.isNotEmpty(tenantUnits)) {
	        			for(V3TenantUnitSpaceContext tenantUnit : tenantUnits) {
	        				tenantUnit.setIsOccupied(true);
	        				tenantUnit.setTenant(tenant);
							V3RecordAPI.updateRecord(tenantUnit, module, fields);
	        			}
        			}
        		}
            }
        }
		return false;
	}
}
