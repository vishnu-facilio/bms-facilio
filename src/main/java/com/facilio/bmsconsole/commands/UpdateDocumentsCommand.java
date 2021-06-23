package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DocumentContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;

public class UpdateDocumentsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<DocumentContext> documents = (List<DocumentContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		if(CollectionUtils.isNotEmpty(documents)) {
			for(DocumentContext doc : documents) {
				UpdateRecordBuilder<DocumentContext> updateRecordBuilder = new UpdateRecordBuilder<DocumentContext>()
						.module(module)
						.fields(modBean.getAllFields(moduleName))
						.andCondition(CriteriaAPI.getIdCondition(doc.getId(), module));
				updateRecordBuilder.update(doc);
			}
		}
		return false;
	}

}
