package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkorderPartsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetWorkorderPartsListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule workorderPartsModule = modBean.getModule(moduleName);
			List<FacilioField> workorderPartsFields = modBean.getAllFields(moduleName);
			Map<String, FacilioField> workorderPartsFieldMap = FieldFactory.getAsMap(workorderPartsFields);
			long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
			SelectRecordsBuilder<WorkorderPartsContext> selectBuilder = new SelectRecordsBuilder<WorkorderPartsContext>()
					.select(workorderPartsFields).table(workorderPartsModule.getTableName())
					.moduleName(workorderPartsModule.getName()).beanClass(WorkorderPartsContext.class)
					.andCondition(CriteriaAPI.getCondition(workorderPartsFieldMap.get("parentId"),
							String.valueOf(parentId), PickListOperators.IS));

			List<WorkorderPartsContext> workorderParts = selectBuilder.get();
			context.put(FacilioConstants.ContextNames.WORKORDER_PART_LIST, workorderParts);
		}
		return false;
	}

}
