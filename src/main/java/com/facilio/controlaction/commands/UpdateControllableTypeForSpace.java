package com.facilio.controlaction.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControllableAssetCategoryContext;
import com.facilio.controlaction.context.ControllablePointContext;
import com.facilio.controlaction.context.ControllableResourceContext;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateControllableTypeForSpace extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long spaceId = (long)context.get(FacilioConstants.ContextNames.SPACE_ID);
		
		ControlActionMode controlActionMode = (ControlActionMode)context.get(ControlActionUtil.CONTROL_MODE);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule assetModule = modbean.getModule(FacilioConstants.ContextNames.ASSET);
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		fields.addAll(modbean.getAllFields(assetModule.getName()));
		
		fields.addAll(FieldFactory.getReadingDataMetaFields());
		
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
			.module(assetModule)
			.beanClass(ModuleBaseWithCustomFields.class)
				.select(fields)
				
				.innerJoin(ModuleFactory.getReadingDataMetaModule().getTableName())
				.on(ModuleFactory.getReadingDataMetaModule().getTableName()+".RESOURCE_ID = "+assetModule.getTableName()+".ID")

				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("space"), spaceId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS))
				;
		
		
		List<Map<String, Object>> props = select.getAsProps();
		
		List<ReadingDataMeta> metas = new ArrayList<>();
		if(props != null && !props.isEmpty()) {

			for(Map<String, Object> prop : props) {
				
				ReadingDataMeta rdm = FieldUtil.getAsBeanFromMap(prop, ReadingDataMeta.class);
				rdm.setControlActionMode(controlActionMode.getIntVal());
				metas.add(rdm);
			}
		}
		
		FacilioChain addReadingAlarmRuleChain = TransactionChainFactory.updateReadingDataMetaChain();
		
		FacilioContext context1 = addReadingAlarmRuleChain.getContext();
		
		context1.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, metas);
		
		addReadingAlarmRuleChain.execute();
		
		return false;
	}

}
