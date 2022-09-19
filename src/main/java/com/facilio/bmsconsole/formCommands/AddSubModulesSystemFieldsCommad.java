package com.facilio.bmsconsole.formCommands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AddSubModulesSystemFieldsCommad extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule customNotesModule = new FacilioModule();
        customNotesModule.setName(module.getName() + "cmdnotes");
        customNotesModule.setDisplayName(module.getDisplayName() + "CMD Notes");
        customNotesModule.setTableName("CMD_Notes");
        customNotesModule.setType(FacilioModule.ModuleType.NOTES);
        customNotesModule.setCustom(true);
        modules.add(customNotesModule);


        FacilioModule customAttachmentModule = new FacilioModule();
        customAttachmentModule.setName(module.getName() + "cmdattachments");
        customAttachmentModule.setDisplayName(module.getDisplayName() + "CMD Attachments");
        customAttachmentModule.setTableName("CMD_Attachments");
        customAttachmentModule.setCustom(true);
        customAttachmentModule.setType(FacilioModule.ModuleType.ATTACHMENTS);

        modules.add(customAttachmentModule);

        if (modules != null && modules.size() > 0) {
            for (FacilioModule subModule: modules) {
                addModuleBasedFields(module, subModule);
            }
        }
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, null);
        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
    public static void addModuleBasedFields(FacilioModule parentModule, FacilioModule subModule) {
        List<FacilioField> fields = new ArrayList<>();
        switch (subModule.getTypeEnum()) {
            case NOTES:

                FacilioField createdTime =  FieldFactory.getField("createdTime", "Created Time", "CREATED_TIME", subModule, FieldType.NUMBER);
                createdTime.setDefault(true);
                fields.add(createdTime);

                LookupField createdBy = (LookupField) FieldFactory.getField("createdBy", "Created By", "CREATED_BY", subModule, FieldType.LOOKUP);
                createdBy.setSpecialType("users");
                createdBy.setDefault(true);
                fields.add(createdBy);

                LookupField parentId = (LookupField) FieldFactory.getField("parent", "PARENT Id", "PARENT_ID", subModule, FieldType.LOOKUP);
                parentId.setDefault(true);
                parentId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
                parentId.setLookupModule(parentModule);
                fields.add(parentId);

                FacilioField title =  FieldFactory.getField("title", "Title", "TITLE", subModule, FieldType.STRING);
                title.setDefault(true);
                fields.add(title);

                FacilioField body =  FieldFactory.getField("body", "Body", "BODY", subModule, FieldType.STRING);
                body.setDefault(true);
                fields.add(body);


                FacilioField notifyRequester =  FieldFactory.getField("notifyRequester", "Notify Requester", "NOTIFY_REQUESTER", subModule, FieldType.BOOLEAN);
                notifyRequester.setDisplayType(5);
                notifyRequester.setDefault(true);
                fields.add(notifyRequester);

                subModule.setFields(fields);

                break;
            case ATTACHMENTS:

                FacilioField attachmentCreatedTime =  FieldFactory.getField("createdTime", "Created Time", "CREATED_TIME", subModule, FieldType.NUMBER);
                fields.add(attachmentCreatedTime);

                FileField fileField = (FileField) FieldFactory.getField("file", "File ID", "FILE_ID", subModule, FieldType.FILE);
                fileField.setDefault(true);
                fields.add(fileField);

                String columnName = "PARENT_ID";
                if(subModule.getTableName().equals("CMD_Attachments")) {
                    columnName = "PARENT_TICKET";
                }
                LookupField attachmentParentId = (LookupField) FieldFactory.getField("parent", "PARENT Id", columnName, subModule, FieldType.LOOKUP);
                attachmentParentId.setDefault(true);
                attachmentParentId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
                attachmentParentId.setLookupModule(parentModule);
                fields.add(attachmentParentId);
                subModule.setFields(fields);

                break;
        }

    }
}
