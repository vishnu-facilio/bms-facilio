package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class V3ControlActionAction extends V3Action {
    private Long controlActionId;

    public String v3ControlActionPublish() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put("controlActionId",controlActionId);
        FacilioChain chain = TransactionChainFactoryV3.getPublishControlActionChain();
        chain.setContext(context);
        chain.execute();
        return SUCCESS;
    }
    public String v3ControlActionUnPublish() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put("controlActionId",controlActionId);
        FacilioChain chain = TransactionChainFactoryV3.getUnPublishControlActionChain();
        chain.setContext(context);
        chain.execute();
        return SUCCESS;
    }

}
