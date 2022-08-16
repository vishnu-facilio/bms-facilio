package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FieldOption;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetEnumValuesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);

        int page = (int) context.getOrDefault(FacilioConstants.ContextNames.PAGE, 1);
        int perPage = (int) context.getOrDefault(FacilioConstants.ContextNames.PER_PAGE, 20);
        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }

        Criteria serverCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA);
        FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getEnumFieldValuesFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(id), NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER, IDX")
                ;
        selectBuilder.offset(offset);
        selectBuilder.limit(perPage);
        if(serverCriteria != null) {
            selectBuilder.andCriteria(serverCriteria);
        }
        List<Map<String, Object>> props = selectBuilder.get();
        context.put(FacilioConstants.ContextNames.RECORD_LIST, props);

        if (CollectionUtils.isEmpty(props)) {
            return true;
        }

        List<FieldOption<Long>> options = new ArrayList<>();
        for (Map<String, Object> prop : props) {
            options.add(new FieldOption<>(
                    Long.parseLong(prop.get("index").toString()),
                    (String)prop.get("value"),
                    null,
                    null
            ));
        }
        context.put(FacilioConstants.ContextNames.PICKLIST, options);
        return false;
    }
}
