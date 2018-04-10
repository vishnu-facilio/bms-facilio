package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;

public class DeleteTicketDependenciesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			FacilioField field = FieldFactory.getAsMap(fields).get("parentId");
			
			SelectRecordsBuilder<AttachmentContext> attachmentBuilder = new SelectRecordsBuilder<AttachmentContext>()
																		.select(fields)
																		.module(module)
																		.beanClass(AttachmentContext.class)
																		.andCondition(CriteriaAPI.getCondition(field, recordIds, PickListOperators.IS))
																		;
			List<AttachmentContext> attachments = attachmentBuilder.get();
			if (attachments != null && !attachments.isEmpty()) {
				List<Long> fileIds = attachments.stream().map(AttachmentContext::getFileId).collect(Collectors.toList());
				FileStoreFactory.getInstance().getFileStore().deleteFiles(fileIds);
			}
		}
		return false;
	}

}
