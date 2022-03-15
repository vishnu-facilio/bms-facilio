package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FieldOption;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FetchLookupLabelsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<String>> labelMeta = (Map<String, List<String>>) context.get(FacilioConstants.PickList.LOOKUP_LABEL_META);
        FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(labelMeta), "Meta cannot be null/ empty for fetching labels");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List<FieldOption<String>>> labels = new HashMap<>();
        for (Map.Entry<String, List<String>> meta : labelMeta.entrySet()) {
            String moduleName = meta.getKey();
            List<String> id = meta.getValue();
            Pair<List<Long>, List<FieldOption<String>>> splValuesAndIds = splitSpecialValues(id);
            List<FieldOption<String>> options = constructFieldOptions(modBean, moduleName, splValuesAndIds.getLeft());
            if (CollectionUtils.isNotEmpty(splValuesAndIds.getRight())) {
                options = CollectionUtils.isEmpty(options) ? new ArrayList<>() : options;
                options.addAll(splValuesAndIds.getRight());
            }
            labels.put(moduleName, options);
        }
        context.put(FacilioConstants.PickList.LOOKUP_LABELS, labels);
        return false;
    }

    private Pair<List<Long>, List<FieldOption<String>>> splitSpecialValues(List<String> ids) {
        List<Long> id = new ArrayList<>();
        List<FieldOption<String>> splFieldOptions = new ArrayList<>();
        for (String val : ids) {
            String displayName = PickListOperators.getDisplayNameForCurrentValue(val);
            if (StringUtils.isEmpty(displayName)) {
                id.add(FacilioUtil.parseLong(val));
            }
            else {
                splFieldOptions.add(new FieldOption<>(val, displayName));
            }
        }
        return Pair.of(id, splFieldOptions);
    }

    private List<FieldOption<String>> constructFieldOptions (ModuleBean modBean, String moduleName, List<Long> id) throws Exception {
        if (CollectionUtils.isEmpty(id)) {
            return null;
        }

        if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
            return fetchSplModuleLabels(moduleName, id);
        }
        else {
            return fetchModuleLabels(modBean, moduleName, id);
        }
    }

    private List<FieldOption<String>> fetchSplModuleLabels (String moduleName, List<Long> ids) throws Exception {
        Map<Long, Object> records = LookupSpecialTypeUtil.getPickList(moduleName, ids);
        if (MapUtils.isEmpty(records)) {
            return null;
        }

        List<FieldOption<String>> options = new ArrayList<>();
        for (Map.Entry<Long, Object> entry : records.entrySet()) {
            options.add(new FieldOption<>(entry.getKey().toString(), entry.getValue().toString()));
        }
        return options;
    }

    private List<FieldOption<String>> fetchModuleLabels (ModuleBean modBean, String moduleName, List<Long> id) throws Exception {
        FacilioModule module = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(module == null, MessageFormat.format("Invalid module name => {0}", moduleName));
        FacilioChain pickListChain = ReadOnlyChainFactory.newPicklistFromDataChain();
        pickListChain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        pickListChain.getContext().put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, CriteriaAPI.getIdCondition(id, module));
        pickListChain.execute();

        return (List<FieldOption<String>>) pickListChain.getContext().get(FacilioConstants.ContextNames.PICKLIST);
    }
}
