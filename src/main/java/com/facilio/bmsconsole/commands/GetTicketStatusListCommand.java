package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GetTicketStatusListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
		
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
		
		List<FacilioStatus> statuses;
		if (module == null) {
			statuses = TicketAPI.getAllStatus(false);
		} else {
			statuses = TicketAPI.getAllStatus(module, false);
		}
		context.put(FacilioConstants.ContextNames.TICKET_STATUS_LIST, statuses);
		
		return false;
	}

}
