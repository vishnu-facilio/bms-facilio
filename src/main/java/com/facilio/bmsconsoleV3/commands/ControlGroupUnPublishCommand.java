package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupAssetCategory;
import com.facilio.control.ControlGroupAssetContext;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupFieldContext;
import com.facilio.control.ControlGroupRoutineContext;
import com.facilio.control.ControlGroupSection;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleGroupedSlot;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class ControlGroupUnPublishCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext group = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ControlGroupTenentContext> select = new SelectRecordsBuilder<ControlGroupTenentContext>()
				.select(fields)
				.moduleName(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)
				.beanClass(ControlGroupTenentContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentGroup"), group.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("tenant"), tenant.getId()+"", NumberOperators.EQUALS));
		
		List<ControlGroupTenentContext> sharedTenents = select.get();
		
		ControlGroupTenentContext childGroup = sharedTenents.get(0);
		
		planAssetBack(childGroup,group);
		
		
		DeleteRecordBuilder<ControlGroupTenentContext> delete = new DeleteRecordBuilder<ControlGroupTenentContext>()
				.moduleName(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)
				.andCondition(CriteriaAPI.getIdCondition(childGroup.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)));
		
		delete.markAsDelete();
		
		ControlScheduleContext schedule = ControlScheduleUtil.getControlSchedule(group.getControlSchedule().getId(),ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME);
		
		ControlScheduleUtil.deleteControlScheduleRelated(schedule);
		ControlScheduleUtil.deleteControlGroupRelated(childGroup);
		ControlScheduleUtil.deleteControlGroupSlotRelated(childGroup);
		
		FacilioChain chain = TransactionChainFactoryV3.getPlanControlGroupSlotChain();
		
		FacilioContext newContext = chain.getContext();
		
		Map<String, List<ControlGroupContext>> map = Collections.singletonMap(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, Collections.singletonList(group));
		
		newContext.put(FacilioConstants.ContextNames.RECORD_MAP, map);
		
		chain.execute();
		
		return false;
	}

	private void planAssetBack(ControlGroupContext tenantGroup, ControlGroupContext parentGroup) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		tenantGroup = ControlScheduleUtil.getControlGroup(tenantGroup.getId(), ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
		
		List<Long> returningAssets = new ArrayList<Long>();
		for(ControlGroupSection section :tenantGroup.getSections()) {
			for(ControlGroupAssetCategory category : section.getCategories()) {
				for(ControlGroupAssetContext asset :category.getControlAssets()) {
					returningAssets.add(asset.getAsset().getId());
				}
			}
		}
		
		if(!returningAssets.isEmpty()) {
			
			List<FacilioField> fields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME);
			
			Map<String, FacilioField> map = FieldFactory.getAsMap(fields);
			
			Map<String, Object> props = new HashMap<String, Object>();
			
			props.put("status", ControlGroupAssetContext.Status.ACTIVE.getIntVal());
			
			UpdateRecordBuilder<ControlGroupAssetContext> update = new UpdateRecordBuilder<ControlGroupAssetContext>()
					.fields(fields)
					.module(modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME))
					.andCondition(CriteriaAPI.getCondition(map.get("controlGroup"), parentGroup.getId()+"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(map.get("asset"), StringUtils.join(returningAssets,","), NumberOperators.EQUALS));
			
			update.updateViaMap(props);
			
		}
	}

}
