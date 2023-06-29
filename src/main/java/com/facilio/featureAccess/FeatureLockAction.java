package com.facilio.featureAccess;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.constants.FacilioConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.chain.FacilioChain;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j
public class FeatureLockAction extends FacilioAction {
    public String updateFeatureLock() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String[] idStingArr = request.getParameterValues("ids[]");
        long orgId = Long.parseLong(request.getParameter("orgId"));
        int feature = Integer.parseInt(request.getParameter("feature"));
        boolean locked = Boolean.parseBoolean(request.getParameter("locked"));

        AccountUtil.setCurrentAccount(orgId);

        List<Long> ids = new ArrayList<>();
        if (idStingArr != null && idStingArr.length > 0) {
            ids = Arrays.stream(idStingArr).map(Long::valueOf).collect(Collectors.toList());

            FacilioChain updateChain = TransactionChainFactory.getUpdateFeatureLockChain();
            FacilioContext context = updateChain.getContext();
            context.put(FacilioConstants.ContextNames.ORGID, orgId);
            context.put(FacilioConstants.FeatureAccessConstants.RECORD_IDS, ids);
            context.put(FacilioConstants.FeatureAccessConstants.FEATURE, feature);
            context.put(FacilioConstants.FeatureAccessConstants.ACCESS_PERMISSION, locked);
            updateChain.execute();
        }

        AccountUtil.cleanCurrentAccount();

        setResult("result", "success");
        ServletActionContext.getResponse().setStatus(200);

        return SUCCESS;
    }
}
