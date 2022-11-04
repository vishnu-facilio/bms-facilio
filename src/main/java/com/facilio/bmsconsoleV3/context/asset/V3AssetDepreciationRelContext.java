package com.facilio.bmsconsoleV3.context.asset;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V3AssetDepreciationRelContext extends V3Context {

    private AssetContext asset;
    private AssetDepreciationContext depreciation;
    private Double depreciationAmount;
    private Boolean activated;
    private Long lastCalculatedId;
}
