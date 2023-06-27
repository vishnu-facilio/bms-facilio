package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PeopleSkillLevelContext extends V3Context {
    private Double rate;
    private int skill;
    private int peopleId;

    public  int getLevel() {
        return this.level != null ? this.level.getIndex() : -1;
    }
    public void setLevel(int level) {
        this.level = Level.valueOf(level);
    }
    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevelEnum() {
        return this.level;
    }

    private Level level;
    public static enum Level implements FacilioIntEnum {
        EXPERT,
        PROFICIENT,
        NOVICE,
        TRAINEE;

        private Level() {
        }

        public static Level valueOf(int value) {
            return value > 0 && value <= values().length ? values()[value - 1] : null;
        }

        public Integer getIndex() {
            return this.ordinal() + 1;
        }

        public String getValue() {
            return this.name();
        }
    }

}
