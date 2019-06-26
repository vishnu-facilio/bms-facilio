package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;

public class DisAssociateAssetToContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		FacilioModule assetAssociatedModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
		DeleteRecordBuilder<ContractAssociatedAssetsContext> deleteAssetsBuilder = new DeleteRecordBuilder<ContractAssociatedAssetsContext>()
				.module(assetAssociatedModule)
				.andCondition(CriteriaAPI.getIdCondition(recordIds, assetAssociatedModule));
		deleteAssetsBuilder.delete();
		return false;
	}

}
