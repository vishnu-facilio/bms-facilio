package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetBoundedSpacesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        double maxLat = (double) context.get("maxLat");
        double minLat = (double) context.get("minLat");
        double maxLng = (double) context.get("maxLng");
        double minLng = (double) context.get("minLng");
        String defaultIds = (String) context.get("defaultIds");
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        FacilioModule spaceMoodule = Constants.getModBean().getModule(moduleName);
        List<FacilioField> spaceFields = Constants.getModBean().getAllFields(spaceMoodule.getName());
        spaceFields.add(FieldFactory.getIdField(spaceMoodule));
        List<FacilioField> locationFields = Constants.getModBean().getAllFields("location");
        Map<String,FacilioField> spaceFieldMap = FieldFactory.getAsMap(spaceFields);
        Map<String,FacilioField> locationFieldMap = FieldFactory.getAsMap(locationFields);
        FacilioModule locationModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.LOCATION);

        List<FacilioField> selectFields = new ArrayList<>();
        selectFields.add(FieldFactory.getIdField(spaceMoodule));
        selectFields.add(locationFieldMap.get("lat"));
        selectFields.add(locationFieldMap.get("lng"));
        selectFields.add(spaceFieldMap.get("name"));
        selectFields.add(spaceFieldMap.get("photoId"));
        selectFields.add(locationFieldMap.get("street"));
        selectFields.add(locationFieldMap.get("city"));
        selectFields.add(locationFieldMap.get("state"));
        selectFields.add(locationFieldMap.get("zip"));
        selectFields.add(locationFieldMap.get("country"));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(selectFields)
                .table(locationModule.getTableName())
                .innerJoin(spaceMoodule.getTableName())
                .on(spaceFieldMap.get("location").getCompleteColumnName()+"= "+locationModule.getTableName()+".id")
                .innerJoin("Resources")
                .on(spaceFieldMap.get("id").getCompleteColumnName()+"= Resources.id");


        Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
        if (filterCriteria != null) {
            builder.andCriteria(filterCriteria);
        }else{
            if (maxLat > 0) {
                builder.andCondition(CriteriaAPI.getCondition(locationFieldMap.get("lat"), String.valueOf(maxLat), NumberOperators.LESS_THAN));
            }
            if (minLat > 0) {
                builder.
                        andCondition(CriteriaAPI.getCondition(locationFieldMap.get("lat"), String.valueOf(minLat), NumberOperators.GREATER_THAN));
            }
            if (maxLng > 0) {
                builder.
                        andCondition(CriteriaAPI.getCondition(locationFieldMap.get("lng"), String.valueOf(maxLng), NumberOperators.LESS_THAN));
            }
            if (minLng > 0) {
                builder.
                        andCondition(CriteriaAPI.getCondition(locationFieldMap.get("lng"), String.valueOf(minLng), NumberOperators.GREATER_THAN));
            }
        }
        if(!defaultIds.isEmpty()){
            builder.orCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE)),defaultIds, StringOperators.IS));
        }

        List<Map<String,Object>> props = builder.get();
        context.put("spaces",props);
        return false;
    }
}
