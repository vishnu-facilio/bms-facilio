package com.facilio.fsm.context;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PeopleSkillLevelContext extends V3Context {
    private ServiceSkillsContext skill;
    private V3PeopleContext people;
    private String successMsg;

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

    private String levelName;
    public String getLevelName() {
        if(this.level != null){
            return this.level.getDisplayName();
        }
        return null;
    }

    private Level level;
    public static enum Level implements FacilioIntEnum {
        EXPERT("Expert"),
        PROFICIENT("Proficient"),
        NOVICE("Novice"),
        TRAINEE("Trainee");

        String name;
        Level(String name) {
            this.name = name;
        }

        public static Level valueOf(int value) {
            return value > 0 && value <= values().length ? values()[value - 1] : null;
        }
        @Override
        public Integer getIndex() {
            return this.ordinal() + 1;
        }
        @Override
        public String getValue() {
            return this.name();
        }
        public String getDisplayName() {
            return name;
        }
    }

}
