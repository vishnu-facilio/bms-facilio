package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShipmentContext;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsole.context.ToolTransactionContext;
import com.facilio.bmsconsole.util.TransactionState;
import com.facilio.bmsconsole.util.TransactionType;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class AddBulkRotatingToolShipmentTransactionCommand implements Command{


		@Override
		public boolean execute(Context context) throws Exception {
			// TODO Auto-generated method stub
			List<ToolTransactionContext> toolTransactions = (List<ToolTransactionContext>) context.get(FacilioConstants.ContextNames.TOOL_TRANSACTION_LIST);
			if (CollectionUtils.isNotEmpty(toolTransactions)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);
				List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL_TRANSACTIONS);

				List<FacilioField> Toolfields = modBean.getAllFields(FacilioConstants.ContextNames.TOOL);
				Map<String, FacilioField> toolsFieldMap = FieldFactory.getAsMap(Toolfields);
				List<LookupField> lookUpfields = new ArrayList<>();
				lookUpfields.add((LookupField) toolsFieldMap.get("toolType"));

				InsertRecordBuilder<ToolTransactionContext> readingBuilder = new InsertRecordBuilder<ToolTransactionContext>()
						.module(module).fields(fields).addRecords(toolTransactions);
				readingBuilder.save();
				List<ToolTransactionContext> shipmentTransactions = new ArrayList<ToolTransactionContext>();
				shipmentTransactions.addAll((List<ToolTransactionContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST));
				shipmentTransactions.addAll(toolTransactions);
				
				
				context.put(FacilioConstants.ContextNames.RECORD_LIST, shipmentTransactions);
			}
			return false;
		}

	

}
