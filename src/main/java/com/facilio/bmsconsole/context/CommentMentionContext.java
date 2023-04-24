package com.facilio.bmsconsole.context;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentMentionContext extends ModuleBaseWithCustomFields {
    private static final long serialVersionUID = 1L;

    private long parentId = -1;
    private long parentModuleId = -1;
    private long mentionedModuleId = -1;
    private long mentionedRecordId = -1;
    private String mentionedModuleName;
    private Object recordObj;
    private MentionType mentionType;
    public int getMentionType() {
        if (mentionType != null) {
            return mentionType.getIndex();
        }
        return -1;
    }
    public MentionType getMentionTypeEnum() {
        return mentionType;
    }
    public void setMentionType(MentionType mentionType) {
        this.mentionType = mentionType;
    }
    public void setMentionType(int mentionType) {
        this.mentionType = MentionType.valueOf(mentionType);
    }

    public enum MentionType implements FacilioIntEnum {
        PEOPLE("people"),
        ROLE("role"),
        GROUP("group"),
        RECORD("record");
        String name;

        MentionType(String name) {
            this.name = name;
        }

        public static MentionType valueOf(int mentionType) {
            if (mentionType > 0 && mentionType <= values().length) {
                return values()[mentionType - 1];
            }
            return null;
        }
    }


}
