
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AltayerVendorPrimaryContactSiteValueGenerator extends ValueGenerator {

    @Override
    public Object generateValueForCondition(int appType) {
        if (appType == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            try {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                Long peopleId = AccountUtil.getCurrentUser().getPeopleId();
                FacilioModule vendorContactModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
                List<FacilioField> fields = modBean.getAllFields(vendorContactModule.getName());

                List<V3VendorContactContext> vendorContacts = V3RecordAPI.getRecordsListWithSupplements(vendorContactModule.getName(), Collections.singletonList(peopleId), V3VendorContactContext.class, null, null, null, null, true);
                if (CollectionUtils.isNotEmpty(vendorContacts)) {
                    List<Long> vcIds = vendorContacts.stream().map(V3VendorContactContext::getId).collect(Collectors.toList());
                    Criteria criteria = new Criteria();
                    criteria.addAndCondition(CriteriaAPI.getCondition("LEFT_ID", "left", StringUtils.join(vcIds, ","), NumberOperators.EQUALS));
                    List<RelRecord> relRecs = V3RecordAPI.getRecordsListWithSupplements("vendorcontact-site-accessible_sites_vendorcontact_1-rel", null, RelRecord.class, criteria, null, null, null, true);
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
                List<V3SiteContext> sites = V3RecordAPI.getRecordsListWithSupplements(siteModule.getName(), null, V3SiteContext.class, null, null, null, null, true);
                if (CollectionUtils.isNotEmpty(sites)) {
                    List<Long> siteIds = sites.stream().map(V3SiteContext::getId).collect(Collectors.toList());
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
        return "Altayer Vendor Primary Contact Site";
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AltayerVendorPrimaryContactSiteValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SITE;
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
