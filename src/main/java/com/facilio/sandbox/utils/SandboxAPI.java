package com.facilio.sandbox.utils;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.util.RequestUtil;
import com.facilio.wmsv2.endpoint.Broadcaster;
import com.facilio.wmsv2.message.WebMessage;
import com.opensymphony.xwork2.ActionContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class SandboxAPI {

    public static SandboxConfigContext getSandboxById(long sandboxId) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(sandboxId, sandboxModule));
        Map<String, Object> props = selectBuilder.fetchFirst();

        FacilioModule sandboxSharingModule = ModuleFactory.getSandboxSharingModule();
        List<FacilioField> sandboxSharingFields = FieldFactory.getSandboxSharingFields();
        SharingContext<SingleSharingContext> sharingMap = SharingAPI.getSharing(sandboxId, sandboxSharingModule,
                SingleSharingContext.class, sandboxSharingFields);
        if(!MapUtils.isEmpty(props)) {
            SandboxConfigContext sandboxConfig = FieldUtil.getAsBeanFromMap(props, SandboxConfigContext.class);
            setSandboxDomain(sandboxConfig);
            sandboxConfig.setSandboxSharing(sharingMap);
            return sandboxConfig;
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
        Map<Long, SharingContext<SingleSharingContext>> sharingMap = SharingAPI.getSharing(sandboxIds, sandboxSharingModule,
                SingleSharingContext.class, sandboxSharingFields);
        if (!CollectionUtils.isEmpty(props)) {
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

    public static void setSandboxDomain(SandboxConfigContext sandboxConfig) {
        String subDomain = sandboxConfig.getSubDomain();
        StringBuilder builder = new StringBuilder();
        HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
        builder.append(RequestUtil.getProtocol(request))
                .append("://")
                .append(subDomain + "." + FacilioProperties.getBaseDomain());
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

    public static void sendSandboxProgress(SandboxConfigContext sandboxConfigContext) throws Exception {
        JSONObject json = FieldUtil.getAsJSON(sandboxConfigContext);
        WebMessage msg = new WebMessage();
        msg.setTopic("__migration__/" + sandboxConfigContext.getOrgId() + "/" + sandboxConfigContext.getSandboxOrgId() + "/sandbox");
        msg.setOrgId(sandboxConfigContext.getOrgId());
        msg.setContent(json);
        Broadcaster.getBroadcaster().sendMessage(msg);
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
}
