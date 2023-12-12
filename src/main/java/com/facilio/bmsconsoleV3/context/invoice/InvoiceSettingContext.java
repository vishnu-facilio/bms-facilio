package com.facilio.bmsconsoleV3.context.invoice;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class InvoiceSettingContext implements Serializable {
    private static final long serialVersionUID = 1L;
    Long Id;
    Long orgId;
    private Boolean vendorInvoice;
    private Boolean enduserInvoice;
    private Boolean allowUserSeeMarkup;
    private Boolean canShowMarkupDefaultValue;
    Long markupDefaultValue;
    Long markupdisplayMode;
    private Boolean showMarkupValue;
    private Double globalMarkupValue;

    public enum markupdisplayMode {
        GLOBAL,
        LINEITEM;

        public int getValue() {
            return ordinal() + 1;
        }

        public static markupdisplayMode valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

}
