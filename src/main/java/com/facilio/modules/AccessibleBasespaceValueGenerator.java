package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ClientAction;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.TenantUnitSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class AccessibleBasespaceValueGenerator extends ValueGenerator {

    @Override
    public Criteria getCriteria(FacilioField field, List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            if (CollectionUtils.isNotEmpty(value)) {
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE)), StringUtils.join(value, ","), NumberOperators.EQUALS));
            }
            return criteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    @Override
    public Object generateValueForCondition(int appType) {
        try {
            if (appType == AppDomain.AppDomainType.FACILIO.getIndex()) {
                return getAccessibleSites();
            }
            else if (appType == AppDomain.AppDomainType.TENANT_PORTAL.getIndex()) {
                V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
                if (tenant != null) {
                    if (tenant.getSiteId() > 0) {
                        return String.valueOf(tenant.getSiteId());
                    }
                    else {
                        List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
                        if(CollectionUtils.isNotEmpty(tenantUnits)) {
                            List<Long> siteIDs = new ArrayList<>();
                            for (TenantUnitSpaceContext tu :tenantUnits) {
                                siteIDs.add(tu.getSiteId());
                            }
                            return StringUtils.join(siteIDs, ",");
                        }
                    }
                }
            } else if (appType == AppDomain.AppDomainType.SERVICE_PORTAL.getIndex()) {
                V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId(), true);
                if (tenant != null) {
                    List<TenantUnitSpaceContext> tenantUnits = TenantsAPI.getTenantUnitsForTenant(tenant.getId());
                    if (CollectionUtils.isNotEmpty(tenantUnits)) {
                        List<Long> siteIDs = new ArrayList<>();
                        for (TenantUnitSpaceContext tu : tenantUnits) {
                            siteIDs.add(tu.getSiteId());
                        }
                        return StringUtils.join(siteIDs, ",");
                    }
                } else {
                    return getAccessibleSites();
                }
            }
            else if (appType == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {
                V3ClientContext client = V3PeopleAPI.getClientForUser(AccountUtil.getCurrentUser().getId(), true);
                if (client != null) {
                        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
                        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SITE);
                        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

                    ClientAction clientaction=new ClientAction();
                    List<V3SiteContext> clienSiteList= clientaction.getAssociatedClientSite(module,fields,client);
                        if(CollectionUtils.isNotEmpty(clienSiteList)) {
                            List<Long> siteIDs = new ArrayList<>();
                            for (V3SiteContext clientsite :clienSiteList) {
                                siteIDs.add(clientsite.getId());
                            }
                            return StringUtils.join(siteIDs, ",");
                        }

                }
            }
        } catch (Exception e) {e.printStackTrace();
        }

        return null;
    }

    private String getAccessibleSites() throws Exception {
        //handling user delegation
        List<User> delegatedUsers = new ArrayList<>();
        List<User> users = DelegationUtil.getUsers(AccountUtil.getCurrentAccount().getUser(), System.currentTimeMillis(), DelegationType.USER_SCOPING);
        if(CollectionUtils.isNotEmpty(users)) {
            delegatedUsers.addAll(users);
        }
        delegatedUsers.add(AccountUtil.getCurrentUser());

        List<Long> baseSpaceIds = new ArrayList<>();

        Map<Long,List<Long>> usersAccessibleSpaces = getAccessibleSpacesForUserMap(delegatedUsers);
        if(MapUtils.isNotEmpty(usersAccessibleSpaces)) {
            if(CollectionUtils.isNotEmpty(delegatedUsers)) {
                for (User delegatedUser : delegatedUsers) {
                    List<Long> accessibleSpaces = usersAccessibleSpaces.get(delegatedUser.getId());
                    if(CollectionUtils.isNotEmpty(accessibleSpaces)) {
                        baseSpaceIds.addAll(accessibleSpaces);
                    }
                    else {
                        return FacilioConstants.ContextNames.ALL_VALUE;
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(baseSpaceIds)) {
            return StringUtils.join(baseSpaceIds, ",");
        }
        return FacilioConstants.ContextNames.ALL_VALUE;
    }

    private Map<Long,List<Long>> getAccessibleSpacesForUserMap(List<User> users) throws Exception {
        Map<Long,List<Long>> userSpacesMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(users)) {
            List<Long> ouIds = users.stream().map(User::getOuid).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(ouIds)) {
                FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
                GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
                        .select(AccountConstants.getAccessbileSpaceFields())
                        .table(accessibleSpaceMod.getTableName())
                        .andCondition(CriteriaAPI.getCondition("ORG_USER_ID","ouid",StringUtils.join(ouIds,","),NumberOperators.EQUALS));
                List<Map<String, Object>> props = null;
                props = selectAccessibleBuilder.get();
                if (CollectionUtils.isNotEmpty(props)) {
                    for (Map<String, Object> prop : props) {
                        Long bsId = (Long) prop.get("bsid");
                        Long siteId = (Long) prop.get("siteId");
                        Long ouId = (Long) prop.get("ouid");
                        List<Long> spaces = null;
                        if(userSpacesMap.containsKey(ouId)) {
                            spaces = userSpacesMap.get(ouId);
                        }
                        else {
                            spaces = new ArrayList<>();
                        }
                        spaces.add(bsId);
                        if(!spaces.contains(siteId)) {
                            spaces.add(siteId);
                        }
                        userSpacesMap.put(ouId,spaces);
                    }
                }
            }
        }
        return userSpacesMap;
    }
    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.ACCESSIBLE_SPACES;
    }

    @Override
    public String getLinkName() {
        return "com.facilio.modules.AccessibleBasespaceValueGenerator";
    }

    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.BASE_SPACE;
    }

    @Override
    public Boolean getIsHidden() {
        return false;
    }

    @Override
    public Integer getOperatorId() {
        return 38;
    }

}
