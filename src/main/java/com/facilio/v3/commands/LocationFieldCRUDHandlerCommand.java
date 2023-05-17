package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationFieldCRUDHandlerCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(LocationFieldCRUDHandlerCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = recordMap.get(moduleName);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Criteria fieldCriteria = new Criteria();
        fieldCriteria.addAndCondition(CriteriaAPI.getCondition("DATA_TYPE", "dataType", String.valueOf(FieldType.LOOKUP.getTypeAsInt()), StringOperators.IS));
        fieldCriteria.addAndCondition(CriteriaAPI.getCondition("DISPLAY_TYPE", "displayType", String.valueOf(FacilioField.FieldDisplayType.GEO_LOCATION.getIntValForDB()), StringOperators.IS));
        fieldCriteria.addOrCondition(CriteriaAPI.getCondition("IS_MAIN_FIELD", "isMainField", String.valueOf(true), BooleanOperators.IS));

        List<FacilioField> fields = modBean.getAllFields(moduleName,null,null, fieldCriteria);
        if(CollectionUtils.isEmpty(fields)){
            return false;
        }
        List<FacilioField> locationFields = fields.stream().filter(field -> field.getDisplayType()== FacilioField.FieldDisplayType.GEO_LOCATION).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(locationFields)){
            return false;
        }

        List<FacilioField> mainFields = fields.stream().filter(FacilioField::isMainField).collect(Collectors.toList());
        FacilioField mainField = null;
        if(CollectionUtils.isNotEmpty(mainFields)){
            mainField = mainFields.get(0);
        }

        for (ModuleBaseWithCustomFields record: moduleBaseWithCustomFields) {

            try{
            Object mainFieldValue = null;
            if(mainField != null){
                mainFieldValue = FieldUtil.getValue(record,mainField);
            }

            for(FacilioField facilioField : locationFields){
                Object value = FieldUtil.getValue(record,facilioField);

                if(!FacilioUtil.isEmptyOrNull(value) && (value instanceof Map || value instanceof LocationContext)){
                    if(value instanceof LocationContext){
                        value = FieldUtil.getAsProperties(value);
                    }
                    Map<String, Object> fieldValue  = (Map<String, Object>) value;

                    LocationContext location = new LocationContext();
                    Double lng = (Double) fieldValue.get("lng");
                    Double lat = (Double) fieldValue.get("lat");

                    location.setLat(lat);
                    location.setLng(lng);
                    if (mainFieldValue != null) {
                        location.setName(moduleName + "_" + mainFieldValue + "_Location");
                    } else {
                        location.setName(moduleName + "_" + facilioField.getName() + "_Location");
                    }

                    Context locationContext = new FacilioContext();
                    Constants.setRecord(locationContext, location);

                     if(fieldValue.get("id")==null){

                         FacilioChain addLocation = FacilioChainFactory.addLocationChain();
                         addLocation.execute(locationContext);

                         long recordId = Constants.getRecordId(locationContext);
                         location.setId(recordId);

                         FieldUtil.setValue(record,facilioField,location);
                     }else{
                         long locationId = (long) fieldValue.get("id");
                         location.setId(locationId);
                         locationContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, java.util.Collections.singletonList(locationId));
                         FacilioChain updateLocation = FacilioChainFactory.updateLocationChain();
                         updateLocation.execute(locationContext);
                     }
                }

            }
        } catch (Exception e) {
                LOGGER.error("Exception occurred ", e);
            }
        }

        return false;
    }
}
