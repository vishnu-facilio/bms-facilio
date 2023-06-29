package com.facilio.bmsconsoleV3.context.workorder;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3WorkOrderModuleSettingContext extends V3Context {

    private Boolean hideGallery;
    private Long autoResolveStateId;
}
