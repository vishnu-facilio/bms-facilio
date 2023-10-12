package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPortfolioSpacesCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        Map<String,Object> countMap = new HashMap<>();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
        FacilioModule baseSpaceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.BASE_SPACE);
        FacilioModule resourceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE);
        List<FacilioField> baseSpaceFields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(baseSpaceFields);
        List<FacilioField> selectableFields = new ArrayList<>();
        selectableFields.add(fieldMap.get("spaceType"));
        builder.table(baseSpaceModule.getTableName())
                .innerJoin(resourceModule.getTableName())
                .on(baseSpaceModule.getTableName()+".id = "+resourceModule.getTableName()+".id")
                .select(selectableFields)
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(baseSpaceModule))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"),searchText, StringOperators.CONTAINS))
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted",String.valueOf(Boolean.FALSE), BooleanOperators.IS))
                .groupBy(fieldMap.get("spaceType").getColumnName());
        List<Map<String,Object>> props = builder.get();
        for(Map<String,Object> prop:props){
            countMap.put(BaseSpaceContext.SpaceType.getType((int)prop.get("spaceType")).getStringVal(),prop.get("id"));
        }
        context.put(FacilioConstants.ContextNames.COUNT,countMap);
        return false;
    }
}
