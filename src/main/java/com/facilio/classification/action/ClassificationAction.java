package com.facilio.classification.action;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ClassificationAction extends V3Action {

    private Boolean status;
    private Long classificationId=-1l;
    public String changeStatus() throws Exception {
        FacilioChain chain = TransactionChainFactory.getChangeStatusForClassificationChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.CLASSIFICATION_ID,classificationId);
        context.put(FacilioConstants.ContextNames.STATUS,status);

        chain.execute();
        setData("result","success");
        return SUCCESS;
    }

}
