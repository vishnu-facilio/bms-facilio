package com.facilio.modules;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class SiteTenantValueGenerator extends ValueGenerator{
    @Override
    public Object generateValueForCondition(int appType) {
        try {
//            List<V3TenantContext> tenants = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.TENANT, null, V3TenantContext.class);
//            List<Long> tenantIds = tenants.stream().map(V3TenantContext::getId).collect(Collectors.toList());
//            return StringUtils.join(tenantIds, ",");
            List<Object> values = new ArrayList<Object>();
            Long currentSiteId = (Long) AccountUtil.getSwitchScopingFieldValue("siteId");
            if (AccountUtil.getShouldApplySwitchScope() && currentSiteId != null && currentSiteId > 0) {
                values.add(currentSiteId);
            } else if (appType == AppDomain.AppDomainType.FACILIO.getIndex()) {
                List<BaseSpaceContext> sites = getMySites();
                if (CollectionUtils.isNotEmpty(sites)) {
                    for (BaseSpaceContext site : sites) {
                        values.add(site.getId());
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(values)){
                Criteria criteria = new Criteria();
                criteria.addOrCondition(CriteriaAPI.getCondition("SITE_ID", "siteId", StringUtils.join(values,","), NumberOperators.EQUALS));
                List<V3TenantContext> tenants = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TENANT,null, V3TenantContext.class,criteria,null);
                List<Long> tenantIds = tenants.stream().map(V3TenantContext::getId).collect(Collectors.toList());
                return StringUtils.join(tenantIds, ",");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public List<BaseSpaceContext> getMySites() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
        List<Long> siteIds = new ArrayList<>();
        if (AccountUtil.getCurrentUser() != null) {
            FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
            GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
                    .select(AccountConstants.getAccessbileSpaceFields())
                    .table(accessibleSpaceMod.getTableName())
                    .andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());
            List<Map<String, Object>> props = selectAccessibleBuilder.get();
            if (props != null && !props.isEmpty()) {
                for(Map<String, Object> prop : props) {
                    Long siteId = (Long) prop.get("siteId");
                    if (siteId != null) {
                        siteIds.add(siteId);
                    }
                }
            }
        }
        SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
                .select(fields)
                .module(module)
                .skipScopeCriteria()
                .beanClass(BaseSpaceContext.class)
                .andCondition(CriteriaAPI.getCondition("SPACE_TYPE", "spaceType", String.valueOf(BaseSpaceContext.SpaceType.SITE.getIntVal()), NumberOperators.EQUALS));
        List<BaseSpaceContext> accessibleBaseSpace;
        if (siteIds.isEmpty()) {
            accessibleBaseSpace = selectBuilder.get();
        } else {
            accessibleBaseSpace = selectBuilder.andCondition(CriteriaAPI.getIdCondition(siteIds, module)).get();
        }
        if (accessibleBaseSpace == null || accessibleBaseSpace.isEmpty()) {
            return Collections.emptyList();
        }
        return accessibleBaseSpace;
    }
    @Override
    public String getValueGeneratorName() {
        return FacilioConstants.ContextNames.ValueGenerators.SITE_TENANT;
    }
    @Override
    public String getLinkName() {
        return "com.facilio.modules.SiteTenantValueGenerator";
    }
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.TENANT;
    }
    @Override
    public Boolean getIsHidden() {
        return false;
    }
    @Override
    public Integer getOperatorId() {
        return 36;
    }

    @Override
    public Criteria getCriteria(FacilioField field,List<Long> value) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Criteria criteria = new Criteria();
            if(CollectionUtils.isNotEmpty(value)) {
                criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(modBean.getModule(FacilioConstants.ContextNames.TENANT)), StringUtils.join(value, ","), NumberOperators.EQUALS));
            }
            return criteria;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }
}