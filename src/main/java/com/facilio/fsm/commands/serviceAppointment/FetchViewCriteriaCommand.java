package com.facilio.fsm.commands.serviceAppointment;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FetchViewCriteriaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String viewName = (String) context.get(FacilioConstants.ContextNames.VIEW_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);

        if(viewName != null && !viewName.isEmpty()) {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule module = modBean.getModule(moduleName);
            long moduleId = module.getModuleId();
            FacilioModule view = ModuleFactory.getViewsModule();
            FacilioField criteriaId = FieldFactory.getField("criteriaId", "CRITERIAID", view, FieldType.NUMBER);
            List<FacilioField> allFields = FieldFactory.getViewFields();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(allFields);
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(Collections.singletonList(criteriaId))
                    .table("Views")
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), viewName, StringOperators.IS));

            if (moduleId > 0) {
                builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
            }
            else if (moduleName != null) {
                builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleName"), moduleName, StringOperators.IS));
            }

            ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
            if (app == null) {
                app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            }
            Criteria appCriteria = new Criteria();
            appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
            if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
                appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), CommonOperators.IS_EMPTY));
            }
            builder.andCriteria(appCriteria);

            List<Map<String, Object>> props = builder.get();
                long CriteriaId = 0;
                if(CollectionUtils.isNotEmpty(props)){
                    CriteriaId = (long) props.get(0).get("criteriaId");
                }
                context.put(FacilioConstants.ContextNames.CRITERIA,CriteriaId);

        }
        return false;
    }
}
