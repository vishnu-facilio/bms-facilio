package com.facilio.readingrule.faulttowo;

import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Log4j
@Getter
@Setter

public class ReadingRuleWorkOrderRelContext extends V3Context {

    JSONObject comments;
    Long ruleId;
    WorkflowRuleContext workflowRule;
    Long workFlowRuleId;
    Long ruleWoId;
    Long criteriaId;
    Criteria criteria;
    Boolean isSkip;

    public JSONObject  getComments(){
        return comments;
    }
    public void setComments(JSONObject jsonStr){
        this.comments=jsonStr;
    }
    public String getCommentsJsonStr() {
        if(comments != null) {
            return comments.toJSONString();
        }
        return null;
    }
    public void setCommentsJsonStr(String jsonStr) throws ParseException {
        if(jsonStr != null) {
            JSONParser parser = new JSONParser();
            comments = (JSONObject) parser.parse(jsonStr);
        }
    }

}
