package com.facilio.componentpackage.utils;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.opencsv.CSVReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PackageNotesUtil {

    public static List<NoteContext> getAllNotes(FacilioModule module) throws Exception {

        if (AccountUtil.getCurrentAccount().getApp() == null) {
            AccountUtil.getCurrentAccount().setApp(ApplicationApi.getApplicationForLinkName(SignupUtil.getSignupApplicationLinkName()));
        }

        FacilioChain getNoteChain = FacilioChainFactory.getNotesChain();
        FacilioContext context = getNoteChain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        context.put("onlyFetchParentNotes", false);
        context.put(FacilioConstants.ContextNames.PARENT_ID, -1l);
        context.put(FacilioConstants.ContextNames.PARENT_NOTE_ID, -1l);

        getNoteChain.execute();

        return (List<NoteContext>) context.get(FacilioConstants.ContextNames.NOTE_LIST);
    }


    public static List<NoteContext> getNotesFromFile(File notesFile, FacilioModule notesModule, Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets, FacilioModule parentModule, DataMigrationBean targetConnection, DataMigrationStatusContext dataMigrationObj, PackageFolderContext dataFilesFolder) throws Exception {

        List<NoteContext> allNotes = new ArrayList<>();
        List<NoteContext> notes = new ArrayList<>();
        if (notesFile == null || notesModule == null) {
            return allNotes;
        }

        Map<Long, List<Long>> moduleIdVsOldIds = new HashMap<>();
        Map<Long, Map<Long, Long>> moduleIdVsOldIdVsNewId = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(notesFile.getAbsolutePath()))) {
            List<String[]> csvData = csvReader.readAll();
            String[] fieldNames = csvData.get(0);
            for (int i = 1; i < csvData.size(); i++) {
                String[] fieldValues = csvData.get(i);
                NoteContext noteContext = getCsvNote(fieldNames, fieldValues, notesModule, packageChangSets, parentModule, moduleIdVsOldIds, dataFilesFolder);
                allNotes.add(noteContext);
            }
        }

        for (Map.Entry<Long, List<Long>> entry : moduleIdVsOldIds.entrySet()) {
            Long moduleId = entry.getKey();
            List<Long> oldIds = entry.getValue();
            Map<Long, Long> idMappings = targetConnection.getOldVsNewId(dataMigrationObj.getId(), moduleId, oldIds);
            moduleIdVsOldIdVsNewId.put(moduleId, idMappings);
        }

        Map<Long, Long> idMapping = moduleIdVsOldIdVsNewId.get(parentModule.getModuleId());

        for (NoteContext note : allNotes) {
            long oldParentId = note.getParentId();
            long newParentId = idMapping.getOrDefault(oldParentId,-1l);
            if(newParentId<1){
                continue;
            }
            note.setParentId(newParentId);
            List<CommentMentionContext> commentMentions = note.getMentions();
            for (CommentMentionContext commentMention : commentMentions) {
                long oldMentionParentId = commentMention.getMentionedRecordId();
                if (oldMentionParentId > 0) {
                    long newMentionParentId = idMapping.getOrDefault(oldMentionParentId, -1l);
                    commentMention.setMentionedRecordId(newMentionParentId);
                }
            }
            notes.add(note);
        }

        return notes;
    }

    public static List<FacilioField> getAllNotesCsvFields(FacilioModule noteModule) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> notesFields = moduleBean.getAllFields(noteModule.getName());
        FacilioField idField = FieldFactory.getIdField(noteModule);
        notesFields.add(0, idField);
        FacilioModule commentMentionsModule = ModuleFactory.getCommentMentionsModule();
        List<FacilioField> commentMentionFields = FieldFactory.getCommentMentionsFields(commentMentionsModule);
        FacilioModule commentsSharingModule = ModuleFactory.getCommentsSharingModule();
        List<FacilioField> commentsSharingFields = FieldFactory.getCommentsSharingFields(commentsSharingModule);
        List<FacilioField> commentAttachmentFields = moduleBean.getAllFields(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS);
        Map<String, FacilioField> commentAttachmentFieldsMap = FieldFactory.getAsMap(commentAttachmentFields);
        Map<String, FacilioField> commentsSharingFieldsMap = FieldFactory.getAsMap(commentsSharingFields);
        Map<String, FacilioField> commentMentionFieldsMap = FieldFactory.getAsMap(commentMentionFields);
        notesFields.add(commentAttachmentFieldsMap.get("type"));
        FacilioModule attachmentModule = commentAttachmentFieldsMap.get("fileId").getModule();
        notesFields.add(FieldFactory.getStringField("attachmentContentType",null,attachmentModule));
        notesFields.add(FieldFactory.getStringField("attachmentFileName",null,attachmentModule));
        notesFields.add(FieldFactory.getStringField("attachmentUniqueIdentifierForFile",null,attachmentModule));
        notesFields.add(commentsSharingFieldsMap.get("appId"));
        notesFields.add(commentMentionFieldsMap.get("mentionedRecordId"));
        notesFields.add(commentMentionFieldsMap.get("mentionedModuleId"));
        notesFields.add(commentMentionFieldsMap.get("parentModuleId"));
        notesFields.add(commentMentionFieldsMap.get("mentionType"));

        return notesFields;
    }

    private static NoteContext getCsvNote(String[] fieldNames, String[] fieldValues, FacilioModule noteModule, Map<ComponentType, List<PackageChangeSetMappingContext>> packageChangSets,FacilioModule parentModule, Map<Long, List<Long>> moduleIdVsOldIds,PackageFolderContext dataFilesFolder) throws Exception {

        List<FacilioField> notesFields = PackageNotesUtil.getAllNotesCsvFields(noteModule);
        Map<String, FacilioField> allNotesFieldsMap = notesFields.stream().collect(Collectors.toMap(FacilioField::getName, Function.identity(), (name1, name2) -> {
            return name1;
        }));
        Map<String, ComponentType> nameVsComponentType = PackageUtil.nameVsComponentType;

        List<Long> oldIds = new ArrayList<>();
        if (MapUtils.isNotEmpty(moduleIdVsOldIds)) {
            oldIds = moduleIdVsOldIds.get(parentModule.getModuleId());
        }
        NoteContext noteContext = new NoteContext();
        List<CommentSharingContext> commentSharingContexts = new ArrayList<>();
        List<CommentAttachmentContext> commentAttachments = new ArrayList<>();
        List<CommentMentionContext> commentMentionContexts = new ArrayList<>();

        noteContext.setModuleId(parentModule.getModuleId());
        for (int i = 0; i < fieldNames.length; i++) {
            if (i < fieldValues.length) {
                if (StringUtils.isEmpty(fieldValues[i])) {
                    continue;
                }
                String fieldName = fieldNames[i];
                FacilioField field = allNotesFieldsMap.get(fieldName);
                String fieldValue = fieldValues[i];
                if (Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.COMMENT_ATTACHMENTS)) {
                    String[] valueArray = fieldValue.split(",");
                    if (CollectionUtils.isNotEmpty(commentAttachments)) {
                        int index = 0;
                        for (CommentAttachmentContext commentAttachment : commentAttachments) {
                            String value = valueArray[index];
                            if (Objects.equals(fieldName, "attachmentFileName")) {
                                commentAttachment.setFileName(value);
                            } else if (Objects.equals(fieldName, "attachmentContentType")) {
                                commentAttachment.setContentType(value);
                            }else if (Objects.equals(fieldName, "attachmentUniqueIdentifierForFile")) {
                                String filePath = dataFilesFolder.getPath() + File.separator + value;
                                File sourceOrgFile = new File(filePath);
                                File file = new File(sourceOrgFile.getParentFile(),commentAttachment.getFileName());
                                sourceOrgFile.renameTo(file);
                                FileContext fileContext = PackageFileUtil.addFileToStore(file, commentAttachment.getContentType());
                                commentAttachment.setFileId(fileContext.getFileId());
                            }else if (Objects.equals(fieldName, "type")){
                                commentAttachment.setType(Integer.parseInt(value));
                            }
                            index++;
                        }
                    } else {
                        for (String commentAttachmentValue : valueArray) {
                            CommentAttachmentContext commentAttachment = new CommentAttachmentContext();
                            if (Objects.equals(fieldName, "attachmentFileName")) {
                                commentAttachment.setFileName(commentAttachmentValue);
                            } else if (Objects.equals(fieldName, "attachmentContentType")) {
                                commentAttachment.setContentType(commentAttachmentValue);
                            }else if (Objects.equals(fieldName, "attachmentUniqueIdentifierForFile")) {
                                String filePath = dataFilesFolder.getPath() + File.separator + fieldValue;
                                File sourceOrgFile = new File(filePath);
                                File file = new File(sourceOrgFile.getParentFile(),commentAttachment.getFileName());
                                sourceOrgFile.renameTo(file);
                                FileContext fileContext = PackageFileUtil.addFileToStore(file, commentAttachment.getContentType());
                                commentAttachment.setFileId(fileContext.getFileId());
                            }else if (Objects.equals(fieldName, "type")){
                                commentAttachment.setType(Integer.parseInt(commentAttachmentValue));
                            }
                            commentAttachments.add(commentAttachment);
                        }

                    }

                } else if (Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.COMMENT_SHARING)) {
                    String[] valueArray = fieldValue.split(",");
                    for (String sharing : valueArray) {
                        CommentSharingContext sharingContext = new CommentSharingContext();
                        Object appIdObj = DataMigrationUtil.getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, FacilioConstants.ContextNames.APP_ID, sharing);
                        long appId = Long.parseLong(appIdObj.toString());
                        sharingContext.setAppId(appId);
                        sharingContext.setParentModuleId(parentModule.getModuleId());
                        commentSharingContexts.add(sharingContext);
                    }

                } else if (Objects.equals(field.getModule().getName(), FacilioConstants.ContextNames.COMMENT_MENTIONS)) {
                    String[] valueArray = fieldValue.split(",");
                    if (CollectionUtils.isNotEmpty(commentMentionContexts)) {
                        int index = 0;
                        for (CommentMentionContext commentMention : commentMentionContexts) {
                            String value = valueArray[index];
                            if (Objects.equals(fieldName, "mentionedRecordId")) {
                                long oldId = Long.parseLong(value);
                                if (!oldIds.contains(oldId)) {
                                    oldIds.add(oldId);
                                }
                                commentMention.setMentionedRecordId(oldId);
                            } else if (Objects.equals(fieldName, "mentionedModuleId")) {
                                commentMention.setMentionedModuleId(parentModule.getModuleId());
                            } else if (Objects.equals(fieldName, "parentModuleId")) {
                                commentMention.setParentModuleId(parentModule.getModuleId());
                            } else if (Objects.equals(fieldName, "mentionType")) {
                                commentMention.setMentionType(Integer.parseInt(value));
                            }
                            index++;
                        }
                    } else {
                        for (String commentMentionValue : valueArray) {
                            CommentMentionContext commentMention = new CommentMentionContext();
                            if (Objects.equals(fieldName, "mentionedRecordId")) {
                                long oldId = Long.parseLong(commentMentionValue);
                                if (!oldIds.contains(oldId)) {
                                    oldIds.add(oldId);
                                }
                                commentMention.setMentionedRecordId(oldId);
                            } else if (Objects.equals(fieldName, "mentionedModuleId")) {
                                commentMention.setMentionedModuleId(parentModule.getModuleId());
                            } else if (Objects.equals(fieldName, "parentModuleId")) {
                                commentMention.setParentModuleId(parentModule.getModuleId());
                            } else if (Objects.equals(fieldName, "mentionType")) {
                                commentMention.setMentionType(Integer.parseInt(commentMentionValue));
                            }
                            commentMentionContexts.add(commentMention);
                        }

                    }
                } else {
                    if (Objects.equals(fieldName, "id")) {
                        noteContext.setId(Long.parseLong(fieldValue));
                    } else if (Objects.equals(fieldName, "body")) {
                        noteContext.setBody(fieldValue);
                    } else if (Objects.equals(fieldName, "bodyHTML")) {
                        noteContext.setBodyHTML(fieldValue);
                    } else if (Objects.equals(fieldName, "createdBy")) {
                        Object userId = DataMigrationUtil.getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, FacilioConstants.ContextNames.USERS, fieldValue);
                        User createdBy = AccountUtil.getUserBean().getUser(Long.parseLong(userId.toString()), false);
                        noteContext.setCreatedBy(createdBy);
                    } else if (Objects.equals(fieldName, "createdTime")) {
                        noteContext.setCreatedTime(Long.parseLong(fieldValue));
                    } else if (Objects.equals(fieldName, "notifyRequester")) {
                        noteContext.setNotifyRequester(Boolean.parseBoolean(fieldValue));
                    } else if (Objects.equals(fieldName, "parentId")) {
                        long oldId = Long.parseLong(fieldValue);
                        if (!oldIds.contains(oldId)) {
                            oldIds.add(oldId);
                        }
                        noteContext.setParentId(oldId);
                    } else if (Objects.equals(fieldName, "sysModifiedBy")) {
                        Object userId = DataMigrationUtil.getComponentIdFromChaneSet(nameVsComponentType, packageChangSets, FacilioConstants.ContextNames.USERS, fieldValue);
                        User sysModifiedBy = AccountUtil.getUserBean().getUser(Long.parseLong(userId.toString()), false);
                        noteContext.setSysModifiedBy((IAMUser) sysModifiedBy);
                    } else if (Objects.equals(fieldName, "sysModifiedTime")) {
                        noteContext.setSysModifiedTime(Long.parseLong(fieldValue));
                    } else if (Objects.equals(fieldName, "parentNote")) {
                        NoteContext parentNote = new NoteContext();
                        parentNote.setId(Long.parseLong(fieldValue));
                        noteContext.setParentNote(parentNote);
                    }
                }
            }
        }

        noteContext.setCommentSharing(commentSharingContexts);
        noteContext.setMentions(commentMentionContexts);
        noteContext.setAttachments(commentAttachments);
        moduleIdVsOldIds.put(parentModule.getModuleId(), oldIds);

        return noteContext;
    }


    public static Map<Long, Long> addNotes(List<NoteContext> notes, String moduleName, String parentModuleName) throws Exception {

        Map<Long, Long> oldVsNewIds = new HashMap<>();
        if (CollectionUtils.isEmpty(notes)) {
            return oldVsNewIds;
        }

        LinkedList<Long> oldNotesIds = notes.stream().map(note -> note.getId()).collect(Collectors.toCollection(LinkedList::new));

        FacilioChain chain = TransactionChainFactory.getAddNotesChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.NOTE_LIST, notes);
        context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        chain.execute();

        int i = 0;
        for(NoteContext oldNote : notes){
            oldVsNewIds.put(oldNotesIds.get(i),oldNote.getId());
            i++;
        }

        return oldVsNewIds;
    }
}