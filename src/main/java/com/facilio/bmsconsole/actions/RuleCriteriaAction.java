package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter@Setter
public class RuleCriteriaAction extends FacilioAction {
    private String moduleName;
    private Long criteriaId;
    private int page;
    private int perPage;
    private String recordIds;
    public String getCondition() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getRuleCriteria();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SpaceBooking.CRITERIAID,criteriaId);
        JSONObject pagination = new JSONObject();
        pagination.put("page", page);
        pagination.put("perPage", perPage);
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        chain.execute();
        setResult(FacilioConstants.ContextNames.CONDITIONS, context.get(FacilioConstants.ContextNames.CONDITIONS));
        return SUCCESS;
    }

    public String getRecordValue() throws Exception{
        FacilioChain chain =ReadOnlyChainFactory.getValueForRecordIds();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.ID,recordIds);
        chain.execute();
        setResult(FacilioConstants.ContextNames.RECORD_MAP, context.get(FacilioConstants.ContextNames.RECORD_MAP));

        return SUCCESS;
    }
}
