package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DocumentContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DeleteDocumentsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<Long> docIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		
		if(CollectionUtils.isNotEmpty(docIds)) {
			DeleteRecordBuilder<DocumentContext> deleteBuilder = new DeleteRecordBuilder<DocumentContext>()
					.module(module);
			deleteBuilder.batchDeleteById(docIds);
		}
		return false;
	}

}
