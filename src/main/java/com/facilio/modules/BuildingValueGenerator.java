package com.facilio.modules;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingValueGenerator extends ValueGenerator {
    @Override
    public Object generateValueForCondition(int appType) {
        FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
        GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
                .select(AccountConstants.getAccessbileSpaceFields())
                .table(accessibleSpaceMod.getTableName())
                .andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());
        List<Map<String, Object>> props = null;
        try {
            props = selectAccessibleBuilder.get();
            List<Long> baseSpaceIds = new ArrayList<Long>();
            if (props != null && !props.isEmpty()) {
                for (Map<String, Object> prop : props) {
                    Long bsId = (Long) prop.get("bsid");
                    Long siteId = (Long) prop.get("siteId");
                    if (bsId != null && siteId != null) {
                        if(bsId.equals(siteId)) {
                             List<BuildingContext> buildings = SpaceAPI.getSiteBuildings(siteId);
                             if(CollectionUtils.isNotEmpty(buildings)) {
                                 List<Long> ids = buildings.stream()
                                         .map(BuildingContext::getId)
                                         .collect(Collectors.toList());
                                 baseSpaceIds.addAll(ids);
                             }
                        }
                        else {
                            baseSpaceIds.add(bsId);
                        }
                    }
                }
                return StringUtils.join(baseSpaceIds, ",");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Object getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.BUILDING;
    }

    @Override
    public Object getLinkName() {
        return "com.facilio.modules.BuildingValueGenerator";
    }

    @Override
    public Object getModuleName() {
        return FacilioConstants.ContextNames.BUILDING;
    }

    @Override
    public Object getIsHidden() {
        return false;
    }

    @Override
    public Object getOperatorId() {
        return 36;
    }
}
