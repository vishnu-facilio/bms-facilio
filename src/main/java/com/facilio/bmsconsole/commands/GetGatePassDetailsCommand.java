package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.GatePassContext;
import com.facilio.bmsconsole.context.GatePassLineItemsContext;
import com.facilio.bmsconsole.context.PurchaseOrderLineItemContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetGatePassDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		GatePassContext gatePassContext = (GatePassContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(gatePassContext!=null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.GATE_PASS_LINE_ITEMS);
			Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
			
			SelectRecordsBuilder<GatePassLineItemsContext> builder = new SelectRecordsBuilder<GatePassLineItemsContext>()
					.module(module)
					.select(fields)
					.beanClass(GatePassLineItemsContext.class)
					.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("gatePass"), String.valueOf(gatePassContext.getId()), NumberOperators.EQUALS))
			        .fetchLookups(Arrays.asList((LookupField) fieldsAsMap.get("itemType"),
					(LookupField) fieldsAsMap.get("toolType")))
			        ;
			List<GatePassLineItemsContext> list = builder.get();
			gatePassContext.setLineItems(list);
			context.put(FacilioConstants.ContextNames.GATE_PASS, gatePassContext);
		}
		return false;
	}

}
