package com.facilio.odataservice.util;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ODataReadingViewsUtil {
    static ModuleBean modbean;

    static String tableName = ModuleFactory.getODataReadingModule().getTableName();

    private static final Logger LOGGER = LogManager.getLogger(ODataReadingViewsUtil.class.getName());
    public static ModuleBean getModbean() throws Exception{
        modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        return modbean;
    }

    public static List<FacilioField> fields = FieldFactory.getODataReadingFields();


    public static Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);;
    
    public static ODataReadingsContext getReadingView(String name) throws Exception {
        ODataReadingsContext readingsContext;
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                                                                .table(tableName)
                                                                .select(fields)
                                                                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),name, StringOperators.IS));
        if(selectRecordBuilder.get().size()==0){
            return null;
        }else {
            Map<String, Object> result = selectRecordBuilder.get().get(0);
            readingsContext = FieldUtil.getAsBeanFromMap(result, ODataReadingsContext.class);
            readingsContext.setCriteria(CriteriaAPI.getCriteria(readingsContext.getCriteriaId()));
            return readingsContext;
        }
    }
    public static List<String> getReadingViewsList() throws Exception {
        LOGGER.info(tableName + "fields: "+fields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(tableName)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("isEnabled"), String.valueOf(true),BooleanOperators.IS));
        if(selectRecordBuilder.get().size()==0){
            return null;
        }else {
            List<Map<String, Object>> result = selectRecordBuilder.get();
            List<String> names = new ArrayList<>();
            result.stream().forEach(map -> {
                names.add(map.get("name").toString());
            });
            LOGGER.info("names->"+names);
            return names;
        }
    }

    public static ODataReadingsContext addODataReadingViewName(ODataReadingsContext reading) throws Exception {

        String name = reading.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        int i=1;
        String temp = name;
        while(true) {

            if(ODataReadingViewsUtil.getReadingView(temp) == null) {
                reading.setName(temp);
                break;
            }
            else {
                temp = name + i++;
            }
        }
        return reading;
    }
    public static String getReadingViewName(ODataReadingsContext reading) throws Exception {

        String name = reading.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        int i=1;
        String temp = name;
        while(true) {

            if(ODataReadingViewsUtil.getReadingView(temp) == null) {
                reading.setName(temp);
                break;
            }
            else {
                temp = name + i++;
            }
        }
        return temp;
    }
    public static void updateODataReadingsCriteriaId(ODataReadingsContext oDataReadingsContext) throws Exception {
        if (oDataReadingsContext.getCriteria() != null) {
            Criteria criteria = oDataReadingsContext.getCriteria();
            if (oDataReadingsContext.getReadingType() != -1) {
                for (String key : criteria.getConditions().keySet()) {
                    Condition condition = criteria.getConditions().get(key);
                    String moduleName = "asset";
                    if (oDataReadingsContext.getReadingType() == 1) {
                        moduleName = "asset";
                    } else if (oDataReadingsContext.getReadingType() == 2) {
                        moduleName = "space";
                    }
                    FacilioField field = getModbean().getField(condition.getFieldName(), moduleName);
                    condition.setField(field);
                }
            }
            if(oDataReadingsContext.getCriteriaId() != -1) {
                CriteriaAPI.deleteCriteria(oDataReadingsContext.getCriteriaId());
            }
            long criteriaId = CriteriaAPI.addCriteria(oDataReadingsContext.getCriteria(), AccountUtil.getCurrentOrg().getId());
            oDataReadingsContext.setCriteriaId(criteriaId);
        }
    }
    }