package com.facilio.bmsconsoleV3.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteWithoutScopingAction extends V3Action {
    private long id;

    public String fetchSite() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.fetchSiteDetailsWithoutScopingCommand();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        chain.execute();
        setData("site", context.get("site"));
        return SUCCESS;
    }
}
