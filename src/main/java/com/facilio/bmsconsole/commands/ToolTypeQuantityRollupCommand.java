package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ToolTypeQuantityRollupCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long toolTypeId = (long) context.get(FacilioConstants.ContextNames.TOOL_TYPES_ID);
		
		FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);
		List<FacilioField> toolTypesFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TYPES);
		
		double quantity = getTotalQuantity(toolTypeId);
		
		ToolTypesContext toolType = new ToolTypesContext();
		toolType.setId(toolTypeId);
		toolType.setCurrentQuantity(quantity);
		
		UpdateRecordBuilder<ToolTypesContext> updateBuilder = new UpdateRecordBuilder<ToolTypesContext>()
				.module(toolTypesModule).fields(modBean.getAllFields(toolTypesModule.getName()))
				.andCondition(CriteriaAPI.getIdCondition(toolType.getId(), toolTypesModule));
		
		updateBuilder.update(toolType);
		
		return false;
	}

	public static double getTotalQuantity(long id)
			throws Exception {

		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> toolTypesFieldMap = FieldFactory.getAsMap(toolFields);
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalQuantity", "sum(CURRENT_QUANTITY)", FieldType.DECIMAL));

		SelectRecordsBuilder<ItemContext> builder = new SelectRecordsBuilder<ItemContext>()
																.select(field)
																.moduleName(toolModule.getName())
																.andCondition(CriteriaAPI.getCondition(toolTypesFieldMap.get("toolType"), String.valueOf(id), NumberOperators.EQUALS))
																.setAggregation()
																;
		
		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalQuantity") != null) {
				return (double) rs.get(0).get("totalQuantity");
			}
			return 0d;
		}
		return 0d;
	}
	
}
