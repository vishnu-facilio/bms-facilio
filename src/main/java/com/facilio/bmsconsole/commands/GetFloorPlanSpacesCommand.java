package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SmartControlKioskContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.KioskAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

public class GetFloorPlanSpacesCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Long floorId = (Long) context.get(FacilioConstants.ContextNames.FLOOR_ID);
		
		if (floorId != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioModule moduleObj = modBean.getModule(FacilioConstants.ModuleNames.SPACE);
			
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ModuleNames.SPACE);
			
			SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
					.table(moduleObj.getTableName())
					.moduleName(moduleObj.getName())
					.beanClass(SpaceContext.class)
					.select(fields)
					.orderBy("ID");
			
			builder.andCondition(CriteriaAPI.getCondition("FLOOR_ID", "floor", floorId+"", PickListOperators.IS));
			
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(FacilioConstants.ModuleNames.SPACE);
			if (scopeCriteria != null) {
				builder.andCriteria(scopeCriteria);
			}
			
			if (AccountUtil.getCurrentAccount().getConnectedDevice() != null) {
				
				Long deviceId = AccountUtil.getCurrentAccount().getConnectedDevice().getDeviceId();
				SmartControlKioskContext kiosk = KioskAPI.getSmartControlDevice(deviceId);
				
				if (kiosk != null) {
					long tenantId = kiosk.getTenantId();
					if (tenantId > 0) {
						List<TenantSpaceContext> tenantSpaces = TenantsAPI.fetchTenantSpaces(tenantId);
						if (CollectionUtils.isNotEmpty(tenantSpaces)) {
							List<BaseSpaceContext> spaces = tenantSpaces.stream().map(TenantSpaceContext::getSpace).collect(Collectors.toList());
							List<Long> spaceIds = spaces.stream().map(BaseSpaceContext::getId).collect(Collectors.toList());
							
							builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(moduleObj), spaceIds, BuildingOperator.BUILDING_IS));
						}
					}
					else if (kiosk.getAssociatedResource() != null) {
						List<Long> spaceIds = new ArrayList<>();
						spaceIds.add(kiosk.getAssociatedResource().getId());
						builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(moduleObj), spaceIds, BuildingOperator.BUILDING_IS));
					}
				}
			}
			
			List<SpaceContext> spaces = builder.get();
			
			context.put(FacilioConstants.ContextNames.SPACE_LIST, spaces);
		}
	      
		return false;
	}
}
