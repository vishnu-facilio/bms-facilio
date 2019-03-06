package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.StockedToolsTransactionContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class StockedToolQuantityRollUpCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL);
		List<FacilioField> stockedToolsFields = modBean
				.getAllFields(FacilioConstants.ContextNames.TOOL);
		
		List<Long> stockedToolsIds = (List<Long>) context.get(FacilioConstants.ContextNames.TOOL_IDS);
		
		if(stockedToolsIds!=null && !stockedToolsIds.isEmpty()) {
			for(long stId: stockedToolsIds) {				
				SelectRecordsBuilder<ToolContext> selectBuilder = new SelectRecordsBuilder<ToolContext>()
						.select(stockedToolsFields).table(module.getTableName()).moduleName(module.getName())
						.beanClass(ToolContext.class)
						.andCondition(CriteriaAPI.getIdCondition(stId, module));
				
				List<ToolContext> stockedTools = selectBuilder.get();
				ToolContext stools = new ToolContext();
				if (stockedTools != null && !stockedTools.isEmpty()) {
					stools = stockedTools.get(0);
					double availableQty = stools.getQuantity() - getTotalReturnQuantity(stId);
					stools.setCurrentQuantity(availableQty);
				}

				UpdateRecordBuilder<ToolContext> updateBuilder = new UpdateRecordBuilder<ToolContext>()
						.module(module).fields(modBean.getAllFields(module.getName()))
						.andCondition(CriteriaAPI.getIdCondition(stools.getId(), module));
				
				updateBuilder.update(stools);
			}
		}
		
		return false;
	}

	public static double getTotalReturnQuantity(long id) throws Exception {

		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STOCKED_TOOLS_TRANSACTIONS);
		List<FacilioField> stockedToolsTransactionFields = modBean
				.getAllFields(FacilioConstants.ContextNames.STOCKED_TOOLS_TRANSACTIONS);
		Map<String, FacilioField> stockedToolsTransactionFieldMap = FieldFactory
				.getAsMap(stockedToolsTransactionFields);
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalReturnQty", "sum(RETURN_QUANTITY)", FieldType.DECIMAL));
		field.add(FieldFactory.getField("totalIssueQty", "sum(ISSUE_QUANTITY)", FieldType.DECIMAL));
//		SelectRecordsBuilder<StockedToolsTransactionContext> selectBuilder = new SelectRecordsBuilder<StockedToolsTransactionContext>()
//				.select(field).table(module.getTableName()).moduleName(module.getName())
//				.beanClass(StockedToolsTransactionContext.class)
//				.andCondition(CriteriaAPI.getCondition(stockedToolsTransactionFieldMap.get("StockedTool"), String.valueOf(id), NumberOperators.EQUALS));
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.andCustomWhere(module.getTableName() + ".ORGID = " + AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module),
						String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

//		List<FacilioField> fields = new ArrayList<>();
//		fields.add(FieldFactory.getField("value", "sum(QUANTITY_CONSUMED)", FieldType.DECIMAL));
		builder.select(field);

		builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module),
				String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		
		builder.andCondition(CriteriaAPI.getCondition(stockedToolsTransactionFieldMap.get("stockedTool"), String.valueOf(id), NumberOperators.EQUALS));
		

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && rs.size() > 0) {
			double returnTotal = 0;
			double issueTotal = 0;
			if (rs.get(0).get("totalReturnQty") == null) {
				returnTotal = 0;
			}
			else {
				returnTotal = (double) rs.get(0).get("totalReturnQty");
			}
			
			if (rs.get(0).get("totalIssueQty") == null) {
				issueTotal = 0;
			}
			else {
				issueTotal = (double) rs.get(0).get("totalIssueQty");
			}
			return issueTotal-returnTotal;
		}
		return 0d;
	}
}
