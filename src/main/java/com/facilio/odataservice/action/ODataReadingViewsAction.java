package com.facilio.odataservice.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.odataservice.util.ODataReadingViewsUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.odataservice.util.ODataReadingViewsUtil.addODataReadingViewName;

@Getter @Setter
public class ODataReadingViewsAction extends FacilioAction {
    private ODataReadingsContext readingView;
    private String readingName;
    private long assetCategoryId;
    private Boolean status;
    private static final Logger LOGGER = LogManager.getLogger(ODataModuleAction.class.getName());
    public String addODataReadings() throws Exception {
        ODataReadingsContext context = addODataReadingViewName(getReadingView());
        ODataReadingViewsUtil.updateODataReadingsCriteriaId(context);
        List<FacilioField> fields = FieldFactory.getODataReadingFields();
        Map<String, Object> prop = FieldUtil.getAsProperties(context);
        prop.put("isEnabled",true);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getODataReadingModule().getTableName())
                .ignoreSplNullHandling()
                .fields(fields);
        insertBuilder.addRecord(prop);
        insertBuilder.save();
        return SUCCESS;
    }
    public String fetchODataReadings() throws Exception{
        List<FacilioField> fields = FieldFactory.getODataReadingFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getODataReadingModule().getTableName())
                .select(fields);
        if(getSearch()!=null && !getSearch().isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("displayName"),getSearch(),StringOperators.CONTAINS));
        }
        selectRecordBuilder.limit(getPerPage())
                            .offset(getPerPage() * (getPage()-1));

        List<Map<String, Object>> result = selectRecordBuilder.get();
        List<ODataReadingsContext> readingsContextList = FieldUtil.getAsBeanListFromMapList(result, ODataReadingsContext.class);
        for(int i=0;i<readingsContextList.size();i++){
            ODataReadingsContext context = readingsContextList.get(i);
            context.setCriteria(CriteriaAPI.getCriteria(context.getCriteriaId()));
            context.setEnabled((Boolean)result.get(i).get("isEnabled"));
        }
        setResult("readings",readingsContextList);
        return SUCCESS;
    }
    public String readingsCount() throws Exception{
        List<FacilioField> fields = FieldFactory.getODataReadingFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getODataReadingModule().getTableName())
                .select(fields);
        if(getSearch()!=null && !getSearch().isEmpty()){
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("displayName"),getSearch(),StringOperators.CONTAINS));
        }
        setResult("count",selectRecordBuilder.get().size());
        return SUCCESS;
    }
    public String updateODataReadings() throws Exception {
        List<FacilioField> fields = FieldFactory.getODataReadingFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        ODataReadingsContext context = getReadingView();
        context.setName(ODataReadingViewsUtil.getReadingViewName(context));
        ODataReadingViewsUtil.updateODataReadingsCriteriaId(context);
        Map<String, Object> prop = FieldUtil.getAsProperties(context);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getODataReadingModule().getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),getReadingName(), StringOperators.IS));
        updateBuilder.update(prop);
        return SUCCESS;
    }
    public String changeStatus() throws Exception{
        List<FacilioField> fields = FieldFactory.getODataReadingFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        ODataReadingsContext context = ODataReadingViewsUtil.getReadingView(getReadingName());
        context.setEnabled(getStatus());
        Map<String, Object> prop = new HashMap<>();
        prop.put("isEnabled",getStatus());
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getODataReadingModule().getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),getReadingName(), StringOperators.IS));
        updateBuilder.update(prop);

        return SUCCESS;
    }

    public String deleteODataReadings() throws Exception {
        List<FacilioField> fields = FieldFactory.getODataReadingFields();
        Map<String,FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getODataReadingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsAsMap.get("name"),getReadingName(), StringOperators.IS));
        int deleted = deleteRecordBuilder.delete();
        return SUCCESS;
    }

}
