package com.facilio.sandbox.utils;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
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
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class SandboxAPI {

    public static SandboxConfigContext getSandboxById(long sandboxId) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(sandboxId, sandboxModule));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if(!MapUtils.isEmpty(props)) {
            SandboxConfigContext sandboxConfig =  FieldUtil.getAsBeanFromMap(props, SandboxConfigContext.class);
            setSandboxDomain(sandboxConfig);
            return sandboxConfig;
        }
        return null;
    }

    public static List<SandboxConfigContext> getAllSandbox(int page, int perPage, String search) throws Exception {
        FacilioModule sandboxModule = ModuleFactory.getFacilioSandboxModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFacilioSandboxFields())
                .table(sandboxModule.getTableName());

        if(!"".equals(search)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", search, StringOperators.STARTS_WITH));
        }
        if(page <= 0) {
            page = 1;
            perPage = 50;
        }
        int offset = ((page-1) * perPage);
        selectBuilder.offset(offset);
        selectBuilder.limit(perPage);

        List<Map<String, Object>> props = selectBuilder.get();
        if(!CollectionUtils.isEmpty(props)) {
            List<SandboxConfigContext> sandboxList = FieldUtil.getAsBeanListFromMapList(props, SandboxConfigContext.class);
            for(SandboxConfigContext sandboxConfig : sandboxList) {
                setSandboxDomain(sandboxConfig);
            }
            return sandboxList;
        }
        return null;
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
}
