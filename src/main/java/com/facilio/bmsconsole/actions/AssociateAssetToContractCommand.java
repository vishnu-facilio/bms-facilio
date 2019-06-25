package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractAssociatedAssetsContext;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.util.ContractsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

public class AssociateAssetToContractCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		ContractsContext contractContext = (ContractsContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(contractContext.getStatus() == ContractsContext.Status.APPROVED.getValue()) {
			FacilioModule assetAssociatedModule = modBean.getModule(FacilioConstants.ContextNames.CONTRACT_ASSOCIATED_ASSETS);
			
			DeleteRecordBuilder<ContractAssociatedAssetsContext> deleteAssetRelationBuilder = new DeleteRecordBuilder<ContractAssociatedAssetsContext>()
					.module(assetAssociatedModule)
					.andCondition(CriteriaAPI.getCondition("CONTRACT_ID", "contractId", String.valueOf(contractContext.getId()), NumberOperators.EQUALS));
			deleteAssetRelationBuilder.delete();
		
			ContractsAPI.updateAssetsAssociated(contractContext);
		}
		else {
			throw new IllegalArgumentException("Assets can be associated to only approved contracts");
		}
	
		
		return false;
	}
	
	
	
}
