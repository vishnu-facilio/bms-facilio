package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;

public class GetTicketStatusListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);

		Boolean approvalStatus = (Boolean) context.get(FacilioConstants.ContextNames.APPROVAL_STATUS);

		List<FacilioStatus> statuses;
		if (approvalStatus != null && approvalStatus) {
			statuses = TicketAPI.getAllApprovalStatus();
		}
		else {
			FacilioModule module = null;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (StringUtils.isNotBlank(parentModuleName)) {
				module = modBean.getModule(parentModuleName);
			}
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

//		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
//														.table(dataTableName)
//														.moduleName(moduleName)
//														.beanClass(FacilioStatus.class)
//														.select(fields)
//														.orderBy("ID");

			if (module == null) {
				statuses = TicketAPI.getAllStatus(false);
			} else {
				statuses = TicketAPI.getAllStatus(module, false);
			}
		}
		context.put(FacilioConstants.ContextNames.TICKET_STATUS_LIST, statuses);
		
		return false;
	}

}
