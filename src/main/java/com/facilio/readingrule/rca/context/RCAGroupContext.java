package com.facilio.readingrule.rca.context;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RCAGroupContext extends V3Context {

    String name;

    String desc;

    Long rcaId;

    Long criteriaId;

    Criteria criteria;

    ScoreType scoreType;

    Long scoreRange;

    Boolean status;

    List<RCAConditionScoreContext> conditions;

    public enum ScoreType implements FacilioIntEnum {
        PERCENTAGE("Percentage"),
        RANGE("Range"),
        ;

        private String name;

        ScoreType(String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public static ScoreType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

}


