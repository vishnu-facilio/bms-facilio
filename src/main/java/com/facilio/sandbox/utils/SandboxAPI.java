package com.facilio.sandbox.utils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.componentpackage.bean.OrgSwitchBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.function.Unchecked;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.util.RequestUtil;
import com.opensymphony.xwork2.ActionContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facilio.backgroundactivity.util.BackgroundActivityAPI.isActivityLicenseEnabled;

@Log4j
public class SandboxAPI {
    public static boolean isSandboxSubDomain(String domain) {
        return StringUtils.isNotEmpty(domain) && domain.endsWith(FacilioProperties.getSandboxSubDomain());
    }

    public static Organization getAccessibleSandboxOrg(long userId) throws Exception {
        List<Organization> accessibleSandboxOrgs = AccountUtil.getUserBean().getOrgs(userId, Organization.OrgType.SANDBOX);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(accessibleSandboxOrgs)) {
            return accessibleSandboxOrgs.get(0);
        }
        return null;
    }

    public static SandboxConfigContext getSandboxById(long sandboxId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(sandboxId, ModuleFactory.getFacilioSandboxModule()));

        SandboxConfigContext sandboxConfig = getSandboxByCriteria(criteria);
        if(sandboxConfig != null) {
            setSandboxDomain(sandboxConfig);

            FacilioModule sandboxSharingModule = ModuleFactory.getSandboxSharingModule();
            List<FacilioField> sandboxSharingFields = FieldFactory.getSandboxSharingFields();
            SharingContext<SingleSharingContext> sharingMap = SharingAPI.getSharing(sandboxId, sandboxSharingModule,
                    SingleSharingContext.class, sandboxSharingFields);

            sandboxConfig.setSandboxSharing(sharingMap);
        }
        return sandboxConfig;
    }

    public static SandboxConfigContext getSandboxByDomainName(String domainName) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("DOMAIN_NAME", "subDomain", domainName, StringOperators.IS));

        SandboxConfigContext sandboxConfig = getSandboxByCriteria(criteria);
        if(sandboxConfig != null) {
            setSandboxDomain(sandboxConfig);

            FacilioModule sandboxSharingModule = ModuleFactory.getSandboxSharingModule();
            List<FacilioField> sandboxSharingFields = FieldFactory.getSandboxSharingFields();
            SharingContext<SingleSharingContext> sharingMap = SharingAPI.getSharing(sandboxConfig.getId(), sandboxSharingModule,
                    SingleSharingContext.class, sandboxSharingFields);

            sandboxConfig.setSandboxSharing(sharingMap);
        }
        return sandboxConfig;
    }

    public static SandboxConfigContext getSandboxByCriteria(Criteria criteria) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        List<FacilioField> sandboxFields = FieldFactory.getFacilioSandboxFields();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(sandboxModule.getTableName())
                .select(sandboxFields)
                .andCriteria(criteria);

        Map<String, Object> props = selectBuilder.fetchFirst();

        if (MapUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanFromMap(props, SandboxConfigContext.class);
        }
        return null;
    }

    public static List<SandboxConfigContext> getAllSandbox(int page, int perPage, String search) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName());

        if (StringUtils.isNotEmpty(search)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", search, StringOperators.CONTAINS));
        }
        if (page <= 0) {
            page = 1;
            perPage = 50;
        }
        int offset = ((page - 1) * perPage);
        selectBuilder.offset(offset);
        selectBuilder.limit(perPage);

        List<Map<String, Object>> props = selectBuilder.get();
        List<Long> sandboxIds = props.stream()
                .map(prop -> (Long) prop.get("id"))
                .collect(Collectors.toList());
        FacilioModule sandboxSharingModule = ModuleFactory.getSandboxSharingModule();
        List<FacilioField> sandboxSharingFields = FieldFactory.getSandboxSharingFields();
        if (!CollectionUtils.isEmpty(props)) {
            Map<Long, SharingContext<SingleSharingContext>> sharingMap = SharingAPI.getSharing(sandboxIds, sandboxSharingModule,
                    SingleSharingContext.class, sandboxSharingFields);
            List<SandboxConfigContext> sandboxList = FieldUtil.getAsBeanListFromMapList(props, SandboxConfigContext.class);
            for (SandboxConfigContext sandboxConfig : sandboxList) {
                setSandboxDomain(sandboxConfig);
                sandboxConfig.setSandboxSharing(sharingMap.get(sandboxConfig.getId()));
            }
            return sandboxList;
        }
        return null;
    }

    public static long getSandboxCount(String search) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getCountField())
                .table(sandboxModule.getTableName());
        if (StringUtils.isNotEmpty(search)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", search, StringOperators.CONTAINS));
        }
        Map<String, Object> sandboxCountMap = selectBuilder.fetchFirst();
        long count = MapUtils.isNotEmpty(sandboxCountMap) ? (long) sandboxCountMap.get("count") : 0;
        return count;
    }

    public static void changeSandboxStatus(SandboxConfigContext sandboxConfig) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        Map<String, FacilioField> sandboxFieldMap = FieldFactory.getAsMap(FieldFactory.getFacilioSandboxFields());
        fields.add(sandboxFieldMap.get("id"));
        fields.add(sandboxFieldMap.get("status"));

        SandboxAPI.updateSandboxConfig(sandboxConfig, fields);
    }

    public static int updateSandboxConfig(SandboxConfigContext sandboxConfig, List<FacilioField> updateFields) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(updateFields)
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(sandboxConfig.getId(), sandboxModule));

        Map<String, Object> props = FieldUtil.getAsProperties(sandboxConfig);
        return updateBuilder.update(props);
    }

    public static void setSandboxDomain(SandboxConfigContext sandboxConfig) throws Exception {
        String subDomain = sandboxConfig.getSubDomain();
        Organization productionOrg = IAMOrgUtil.getOrg(AccountUtil.getCurrentOrg().getOrgId());
        StringBuilder builder = new StringBuilder();
        HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
        builder.append(RequestUtil.getProtocol(request))
                .append("://")
                .append(productionOrg.getDomain() + "." + FacilioProperties.getSandboxSubDomain()+"/"+subDomain);
        sandboxConfig.setFullDomain(builder.toString());
    }

    public static boolean checkSandboxDomainIfExist(String domainName) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("DOMAIN_NAME", "subDomain", domainName, StringOperators.IS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if (props != null && props.containsKey("subDomain")) {
            boolean avail = String.valueOf(props.get("subDomain")).equalsIgnoreCase(domainName);
            if (avail) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    public static boolean checkSandboxRestrictionCondition() throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        List<Long> restrictStatuses = new ArrayList<>();
        restrictStatuses.add(3L);
        restrictStatuses.add(4L);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(restrictStatuses, ","), NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            return true;
        }
        return false;
    }
    public static void sendSandboxProgress(Integer percentage, Long recordId, String message) throws Exception {
        BackgroundActivityService backgroundActivityService = new BackgroundActivityService(BackgroundActivityAPI.parentActivityForRecordIdAndType(recordId, "sandbox"));
        if (backgroundActivityService != null) {
            backgroundActivityService.updateActivity(percentage, message);
        }
    }
    public static void sendSandboxProgress(Integer percentage, Long recordId, String message, long sourceOrgId) throws Exception {
        OrgSwitchBean orgSwitchBean = (OrgSwitchBean) BeanFactory.lookup("OrgSwitchBean", sourceOrgId);
        try{
            NewTransactionService.newTransactionWithReturn(() -> orgSwitchBean.sendSandboxProgress(percentage, recordId, message));
        }catch (Exception ex){
            LOGGER.info("Exception while sending Sandbox progress"+ex);
        }
    }
    public static void changeSandboxStatus(SandboxConfigContext sandboxConfigContext, long sourceOrgId) throws Exception {
        OrgSwitchBean orgSwitchBean = (OrgSwitchBean) BeanFactory.lookup("OrgSwitchBean", sourceOrgId);
        try {
            NewTransactionService.newTransactionWithReturn(() -> orgSwitchBean.changeSandboxStatus(sandboxConfigContext));
        }catch (Exception ex){
            LOGGER.info("Exception while changing Sandbox progress"+ex);
        }
    }
    public static SandboxConfigContext getSandboxById(long sandboxId, long sourceOrgId) throws Exception {
        OrgSwitchBean orgSwitchBean = (OrgSwitchBean) BeanFactory.lookup("OrgSwitchBean", sourceOrgId);
        return NewTransactionService.newTransactionWithReturn(() -> orgSwitchBean.getSandboxById(sandboxId));
    }
    public static long getSandboxIdBySourceAndTargetOrgId(Long sourceOrgId, Long targetOrgId) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(sourceOrgId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("SANDBOX_ORG_ID", "sandboxOrgId", String.valueOf(targetOrgId), NumberOperators.EQUALS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        return (long) props.get("id");
    }

    public static Long getSandboxTargetOrgId(String domainName) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("DOMAIN_NAME", "subDomain", String.valueOf(domainName), StringOperators.IS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        return (long) props.get("sandboxOrgId");
    }

    public static long getRecentPackageId(String domainName) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("DOMAIN_NAME", "subDomain", String.valueOf(domainName), StringOperators.IS));
        Map<String, Object> prop = selectBuilder.fetchFirst();
        if(prop!=null && !prop.isEmpty() && prop.containsKey("packageFileId")) {
            return (long) prop.get("packageFileId");
        }
        return -1L;
    }

    public static void setRecentPackageId(String domainName, long fileId) throws Exception {
        Map<String, Object> fileIdMap = new HashMap<>();
        fileIdMap.put("packageFileId", fileId);
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("DOMAIN_NAME", "subDomain", domainName,   StringOperators.IS));
        updateBuilder.update(fileIdMap);
    }

    public static AppDomain getProductionAppDomain(Organization sandboxOrg, long appId) throws Exception {
        long productionOrgId = sandboxOrg.getProductionOrgId();
        ApplicationContext currApplication = ApplicationApi.getApplicationForId(appId);

        if (productionOrgId > 0 && currApplication != null) {
            AccountUtil.setCurrentAccount(productionOrgId);
            ApplicationContext applicationFromProd = ApplicationApi.getApplicationForLinkName(currApplication.getLinkName());
            AppDomain appDomainObj = applicationFromProd != null ? ApplicationApi.getAppDomainForApplication(applicationFromProd.getId()) : null;

            AccountUtil.setCurrentAccount(sandboxOrg.getOrgId());
            return appDomainObj;
        }
        return null;
    }
}
