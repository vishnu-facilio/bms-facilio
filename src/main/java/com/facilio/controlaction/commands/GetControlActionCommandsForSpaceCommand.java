package com.facilio.controlaction.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControllableAssetCategoryContext.ControllableCategory;
import com.facilio.controlaction.context.ControllablePointContext.ControllablePoints;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class GetControlActionCommandsForSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControllableCategory controllableCategory = (ControllableCategory) context.get(ControlActionUtil.CONTROLLABLE_CATEGORY);
		
		ControllablePoints controllablePoint = (ControllablePoints) context.get(ControlActionUtil.CONTROLLABLE_POINT);
		String value = (String) context.get(ControlActionUtil.VALUE);
		long spaceId = (long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule assetModule = modbean.getModule(FacilioConstants.ContextNames.ASSET);
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		fields.addAll(modbean.getAllFields(assetModule.getName()));
		
		fields.addAll(FieldFactory.getReadingDataMetaFields());
		fields.addAll(FieldFactory.getControllablePointFields());
		
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
			.module(assetModule)
			.beanClass(ModuleBaseWithCustomFields.class)
				.select(fields)
				
				.innerJoin(ModuleFactory.getReadingDataMetaModule().getTableName())
				.on(ModuleFactory.getReadingDataMetaModule().getTableName()+".RESOURCE_ID = "+assetModule.getTableName()+".ID")
				.innerJoin(ModuleFactory.getControllablePointModule().getTableName())
				.on(ModuleFactory.getReadingDataMetaModule().getTableName()+".FIELD_ID = "+ModuleFactory.getControllablePointModule().getTableName()+".FIELD_ID")

				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("space"), spaceId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("controllablePoint"), controllablePoint.getPointId()+"", NumberOperators.EQUALS))
				;
		
		
		List<Map<String, Object>> props = select.getAsProps();
		
		List<ControlActionCommandContext> controlActionCommandContexts = new ArrayList<>();
		
		if(props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop : props) {
				
				long resourceId = (long) prop.get("resourceId");
				
				long fieldId = (long) prop.get("fieldId");
				
				ResourceContext resourceContext = new ResourceContext();
				resourceContext.setId(resourceId);
				
				ControlActionCommandContext controlActionCommand = new ControlActionCommandContext();
				controlActionCommand.setResource(resourceContext);
				controlActionCommand.setFieldId(fieldId);
				controlActionCommand.setValue(value);
				
				controlActionCommandContexts.add(controlActionCommand);
			}
			
		}
		
		context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, controlActionCommandContexts);
		return false;
	}

}
