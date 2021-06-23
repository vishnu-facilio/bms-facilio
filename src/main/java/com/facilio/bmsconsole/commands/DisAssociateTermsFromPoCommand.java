package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PoAssociatedTermsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DisAssociateTermsFromPoCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		FacilioModule termsAssociatedModule = modBean.getModule(FacilioConstants.ContextNames.PO_ASSOCIATED_TERMS);
		DeleteRecordBuilder<PoAssociatedTermsContext> deleteTermsBuilder = new DeleteRecordBuilder<PoAssociatedTermsContext>()
				.module(termsAssociatedModule)
				.andCondition(CriteriaAPI.getIdCondition(recordIds, termsAssociatedModule));
		deleteTermsBuilder.delete();
		return false;
	}

}
