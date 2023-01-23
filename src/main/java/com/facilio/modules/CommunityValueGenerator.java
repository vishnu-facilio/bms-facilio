package com.facilio.modules;

import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommunityValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
            List<AudienceContext> audienceList = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.Tenant.AUDIENCE, null);
            if(CollectionUtils.isNotEmpty(audienceList)) {
                List<Long> ids = new ArrayList<>();
                for(AudienceContext audience : audienceList) {
                    ids.add(audience.getId());
                }
                return StringUtils.join(ids, ",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Community Modules";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.CommunityValueGenerator";
    }

    @Override
    public String getModuleName() {
        return "Community Modules";
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 36;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }
}
