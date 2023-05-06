package com.facilio.relation.command;

import org.apache.commons.collections4.CollectionUtils;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.v3.util.V3Util;
import org.json.simple.JSONObject;
import com.facilio.modules.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRelatedModuleDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<JoinContext> joins = (List<JoinContext>) context.getOrDefault(Constants.JOINS, null);
        Object serverCriteria = context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean moduleBean = Constants.getModBean();
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        FacilioModule module = moduleBean.getModule(moduleName);
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        List<FacilioField> selectableFields = moduleBean.getAllFields(moduleName);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder
                .module(module)
                .beanClass(beanClass)
                .select(selectableFields);

        if (serverCriteria != null) {
            if (serverCriteria instanceof Criteria) {
                if (!((Criteria) serverCriteria).isEmpty()) {
                    selectRecordsBuilder.andCriteria((Criteria) serverCriteria);
                }
            } else {
                selectRecordsBuilder.andCondition((Condition) serverCriteria);
            }
        }

        if(CollectionUtils.isNotEmpty(joins)) {
            V3Util.addJoinsToSelectBuilder(selectRecordsBuilder, joins);
        }

        List<? extends ModuleBaseWithCustomFields> records = selectRecordsBuilder.get();

        Map<String, List<? extends  ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, records);

        JSONObject recordJSON = FieldUtil.getAsJSON(recordMap);
        Constants.setJsonRecordMap(context, recordJSON);

        context.put(Constants.RECORD_MAP, recordMap);

        return false;
    }
}
