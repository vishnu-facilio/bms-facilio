package com.facilio.bmsconsoleV3.commands.purchaserequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.PurchaseRequestContext;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
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
import com.facilio.v3.context.Constants;

public class PurchaseRequestTotalCostRollUpCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseRequestContext> purchaseRequestContexts = recordMap.get(moduleName);
        if(purchaseRequestContexts != null && CollectionUtils.isNotEmpty(purchaseRequestContexts)) {
            for(V3PurchaseRequestContext purchaseRequestContext : purchaseRequestContexts) 
            { 
            	if (purchaseRequestContext != null && purchaseRequestContext.getId() > 0) {
        			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
        			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST);
        			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        			
        			List<FacilioField> updatedFields = new ArrayList<FacilioField>();
        			updatedFields.add(fieldsMap.get("totalCost"));
        			//get total cost from line items
        			double totalCost = getTotalCost(purchaseRequestContext.getId());
        			
        			purchaseRequestContext.setTotalCost(totalCost);
        			Map<String, Object> map = new HashMap<String, Object>();
        			map.put("totalCost", totalCost);
        			
        			//update total cost for purchase request
        			UpdateRecordBuilder<V3PurchaseRequestContext> updateBuilder = new UpdateRecordBuilder<V3PurchaseRequestContext>()
        					.module(module).fields(updatedFields)
        					.andCondition(CriteriaAPI.getIdCondition(purchaseRequestContext.getId(), module));
        			updateBuilder.updateViaMap(map);
        		}     	
            }
        }
		return false;
	}

	private double getTotalCost(long id) throws Exception {
		if (id <= 0) {
			return 0d;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
		List<FacilioField> linefields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(linefields);
		
		List<FacilioField> field = new ArrayList<>();
		field.add(FieldFactory.getField("totalItemsCost", "sum(COST)", FieldType.DECIMAL));

		SelectRecordsBuilder<V3PurchaseRequestLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseRequestLineItemContext>()
				.select(field).moduleName(lineModule.getName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("prid"), String.valueOf(id), NumberOperators.EQUALS))
				.setAggregation()
				;

		List<Map<String, Object>> rs = builder.getAsProps();
		if (rs != null && rs.size() > 0) {
			if (rs.get(0).get("totalItemsCost") != null) {
				return (double) rs.get(0).get("totalItemsCost");
			}
			return 0d;
		}
		return 0d;
	}

}

