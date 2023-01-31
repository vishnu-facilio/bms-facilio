package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class V3CustomKioskButtonContext extends V3Context {
    private static final long serialVersionUID = 1L;
    private String label;
    private long icon=-1;
    private Long connectedAppWidgetId;
    private List<ConnectedAppWidgetContext> connectedAppWidgetContext;
    private Integer buttonType;

    private ButtonType buttonTypeEnum;

    public enum ButtonType implements FacilioIntEnum {
        CHECK_IN("Check In"),
        CHECK_OUT("Check Out")
        ;

        private String name;
        ButtonType (String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static ButtonType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }




}
