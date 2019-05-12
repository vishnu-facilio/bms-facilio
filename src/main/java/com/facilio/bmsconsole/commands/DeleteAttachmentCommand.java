package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteAttachmentCommand implements Command, PostTransactionCommand {

	private Set<Long> idsToUpdateCount;
	private String moduleName;

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> attachmentIdList = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module =  modBean.getModule(moduleName);
		
		if (attachmentIdList != null && !attachmentIdList.isEmpty()) {
			
			SelectRecordsBuilder<AttachmentContext> attachmentBuilder = new SelectRecordsBuilder<AttachmentContext>()
					.select(modBean.getAllFields(moduleName))
					.module(module)
					.beanClass(AttachmentContext.class)
					.andCondition(CriteriaAPI.getIdCondition(attachmentIdList, module))
					;
			List<AttachmentContext> attachments = attachmentBuilder.get();
			if (attachments != null && !attachments.isEmpty()) {
				Set<Long> parentIds = attachments.stream().map(AttachmentContext::getParentId).collect(Collectors.toSet());
				idsToUpdateCount = parentIds;
			}
			
			// TODO mark file as deleted if no reference for that file is available for all modules
			if (moduleName.equals(FacilioConstants.ContextNames.ASSET_ATTACHMENTS)) {
				if (attachments != null && !attachments.isEmpty()) {
					List<Long> fileIds = attachments.stream().map(AttachmentContext::getFileId).collect(Collectors.toList());
					FileStoreFactory.getInstance().getFileStore().deleteFiles(fileIds);
				}
			}
			
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getIdCondition(attachmentIdList, module));

			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
		}
		
		return false;
	}
	
	@Override
	public boolean postExecute() throws Exception {
		AttachmentsAPI.updateAttachmentCount(idsToUpdateCount, moduleName);
		return false;
	}

}
