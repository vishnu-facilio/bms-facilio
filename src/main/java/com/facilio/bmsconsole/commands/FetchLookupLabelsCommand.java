package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.PickListAction;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FieldOption;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FetchLookupLabelsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Long>> labelMeta = (Map<String, List<Long>>) context.get(FacilioConstants.PickList.LOOKUP_LABEL_META);
        FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(labelMeta), "Meta cannot be null/ empty for fetching labels");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List<FieldOption<Long>>> labels = new HashMap<>();
        for (Map.Entry<String, List<Long>> meta : labelMeta.entrySet()) {
            String moduleName = meta.getKey();
            List<Long> id = meta.getValue();
            FacilioModule module = modBean.getModule(moduleName);
            FacilioUtil.throwIllegalArgumentException(module == null, MessageFormat.format("Invalid module name => {0}", moduleName));
            List<FieldOption<Long>> options = constructFieldOptions(modBean, module, id);
            labels.put(module.getName(), options);
        }
        context.put(FacilioConstants.PickList.LOOKUP_LABELS, labels);
        return false;
    }

    private List<FieldOption<Long>> constructFieldOptions (ModuleBean modBean, FacilioModule module, List<Long> id) throws Exception {
        if (CollectionUtils.isEmpty(id)) {
            return null;
        }

        if (LookupSpecialTypeUtil.isSpecialType(module.getName())) {
            return fetchSplModuleLabels(module.getName(), id);
        }
        else {
            return fetchModuleLabels(modBean, module, id);
        }
    }

    private List<FieldOption<Long>> fetchSplModuleLabels (String moduleName, List<Long> ids) throws Exception {
        Map<Long, Object> records = LookupSpecialTypeUtil.getPickList(moduleName, ids);
        if (MapUtils.isEmpty(records)) {
            return null;
        }

        List<FieldOption<Long>> options = new ArrayList<>();
        for (Map.Entry<Long, Object> entry : records.entrySet()) {
            options.add(new FieldOption<>(entry.getKey(), entry.getValue().toString()));
        }
        return options;
    }

    private List<FieldOption<Long>> fetchModuleLabels (ModuleBean modBean, FacilioModule module, List<Long> id) throws Exception {
        FacilioField primaryField = modBean.getPrimaryField(module.getName());
        FacilioUtil.throwIllegalArgumentException(primaryField == null, MessageFormat.format("The module ({0}) is corrupt as it doesn't have primary field", module.getName()));

        List<FacilioField> fields = null;
        boolean isResource = module.getName().equals(FacilioConstants.ContextNames.RESOURCE);
        if (isResource) {
            fields = new ArrayList<>(2);
            fields.add(primaryField);
            fields.add(modBean.getField("resourceType", module.getName()));
        }
        else {
            fields = Collections.singletonList(primaryField);
        }

        List<Map<String, Object>> records = new SelectRecordsBuilder()
                .module(module)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(id, module))
                .getAsProps()
                ;

        if (CollectionUtils.isEmpty(records)) {
            return null;
        }

        List<FieldOption<Long>> options = RecordAPI.constructFieldOptionsFromRecords(records, primaryField, null, isResource);
        return options;
    }
}
