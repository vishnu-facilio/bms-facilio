package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FieldOption;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetValueForRecordIds extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName= (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        String recordIdsStr= (String) context.getOrDefault(FacilioConstants.ContextNames.ID,null);
        FacilioUtil.throwIllegalArgumentException(moduleName==null || recordIdsStr==null,"Module Name or Record Id is null");
        List<Map<String, Object>> recordMap=new LinkedList<>();
        List<Long> recordIds = Stream.of(recordIdsStr.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        if(LookupSpecialTypeUtil.isSpecialType(moduleName)){
            List<Map<String, Object>> records=getSpecialModuleRecords(moduleName,recordIds);
            if(CollectionUtils.isNotEmpty(records)){
                recordMap=records;
            }
        }
        else {

            boolean isResource = moduleName.equals(FacilioConstants.ContextNames.RESOURCE);
            List recordList=RecordAPI.getRecords(moduleName,recordIds);
            if(recordList!=null) {
                List<Map<String,Object>> recorMap=FieldUtil.getAsMapList(recordList,FacilioConstants.ContextNames.getClassFromModuleName(moduleName));
                recordMap = FieldUtil.getAsMapList(RecordAPI.constructFieldOptionsFromRecords(recorMap, Constants.getModBean().getPrimaryField(moduleName), null, null, isResource), FieldOption.class);
            }
        }
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }

    public static List<Map<String, Object>> getSpecialModuleRecords(String moduleName,List<Long> recordIds) throws Exception{
        List specialModuleRecordList = null;
        if(FacilioConstants.ContextNames.USERS.equals(moduleName)) {
            List<User> records = AccountUtil.getUserBean().getUsers(recordIds, false);
            specialModuleRecordList = records.stream()
                    .map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
                    .collect(Collectors.toList());
        }
        else if(FacilioConstants.ContextNames.REQUESTER.equals(moduleName)){
            long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
            if(appId > 0) {
                List<User> users = ApplicationApi.getRequesterList(AccountUtil.getCurrentOrg().getOrgId(), appId, null);
                specialModuleRecordList=users.stream().filter(i->recordIds.contains(i.getOuid())).collect(Collectors.toList());
            }
        }
        else if(FacilioConstants.ContextNames.GROUPS.equals(moduleName)){
            List<Group> records=AccountUtil.getGroupBean().getGroups(recordIds);
            specialModuleRecordList= records.stream()
                    .map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
                    .collect(Collectors.toList());
        }else if (FacilioConstants.ContextNames.ROLE.equals(moduleName)){
            List<Role>   records=AccountUtil.getRoleBean().getRoles(recordIds);
            specialModuleRecordList= records.stream()
                    .map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
                    .collect(Collectors.toList());
        }
      if(CollectionUtils.isNotEmpty(specialModuleRecordList)){
         return FieldUtil.getAsMapList(specialModuleRecordList,FieldOption.class);
      }
      return null;
    }
}
