package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DocumentContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;

public class AddDocumentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<DocumentContext> documents = (List<DocumentContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		if(CollectionUtils.isNotEmpty(documents)) {
			InsertRecordBuilder<DocumentContext> attachmentBuilder = new InsertRecordBuilder<DocumentContext>()
					.module(module)
					.fields(modBean.getAllFields(moduleName))
					.addRecords(documents);

			attachmentBuilder.save();
		}
		return false;
	}

}
