package com.facilio.elasticsearch.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.elasticsearch.util.ESUtil;
import com.facilio.elasticsearch.util.SyncUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConstructESSearchCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        if (StringUtils.isNotEmpty(search)) {
            List<FacilioModule> modulesToSearch = SyncUtil.getModulesToSearch();
            if (CollectionUtils.isEmpty(modulesToSearch)) {
                throw new IllegalAccessException("No modules are synced with global search");
            }

            Map<Long, FacilioModule> searchableModule = modulesToSearch.stream().collect(Collectors.toMap(FacilioModule::getModuleId, Function.identity()));

            List<Map<String, Object>> searchResult = new ArrayList<>();

            Map<Long, List<Long>> result = ESUtil.query(search);
            if (MapUtils.isNotEmpty(result)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                for (Long moduleId : result.keySet()) {
                    FacilioModule module = searchableModule.get(moduleId);
                    if (module == null) {
                        continue;
                    }

                    SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                            .module(module)
                            .beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
                            .select(modBean.getAllFields(module.getName()))
                            .andCondition(CriteriaAPI.getIdCondition(result.get(moduleId), module));
                    List<ModuleBaseWithCustomFields> recordList = builder.get();

                    Map<String, Object> searchObject = new HashMap<>();
                    searchObject.put("module", module);
                    searchObject.put("list", recordList);
                    searchResult.add(searchObject);
                }
            }

            context.put(FacilioConstants.ContextNames.SEARCH_RESULT, searchResult);
        }
        return false;
    }
}
