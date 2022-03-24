package com.facilio.readingrule.context;

import com.facilio.bmsconsole.enums.FaultType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RuleAlarmDetails {

    Long id;

    Long ruleId;

    String message;

    int faultType;

    public void setFaultType(int faultType) {
        this.faultType = faultType;
        this.faultTypeEnum = FaultType.valueOf(faultType);
    }

    FaultType faultTypeEnum;

    public void setFaultTypeEnum(FaultType faultTypeEnum) {
        this.faultTypeEnum = faultTypeEnum;
        this.faultType = faultTypeEnum.getIndex();
    }

    String severity;

    Long severityId;

    String problem;

    String possibleCausesStr;
    List<String> possibleCauses;

    public void setPossibleCauses(List<String> possibleCauses) {
        this.possibleCauses = possibleCauses;
        if (possibleCauses != null) {
            JSONArray arr = new JSONArray();
            for (String el : possibleCauses) {
                arr.add(el);
            }
            this.possibleCausesStr = arr.toJSONString();
        }
    }

    public List<String> getPossibleCauses() throws ParseException {
        if (CollectionUtils.isNotEmpty(possibleCauses)) {
            return possibleCauses;
        }
        List<String> causes = new ArrayList<>();
        if (possibleCausesStr != null) {
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(possibleCausesStr);
            for (int i = 0; i < arr.size(); i++) {
                causes.add((String) arr.get(i));
            }
        }
        this.possibleCauses = causes;
        return causes;
    }

    String recommendationsStr;
    List<String> recommendations;

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
        if (RuleAlarmDetails.this.recommendations != null) {
            JSONArray arr = new JSONArray();
            for (String el : RuleAlarmDetails.this.recommendations) {
                arr.add(el);
            }
            this.recommendationsStr = arr.toJSONString();
        }
    }

    public List<String> getRecommendations() throws ParseException {
        if (CollectionUtils.isNotEmpty(recommendations)) {
            return recommendations;
        }
        List<String> recoms = new ArrayList<>();
        if (recommendationsStr != null) {
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(recommendationsStr);
            for (int i = 0; i < arr.size(); i++) {
                recoms.add((String) arr.get(i));
            }
        }
        this.recommendations = recoms;
        return recoms;
    }

    public void setNullForResponse() {
        recommendationsStr = null;
        possibleCausesStr = null;
    }
}
