package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.util.AppModulePermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetSupplementsSupplyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = "asset";
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (supplementFields == null) {
            supplementFields = new ArrayList<>();
        }

        SupplementRecord identifiedLocationField = (SupplementRecord) fieldsAsMap.get("identifiedLocation");
        SupplementRecord spaceField = (SupplementRecord) fieldsAsMap.get("space");
        SupplementRecord categoryField = (SupplementRecord) fieldsAsMap.get("category");
        SupplementRecord rotatingItemField = (SupplementRecord) fieldsAsMap.get("rotatingItem");
        SupplementRecord rotatingToolField = (SupplementRecord) fieldsAsMap.get("rotatingTool");
        SupplementRecord departmentField = (SupplementRecord) fieldsAsMap.get("department");
        SupplementRecord typeField = (SupplementRecord) fieldsAsMap.get("type");
        SupplementRecord failureClassField = (SupplementRecord) fieldsAsMap.get("failureClass");
        SupplementRecord decommissionedByField = (SupplementRecord) fieldsAsMap.get("decommissionedBy");
        SupplementRecord rotatingItemTypeField = (SupplementRecord) fieldsAsMap.get("rotatingItemType");
        SupplementRecord storeRoomField = (SupplementRecord) fieldsAsMap.get("storeRoom");

        supplementFields.add(identifiedLocationField);
        supplementFields.add(spaceField);
        supplementFields.add(categoryField);
        supplementFields.add(rotatingItemField);
        supplementFields.add(rotatingToolField);
        supplementFields.add(departmentField);
        supplementFields.add(typeField);
        supplementFields.add(failureClassField);
        supplementFields.add(decommissionedByField);
        supplementFields.add(rotatingItemTypeField);
        supplementFields.add(storeRoomField);

        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        supplementFields.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        supplementFields.add(sysModifiedBy);


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);


        return false;
    }
}
