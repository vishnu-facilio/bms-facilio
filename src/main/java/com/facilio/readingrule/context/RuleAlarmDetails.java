package com.facilio.readingrule.context;

import com.facilio.bmsconsole.enums.FaultType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RuleAlarmDetails implements Serializable {

    Long id;

    Long ruleId;

    String message;

    int faultType;

    public void setFaultType(int faultType) {
        this.faultType = faultType;
        this.faultTypeEnum = FaultType.valueOf(faultType);
    }

    FaultType faultTypeEnum;

    public FaultType getFaultTypeEnum() {
        return FaultType.valueOf(faultType);
    }

    String severity;

    Long severityId;

    String problem;

    String possibleCausesStr;
    List<String> possibleCauses;

    public void setPossibleCausesStr(String possibleCausesStr) throws ParseException {
        List<String> causes = new ArrayList<>();
        if (possibleCausesStr != null && !possibleCausesStr.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(possibleCausesStr);
            for (int i = 0; i < arr.size(); i++) {
                causes.add((String) arr.get(i));
            }
        }
        this.possibleCauses = causes;
    }

    public void setPossibleCauses(List<String> possibleCauses) {
        this.possibleCauses = possibleCauses;
        if (possibleCauses != null ) {
            JSONArray arr = new JSONArray();
            for (String el : possibleCauses) {
                arr.add(el);
            }
            this.possibleCausesStr = arr.toJSONString();
        }
    }

    String recommendationsStr;
    List<String> recommendations;

    public void setRecommendationsStr(String recommendationsStr) throws ParseException {
        List<String> recoms = new ArrayList<>();
        if (recommendationsStr != null && !recommendationsStr.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(recommendationsStr);
            for (int i = 0; i < arr.size(); i++) {
                recoms.add((String) arr.get(i));
            }
        }
        this.recommendations = recoms;
    }

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

    public void setNullForResponse() {
        possibleCausesStr = null;
        recommendationsStr = null;
    }
}
