package com.facilio.bmsconsole.modulemapping;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleMappingBaseInfoContext {
    private Long id;

    private V3PeopleContext sysCreatedByPeople;

    private V3PeopleContext sysModifiedByPeople;

    private V3PeopleContext sysDeletedByPeople;

    private Long sysDeletedTime;

    private Long sysModifiedTime;

    private Long sysCreatedTime;
}
