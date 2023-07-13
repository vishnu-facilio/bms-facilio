package com.facilio.survey;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyAction extends FacilioAction {

    private Long recordId;
    private String moduleName;

    public String fetchSurvey() throws Exception{

        FacilioChain chain = ReadOnlyChainFactory.fetchSurveyChain();
        FacilioContext context = chain.getContext();
        context.put("moduleName",getModuleName());
        context.put("recordId",getRecordId());
        chain.execute();

        setResult("isSurveyAvailable",context.get("isSurveyAvailable"));
        setResult("isSuperAdmin",context.get("isSuperAdmin"));
        setResult("isViewAllSurvey",context.get("isViewAllSurvey"));
        setResult("response",context.get("response"));

        return SUCCESS;
    }

    public String getSurveySupportedModules() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getSurveySupportedModules();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.MODULE_LIST,context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

}
