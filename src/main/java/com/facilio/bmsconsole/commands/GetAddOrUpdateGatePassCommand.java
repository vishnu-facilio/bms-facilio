package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.DeleteRecordBuilder;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetAddOrUpdateGatePassCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		GatePassContext gatePassContext = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (gatePassContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);

			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS);

			if (CollectionUtils.isEmpty(gatePassContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			// setting current user to requestedBy
			if (gatePassContext.getIssuedBy() == null) {
				gatePassContext.setIssuedBy(AccountUtil.getCurrentUser());
			}
            
			if (gatePassContext.getId() > 0) {
				updateRecord(gatePassContext, module, fields);

				DeleteRecordBuilder<GatePassLineItemsContext> deleteBuilder = new DeleteRecordBuilder<GatePassLineItemsContext>()
						.module(lineModule).andCondition(CriteriaAPI.getCondition("GATE_PASS_ID", "gatePass",
								String.valueOf(gatePassContext.getId()), NumberOperators.EQUALS));
				deleteBuilder.delete();
			} else {
				if (gatePassContext.getIssuedTime() == -1) {
					gatePassContext.setIssuedTime(System.currentTimeMillis());
				}

				gatePassContext.setStatus(GatePassContext.Status.REQUESTED);
				addRecord(true, Collections.singletonList(gatePassContext), module, fields);
			}

			updateLineItems(gatePassContext);
			addRecord(false, gatePassContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));

			context.put(FacilioConstants.ContextNames.RECORD, gatePassContext);
		}
		return false;
	}

	private void updateLineItems(GatePassContext gatePassContext) {
		for (GatePassLineItemsContext lineItemContext : gatePassContext.getLineItems()) {
			lineItemContext.setGatePass(gatePassContext.getId());
		}
	}

	private void addRecord(boolean isLocalIdNeeded, List<? extends ModuleBaseWithCustomFields> list,
			FacilioModule module, List<FacilioField> fields) throws Exception {
		InsertRecordBuilder insertRecordBuilder = new InsertRecordBuilder<>().module(module).fields(fields);
		if (isLocalIdNeeded) {
			insertRecordBuilder.withLocalId();
		}
		insertRecordBuilder.addRecords(list);
		insertRecordBuilder.save();
	}

	public void updateRecord(ModuleBaseWithCustomFields data, FacilioModule module, List<FacilioField> fields)
			throws Exception {
		UpdateRecordBuilder updateRecordBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(data.getId(), module));
		updateRecordBuilder.update(data);
	}

}
