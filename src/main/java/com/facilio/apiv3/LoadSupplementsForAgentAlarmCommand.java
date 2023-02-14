package com.facilio.apiv3;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoadSupplementsForAgentAlarmCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.AGENT_ALARM);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("agent"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("severity"));
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        fetchAdditionalFields(context);
        return false;
    }

    private void fetchAdditionalFields(Context context) throws Exception{
        boolean fetchOnlyViewGroupColumn=(boolean)context.getOrDefault(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,false);
        FacilioView view=(FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        if(fetchOnlyViewGroupColumn && view!=null) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> allFields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);
            List<FacilioField> extraAgentAlarmFields = new ArrayList<>();
            List<FacilioField> viewFields = view.getFields().stream().map(ViewField::getField).filter(Objects::nonNull).collect(Collectors.toList());
            Map<String, FacilioField> viewFieldsMap = FieldFactory.getAsMap(viewFields);
            if(!viewFieldsMap.containsKey("agentAlarmType")){
                extraAgentAlarmFields.add(allFieldsAsMap.get("agentAlarmType"));
            }
            if(!viewFieldsMap.containsKey("lastCreatedTime")){
                extraAgentAlarmFields.add(allFieldsAsMap.get("lastCreatedTime"));
            }
            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, extraAgentAlarmFields);
        }
    }
}
