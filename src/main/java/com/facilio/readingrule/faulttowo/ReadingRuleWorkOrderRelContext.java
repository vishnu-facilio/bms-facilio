package com.facilio.readingrule.faulttowo;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Collections;
import java.util.Map;

@Log4j
@Getter
@Setter

public class ReadingRuleWorkOrderRelContext extends WorkflowRuleContext {

    JSONObject comments;
    Long ruleId;
    Long ruleWoId;
    Boolean isSkip;
    Long woCriteriaId;
    Criteria woCriteria;
    Boolean isRecommendationAsTask;
    Boolean isPossibleCauseAsDesc;
    public JSONObject getComments() {
        return comments;
    }

    public void setComments(JSONObject jsonStr) {
        this.comments = jsonStr;
    }

    public String getCommentsJsonStr() {
        if (comments != null) {
            return comments.toJSONString();
        }
        return null;
    }

    public void setCommentsJsonStr(String jsonStr) throws ParseException {
        if (jsonStr != null) {
            JSONParser parser = new JSONParser();
            comments = (JSONObject) parser.parse(jsonStr);
        }
    }

    public Map<String, Object> constructPlaceHolders(String moduleName, Object record, Map<String, Object> recordPlaceHolders, FacilioContext context) throws Exception {
        BaseAlarmContext baseAlarm = (BaseAlarmContext) record;
        Long ruleId = ((ReadingAlarm) baseAlarm).getRule().getId();
        NewReadingRuleContext newReadingRule = NewReadingRuleAPI.getReadingRules(Collections.singletonList(ruleId)).get(0);
        recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(FacilioConstants.ReadingRules.NEW_READING_RULE, newReadingRule, recordPlaceHolders);
        Map<String, Object> rulePlaceHolders = super.constructPlaceHolders(moduleName, record, recordPlaceHolders, context);
        return rulePlaceHolders;
    }

}
