package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
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

        FacilioModule customTimeLogModule = new FacilioModule();
        customTimeLogModule.setName("timelog__"+module.getName());
        customTimeLogModule.setDisplayName("Timelog " + module.getDisplayName());
        customTimeLogModule.setTableName("CustomTimeLog");
        customTimeLogModule.setType(FacilioModule.ModuleType.TIME_LOG);
        customTimeLogModule.setCustom(true);
        modules.add(customTimeLogModule);

        FacilioModule classificationDataModule = ClassificationUtil.getNewClassificationDataModule(module,"Custom_Module_Classification_Data");
        classificationDataModule.setCustom(true);

        modules.add(classificationDataModule);


        if (modules != null && modules.size() > 0) {
            for (FacilioModule subModule: modules) {
                addModuleBasedFields(module, subModule);
            }
        }
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, null);
        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
    public static void addModuleBasedFields(FacilioModule parentModule, FacilioModule subModule) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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

                LookupField parentNote = (LookupField) FieldFactory.getField("parentNote", "Parent Note", "PARENT_NOTE", subModule, FieldType.LOOKUP);
                parentNote.setLookupModule(subModule);
                parentNote.setDefault(true);
                fields.add(parentNote);

                FacilioField bodyHTML =  FieldFactory.getField("bodyHTML", "Body HTML", "BODY_HTML", subModule, FieldType.STRING);
                bodyHTML.setDefault(true);
                fields.add(bodyHTML);

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

            case TIME_LOG:

                FacilioField name = FieldFactory.getField("name","Name","NAME",subModule,FieldType.STRING);
                name.setDefault(true);
                fields.add(name);

                LookupField parent = (LookupField) FieldFactory.getField("parent", "PARENT", "PARENT_ID", subModule, FieldType.LOOKUP);
                parent.setDefault(true);
                parent.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
                parent.setLookupModule(parentModule);
                fields.add(parent);

                LookupField fromStatusId = (LookupField) FieldFactory.getField("fromStatus","From Status","FROM_STATUS_ID",subModule,FieldType.LOOKUP);
                fromStatusId.setDefault(true);
                fromStatusId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
                fromStatusId.setLookupModule(modBean.getModule("ticketstatus"));
                fields.add(fromStatusId);

                LookupField toStatusId = (LookupField) FieldFactory.getField("toStatus","To Status","TO_STATUS_ID",subModule,FieldType.LOOKUP);
                toStatusId.setDefault(true);
                toStatusId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
                toStatusId.setLookupModule(modBean.getModule("ticketstatus"));
                fields.add(toStatusId);

                FacilioField startTime = FieldFactory.getField("startTime","Start Time","START_TIME", subModule,FieldType.DATE_TIME);
                startTime.setDefault(true);
                fields.add(startTime);

                FacilioField endTime = FieldFactory.getField("endTime","End Time","END_TIME",subModule,FieldType.DATE_TIME);
                endTime.setDefault(true);
                fields.add(endTime);

                FacilioField duration = FieldFactory.getField("duration","Duration","DURATION",subModule,FieldType.NUMBER);
                duration.setDefault(true);
                fields.add(duration);

                FacilioField isTimerEnabaled = FieldFactory.getField("isTimerEnabled","Is Timer Enabled","IS_TIMER_ENABLED",subModule,FieldType.BOOLEAN);
                isTimerEnabaled.setDefault(true);
                fields.add(isTimerEnabaled);

                LookupField doneBy = (LookupField) FieldFactory.getField("doneBy", "Done By", "DONE_BY", subModule, FieldType.LOOKUP);
                doneBy.setSpecialType("users");
                doneBy.setDefault(true);
                fields.add(doneBy);

                subModule.setFields(fields);

        }

    }
}
