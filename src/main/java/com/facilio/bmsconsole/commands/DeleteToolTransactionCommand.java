package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PurchasedToolContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class DeleteToolTransactionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<ToolTransactionContext> deletedToolTransactions = (List<ToolTransactionContext>) context
				.get(FacilioConstants.ContextNames.RECORD_LIST);

		if (deletedToolTransactions != null && !deletedToolTransactions.isEmpty()) {
			for (ToolTransactionContext toolTransaction : deletedToolTransactions) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);
				List<FacilioField> toolFields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);

				Map<String, FacilioField> toolFieldsMap = FieldFactory.getAsMap(toolFields);
				List<LookupField>lookUpfields = new ArrayList<>();
				lookUpfields.add((LookupField) toolFieldsMap.get("toolType"));

				SelectRecordsBuilder<ToolContext> itemSelectBuilder = new SelectRecordsBuilder<ToolContext>()
						.select(toolFields).table(toolModule.getTableName()).moduleName(toolModule.getName())
						.beanClass(ToolContext.class).andCustomWhere(toolModule.getTableName() + ".ID = ?",
								toolTransaction.getTool().getId())
						.fetchSupplements(lookUpfields);

				List<ToolContext> tools = itemSelectBuilder.get();
				if (tools != null && !tools.isEmpty()) {
					ToolContext tool = tools.get(0);
					if (tool.getToolType().isRotating()) {
						FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_TOOL);
						List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_TOOL);

						SelectRecordsBuilder<PurchasedToolContext> selectBuilder = new SelectRecordsBuilder<PurchasedToolContext>()
								.select(fields).table(module.getTableName()).moduleName(module.getName())
								.beanClass(PurchasedToolContext.class).andCustomWhere(module.getTableName() + ".ID = ?",
										toolTransaction.getPurchasedTool().getId());

						List<PurchasedToolContext> purchasedTools = selectBuilder.get();

						if (purchasedTools != null && !purchasedTools.isEmpty()) {
							PurchasedToolContext purchasedTool = purchasedTools.get(0);
							purchasedTool.setIsUsed(false);
							updatePurchasedTool(purchasedTool, module, fields);
						}
					}
				}

			}
		}

		return false;
	}

	private void updatePurchasedTool(PurchasedToolContext purchasedTool,FacilioModule module, List<FacilioField> fields) throws Exception {
		UpdateRecordBuilder<PurchasedToolContext> updateBuilder = new UpdateRecordBuilder<PurchasedToolContext>()
				.module(module).fields(fields).andCondition(CriteriaAPI.getIdCondition(purchasedTool.getId(), module));
		updateBuilder.update(purchasedTool);

		System.err.println(Thread.currentThread().getName() + "Exiting updateReadings in  AddorUpdateCommand#######  ");

	}

}
