package com.facilio.bmsconsoleV3.context.SFG20JobPlan;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SFG20SettingsContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String customerKey;
    private String customerSecret;
    private String description;
}
