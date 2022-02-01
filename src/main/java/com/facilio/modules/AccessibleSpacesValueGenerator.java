package com.facilio.modules;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccessibleSpacesValueGenerator extends ValueGenerator {
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
                        baseSpaceIds.add(bsId);
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
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.ACCESSIBLE_SPACES;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AccessibleSpacesValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.BUILDING;
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 38;
    }

}
