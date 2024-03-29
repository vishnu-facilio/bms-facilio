package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class GetAddOrUpdateGatePassCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		GatePassContext gatePassContext = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (gatePassContext != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = modBean.getAllFields(moduleName);

			FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS);

			if (gatePassContext.getId() <= 0 && CollectionUtils.isEmpty(gatePassContext.getLineItems())) {
				throw new Exception("Line items cannot be empty");
			}
			// setting current user to requestedBy
			if (gatePassContext.getIssuedBy() == null) {
				gatePassContext.setIssuedBy(AccountUtil.getCurrentUser());
			}
            
			if (gatePassContext.getId() > 0) {
				RecordAPI.updateRecord(gatePassContext, module, fields);
                if(gatePassContext.getLineItems() != null) {
					DeleteRecordBuilder<GatePassLineItemsContext> deleteBuilder = new DeleteRecordBuilder<GatePassLineItemsContext>()
							.module(lineModule).andCondition(CriteriaAPI.getCondition("GATE_PASS_ID", "gatePass",
									String.valueOf(gatePassContext.getId()), NumberOperators.EQUALS));
					deleteBuilder.delete();
					updateLineItems(gatePassContext);
					RecordAPI.addRecord(false, gatePassContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
                }
			} else {
				if (gatePassContext.getIssuedTime() == -1) {
					gatePassContext.setIssuedTime(System.currentTimeMillis());
				}

				gatePassContext.setStatus(GatePassContext.Status.REQUESTED);
				RecordAPI.addRecord(true, Collections.singletonList(gatePassContext), module, fields);
				updateLineItems(gatePassContext);
				RecordAPI.addRecord(false, gatePassContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
			}

		
			context.put(FacilioConstants.ContextNames.RECORD, gatePassContext);
		}
		return false;
	}

	private void updateLineItems(GatePassContext gatePassContext) throws Exception {
		List<GatePassLineItemsContext> newLineItems = new ArrayList<GatePassLineItemsContext>();
		for (GatePassLineItemsContext lineItemContext : gatePassContext.getLineItems()) {
			lineItemContext.setGatePass(gatePassContext.getId());
			if(CollectionUtils.isNotEmpty(lineItemContext.getAssetIds())) {
				lineItemContext.setAsset(AssetsAPI.getAssetInfo(lineItemContext.getAssetIds().get(0)));
				lineItemContext.setQuantity(1);
				for(int i=1;i<lineItemContext.getAssetIds().size();i++) {
					GatePassLineItemsContext gatePassItem = new GatePassLineItemsContext();
					gatePassItem.setInventoryType(lineItemContext.getInventoryType());
					gatePassItem.setItemType(lineItemContext.getItemType());
					gatePassItem.setToolType(lineItemContext.getToolType());
					gatePassItem.setGatePass(gatePassContext.getId());
					gatePassItem.setAsset(AssetsAPI.getAssetInfo(lineItemContext.getAssetIds().get(i)));
					gatePassItem.setQuantity(1);
					newLineItems.add(gatePassItem);
				}
			}
		}
		gatePassContext.getLineItems().addAll(newLineItems);
	}

	
}
