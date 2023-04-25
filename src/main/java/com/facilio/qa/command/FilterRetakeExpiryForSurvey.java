package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.qa.context.ResponseContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;
import java.util.Map;

public class FilterRetakeExpiryForSurvey extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Object>> queryParameters = (Map<String, List<Object>>) context.get(Constants.QUERY_PARAMS);
        String moduleName = Constants.getModuleName(context);

        List<Object> viewAllSurveyParams = queryParameters.getOrDefault("isViewAllSurvey",null);

        if(CollectionUtils.isNotEmpty(viewAllSurveyParams)) {
            boolean isViewAllSurvey = Boolean.parseBoolean((String) viewAllSurveyParams.get(0));
            if(isViewAllSurvey){
                Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
                List<ResponseContext> responseContext = (List<ResponseContext>) recordMap.get(moduleName);
                if(responseContext!=null && !responseContext.isEmpty()) {
                    responseContext.removeIf(response ->(
                            (response.getExpiryDate() < DateTimeUtil.getCurrenTime() && (response.getResStatus() == ResponseContext.ResponseStatus.NOT_ANSWERED || response.getResStatus() == ResponseContext.ResponseStatus.PARTIALLY_ANSWERED))
                                    ||
                            (response.getResStatus() == ResponseContext.ResponseStatus.COMPLETED && response.getIsRetakeAllowed() && response.getRetakeExpiry()!=null && response.getRetakeExpiry() < DateTimeUtil.getCurrenTime())
                                    ||
                            (response.getResStatus() == ResponseContext.ResponseStatus.COMPLETED && !response.getIsRetakeAllowed())
                        )
                    );
                    context.put(Constants.COUNT,Long.valueOf(responseContext.size()));
                }
            }
        }
        return false;
    }
}
