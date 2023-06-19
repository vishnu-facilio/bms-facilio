package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.DecommissionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecommissionAction extends FacilioAction {
    private DecommissionContext decommission;
    public String updateDecommission() throws  Exception{
            FacilioUtil.throwIllegalArgumentException(decommission == null,"Fields cannot be null");
            FacilioUtil.throwIllegalArgumentException(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.COMMISSIONING),"COMMISSIONING LICENSE IS DISABLED");
            FacilioChain chain = TransactionChainFactoryV3.updateResourceDecommissionChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.DECOMMISSION, decommission);

            chain.execute();
            return SUCCESS;

    }
    public  String  getChildOfResource() throws Exception {

            FacilioUtil.throwIllegalArgumentException(decommission == null,"Fields cannot be null");

            FacilioChain chain = ReadOnlyChainFactoryV3.getDependentResources();
            FacilioContext context = chain.getContext();

            context.put(FacilioConstants.ContextNames.DECOMMISSION, decommission);

            chain.execute();
            setResult(FacilioConstants.ContextNames.RESOURCE_LIST,context.get(FacilioConstants.ContextNames.RESOURCE_LIST));
            return SUCCESS;

    }
}
