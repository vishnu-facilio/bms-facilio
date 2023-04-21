package com.facilio.featureAccess;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.constants.FacilioConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.chain.FacilioChain;
import lombok.extern.log4j.Log4j;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.ServletActionContext;

import java.util.List;

@Setter @Getter @Log4j
public class FeatureLockAction extends FacilioAction {
    private long orgId;
    private int feature;
    private boolean locked;
    private List<Long> ids;

    public String updateFeatureLock() throws Exception {
        AccountUtil.setCurrentAccount(orgId);
        
        FacilioChain updateChain = TransactionChainFactory.getUpdateFeatureLockChain();
        FacilioContext context = updateChain.getContext();
        context.put(FacilioConstants.FeatureAccessConstants.RECORD_IDS, ids);
        context.put(FacilioConstants.FeatureAccessConstants.FEATURE, feature);
        context.put(FacilioConstants.FeatureAccessConstants.ACCESS_PERMISSION, locked);
        updateChain.execute();

        AccountUtil.cleanCurrentAccount();

        setResult("result", "success");
        ServletActionContext.getResponse().setStatus(200);

        return SUCCESS;
    }
}
