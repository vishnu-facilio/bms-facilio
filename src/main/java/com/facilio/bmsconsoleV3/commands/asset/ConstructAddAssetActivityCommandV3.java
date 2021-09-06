package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConstructAddAssetActivityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long assetId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
        if (changeSet == null) {
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<UpdateChangeSet> changeSets = changeSet.get(assetId);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (moduleName == null) {
            moduleName = FacilioConstants.ContextNames.ASSET;
        }

        JSONObject info = new JSONObject();
        List<Object> changeList = new ArrayList<Object>();
        for (UpdateChangeSet changeset : changeSets) {
            long fieldid = changeset.getFieldId();
            Object oldValue = changeset.getOldValue();
            Object newValue = changeset.getNewValue();
            FacilioField field = modBean.getField(fieldid, moduleName);

            JSONObject changeObj = new JSONObject();
            changeObj.put("field", field.getName());
            changeObj.put("displayName", field.getDisplayName());
            changeObj.put("oldValue", oldValue);
            changeObj.put("newValue", newValue);
            changeList.add(changeObj);
        }
        info.put("changeSet", changeList);

        CommonCommandUtil.addActivityToContext(assetId, -1, AssetActivityType.ADD, info, (FacilioContext) context);

        return false;
    }
}


