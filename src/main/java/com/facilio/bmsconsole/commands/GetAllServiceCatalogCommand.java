package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetAllServiceCatalogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long groupId = (long) context.get(FacilioConstants.ContextNames.GROUP_ID);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getServiceCatalogModule().getTableName())
                .select(FieldFactory.getServiceCatalogFields());

        if (groupId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", String.valueOf(groupId), NumberOperators.EQUALS));
        }

        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }

        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        if (StringUtils.isNotEmpty(searchString)) {
            builder.andCondition(CriteriaAPI.getCondition("NAME", "name", searchString, StringOperators.CONTAINS));
        }

        List<Map<String, Object>> maps = builder.get();
        List<ServiceCatalogContext> serviceCatalogs = FieldUtil.getAsBeanListFromMapList(maps, ServiceCatalogContext.class);
        if (CollectionUtils.isNotEmpty(serviceCatalogs)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            List<Long> ids = new ArrayList<>();
            for (ServiceCatalogContext serviceCatalog : serviceCatalogs) {
                ids.add(serviceCatalog.getFormId());
            }
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getFormModule().getTableName())
                    .select(FieldFactory.getFormFields())
                    .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getFormModule()));
            List<FacilioForm> forms = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), FacilioForm.class);
            Map<Long, FacilioForm> formMap = forms.stream().collect(Collectors.toMap(FacilioForm::getId, Function.identity()));

            for (ServiceCatalogContext serviceCatalog : serviceCatalogs) {
                FacilioModule module = modBean.getModule(serviceCatalog.getModuleId());
                serviceCatalog.setModule(module);
                serviceCatalog.setForm(formMap.get(serviceCatalog.getFormId()));
            }
        }

        context.put(FacilioConstants.ContextNames.SERVICE_CATALOGS, serviceCatalogs);

        return false;
    }
}
