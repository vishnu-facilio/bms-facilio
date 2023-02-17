package com.facilio.bmsconsoleV3.util;

import com.facilio.bmsconsoleV3.context.workorder.V3WorkOrderModuleSettingContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class V3WorkOrderModuleSettingAPI {

    public static final String HIDE_GALLERY = "hideGallery";

    public static void addOrUpdateSetting(V3WorkOrderModuleSettingContext workOrderModuleSettingContext) throws Exception {

        if (workOrderModuleSettingContext.getId() > 0L ){
            update(workOrderModuleSettingContext);
        }else {
            add(workOrderModuleSettingContext);
        }
    }

    private static void add(V3WorkOrderModuleSettingContext workOrderModuleSettingContext) throws Exception {
        Map<String,Object> props = new HashMap<>();
        props.put(HIDE_GALLERY, workOrderModuleSettingContext.isHideGallery());

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .fields(FieldFactory.getWorkorderModuleSettingFields(ModuleFactory.getWoSettingModule()))
                .table(ModuleFactory.getWoSettingModule().getTableName());
        builder.insert(props);
    }

    public static Map<String, Object> fetchWorkOrderModuleSettings() throws Exception {

        JSONObject workorderModuleSetting = new JSONObject();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getWorkorderModuleSettingFields(ModuleFactory.getWoSettingModule()))
                .table(ModuleFactory.getWoSettingModule().getTableName());

        return builder.fetchFirst();

    }

    private static void update(V3WorkOrderModuleSettingContext workOrderModuleSettingContext) throws SQLException {

        Map<String,Object> prop = new HashMap<>();
        prop.put(HIDE_GALLERY,workOrderModuleSettingContext.isHideGallery());

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getWorkorderModuleSettingFields(ModuleFactory.getWoSettingModule()))
                .table(ModuleFactory.getWoSettingModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(workOrderModuleSettingContext.getId(), ModuleFactory.getWoSettingModule()));
        builder.update(prop);
    }
}
