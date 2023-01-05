package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchSpaceBookingWithIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> spaceIds = (List<Long>) context.get(FacilioConstants.ContextNames.ID);
        String moduleName = FacilioConstants.ContextNames.SPACE_BOOKING;
        FacilioContext summary=V3Util.getSummary(moduleName, spaceIds);
        List<ModuleBaseWithCustomFields> moduleRecord = Constants.getRecordMap(summary).get(moduleName);

        List<Long>recordIds = Constants.getRecordIds(summary);
        Long recordId = recordIds.get(0);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioStatus status = TicketAPI.getStatus(modBean.getModule(moduleName),"cancelled");
        FacilioField field = modBean.getField("moduleState",moduleName);
        FacilioField modifiedField = modBean.getField("sysModifiedTime",moduleName);
        List<UpdateChangeSet> changeSet = new ArrayList<>();
        UpdateChangeSet updateChangeSet = new UpdateChangeSet();
        updateChangeSet.setFieldId(field.getFieldId());
        updateChangeSet.setRecordId(recordId);
        updateChangeSet.setOldValue(moduleRecord.get(0).getModuleState().getId());
        updateChangeSet.setNewValue(status.getId());
        changeSet.add(updateChangeSet);

        UpdateChangeSet updateChangeSett = new UpdateChangeSet();
        updateChangeSett.setFieldId(modifiedField.getFieldId());
        updateChangeSett.setRecordId(recordId);
        updateChangeSett.setOldValue(moduleRecord.get(0).getSysModifiedTime());
        updateChangeSett.setNewValue( System.currentTimeMillis());
        changeSet.add(updateChangeSett);

        Map<Long,List> map = new HashMap<>();
        map.put(recordId,changeSet);
        Map<String,Map>changeSetMap = new HashMap<>();
        changeSetMap.put(moduleName,map);

        context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP,changeSetMap);

        List<V3SpaceBookingContext> bookingList = Constants.getRecordListFromContext(summary,moduleName);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        moduleRecord.get(0).setModuleState(status);
        recordMap.put(moduleName,moduleRecord);
        context.put("spacebookingList",bookingList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.FIELD_CHANGE);
        return false;
    }
}
