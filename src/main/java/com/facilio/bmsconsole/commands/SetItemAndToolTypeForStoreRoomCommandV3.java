package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

;

public class SetItemAndToolTypeForStoreRoomCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Long storeRoomId = (Long) context.get(FacilioConstants.ContextNames.STORE_ROOM);
		
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

		V3StoreRoomContext storeRoom = new V3StoreRoomContext();
		storeRoom.setId(storeRoomId);
		storeRoom.setNoOfItemTypes(itemTotal);
		storeRoom.setNoOfToolTypes(toolTotal);
		
		UpdateRecordBuilder<V3StoreRoomContext> updateBuilder = new UpdateRecordBuilder<V3StoreRoomContext>()
				.module(storeRoomModule).fields(modBean.getAllFields(storeRoomModule.getName()))
				.andCondition(CriteriaAPI.getIdCondition(storeRoomId, storeRoomModule));

		updateBuilder.update(storeRoom);
		
		return false;
	}
	
	private long getItemTypeTotal(long id, FacilioModule module, Map<String, FacilioField> itemFieldMap) throws Exception {
		long total = 0;
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("total", "COUNT(*)", FieldType.DECIMAL));

		SelectRecordsBuilder<V3ItemContext> builder = new SelectRecordsBuilder<V3ItemContext>().select(field)
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

		SelectRecordsBuilder<V3ToolContext> builder = new SelectRecordsBuilder<V3ToolContext>().select(field)
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
