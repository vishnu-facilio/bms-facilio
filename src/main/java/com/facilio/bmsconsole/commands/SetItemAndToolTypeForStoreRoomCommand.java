package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.ToolContext;
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

public class SetItemAndToolTypeForStoreRoomCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		
		FacilioModule itemModule = modBean.getModule(FacilioConstants.ContextNames.ITEM);
		List<FacilioField> itemFields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
		Map<String, FacilioField> itemFieldMap = FieldFactory.getAsMap(itemFields);
		
		FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
		Map<String, FacilioField> toolFieldMap = FieldFactory.getAsMap(toolFields);
		
		FacilioModule storeRoomModule = modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.STORE_ROOM);
		
		long itemTotal = -1, toolTotal = -1;
		itemTotal = getItemTypeTotal(storeRoomId, itemModule, itemFieldMap);
		toolTotal = getToolTypeTotal(storeRoomId, toolModule, toolFieldMap);
		
		StoreRoomContext storeRoom = new StoreRoomContext();
		storeRoom.setId(storeRoomId);
		storeRoom.setNoOfItemTypes(itemTotal);
		storeRoom.setNoOfToolTypes(toolTotal);
		
		UpdateRecordBuilder<StoreRoomContext> updateBuilder = new UpdateRecordBuilder<StoreRoomContext>()
				.module(storeRoomModule).fields(modBean.getAllFields(storeRoomModule.getName()))
				.andCondition(CriteriaAPI.getIdCondition(storeRoomId, storeRoomModule));

		updateBuilder.update(storeRoom);
		
		return false;
	}
	
	private long getItemTypeTotal(long id, FacilioModule module, Map<String, FacilioField> itemFieldMap) throws Exception {
		long total = 0;
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("total", "COUNT(*)", FieldType.DECIMAL));

		SelectRecordsBuilder<ItemContext> builder = new SelectRecordsBuilder<ItemContext>().select(field)
				.moduleName(module.getName()).andCondition(CriteriaAPI
						.getCondition(itemFieldMap.get("storeRoom"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("total") != null) {
				return (long) rs.get(0).get("total");
			}
			return 0;
		}
		
		return total;
	}
	
	private long getToolTypeTotal(long id, FacilioModule module, Map<String, FacilioField> itemFieldMap) throws Exception {
		long total = 0;
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("total", "COUNT(*)", FieldType.DECIMAL));

		SelectRecordsBuilder<ToolContext> builder = new SelectRecordsBuilder<ToolContext>().select(field)
				.moduleName(module.getName()).andCondition(CriteriaAPI
						.getCondition(itemFieldMap.get("storeRoom"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation();

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("total") != null) {
				return (long) rs.get(0).get("total");
			}
			return 0;
		}
		
		return total;
	}

}
