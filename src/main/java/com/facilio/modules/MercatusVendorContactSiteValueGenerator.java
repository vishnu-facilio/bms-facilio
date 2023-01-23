
package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MercatusVendorContactSiteValueGenerator extends ValueGenerator {

    @Override
    public Object generateValueForCondition(int appType) {
        if (appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
                FacilioModule vendorcontact = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
                List<FacilioField> fields = modBean.getAllFields(vendorcontact.getName());

                if (peopleId != null) {
                    Criteria criteria = new Criteria();
                    criteria.addAndCondition(CriteriaAPI.getCondition("LEFT_ID", "left", String.valueOf(peopleId), NumberOperators.EQUALS));
                    List<RelRecord> relRecs = V3RecordAPI.getRecordsListWithSupplements("vendorcontact-site-accessible_sites_vendorcontact-rel", null, RelRecord.class, criteria, null, null, null, true);
                    if (CollectionUtils.isNotEmpty(relRecs)) {
                        List<Long> siteIds = new ArrayList<>();
                        for (RelRecord relRecord : relRecs) {
                            Map<String, Object> site = (Map<String, Object>) relRecord.getRight();
                            siteIds.add((Long) site.get("id"));
                        }
                        return StringUtils.join(siteIds, ",");
                    }
                }
                FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
                List<V3FloorContext> sites = V3RecordAPI.getRecordsListWithSupplements(siteModule.getName(), null, V3FloorContext.class, null, null, null, null, true);
                if (CollectionUtils.isNotEmpty(sites)) {
                    List<Long> siteIds = sites.stream().map(V3FloorContext::getId).collect(Collectors.toList());
                    return StringUtils.join(siteIds, ",");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public String getValueGeneratorName() {
        return "Mercatus Vendor Contact Site Value Generator";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.MercatusVendorContactSiteValueGenerator";
    }

    @Override
    public String getModuleName() {
        return "site";
    }

    @Override
    public Boolean getIsHidden() {
        return true;
    }

    @Override
    public Integer getOperatorId() {
        return 9;
    }

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> values) {
        return null;
    }

}
