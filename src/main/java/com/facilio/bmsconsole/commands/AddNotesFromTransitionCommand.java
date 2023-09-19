package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddNotesFromTransitionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        FacilioModule noteModule = getNotesModule(moduleName);
        if (noteModule == null){
            return false;
        }

        for (ModuleBaseWithCustomFields record: records) {
            V3Context commentRecord = (V3Context) record;
            Map<String, Object> comment = commentRecord.getTransitionCommentData();
            if (comment == null ) {
                continue;
            }

            handleNoteForModule(comment,moduleName,noteModule,record);

        }
        return false;
    }

    public static FacilioModule getNotesModule(String moduleName) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule noteModule = new FacilioModule();

        switch (moduleName){
            case "serviceRequest":
                noteModule = modBean.getModule(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
                break;
            default:
                List<FacilioModule> noteModules = modBean.getSubModules(moduleName, FacilioModule.ModuleType.NOTES);
                if (CollectionUtils.isNotEmpty(noteModules)){
                    noteModule = noteModules.get(0);
                }
        }

        return noteModule;
    }

    public static void handleNoteForModule(Map<String,Object> note,String parentModuleName,FacilioModule noteModule,ModuleBaseWithCustomFields record) throws Exception{
        switch (parentModuleName){
            case "serviceRequest":
                note.put("recordId",record.getId());
                Object messageType = note.get("messageType");
                EmailConversationThreadingContext.Message_Type messageTypeValue = EmailConversationThreadingContext.Message_Type.valueOf(Integer.valueOf(messageType.toString()));
                if (messageTypeValue.equals(EmailConversationThreadingContext.Message_Type.REPLY)){
                    throw new IllegalArgumentException("Only comment is supported");
                }
                V3Util.createRecord(noteModule,note);
                break;
            default:
                note.put("parentId",record.getId());
                addNotesForModule(note,parentModuleName,noteModule.getName());
                break;

        }

    }
    public static void addNotesForModule(Map<String,Object> note,String parentModuleName,String noteModuleName) throws Exception{

        FacilioChain chain = TransactionChainFactory.getAddNotesChain();

        FacilioContext context = chain.getContext();

        NoteContext noteContext = FieldUtil.getAsBeanFromMap(note, NoteContext.class);

        context.put(FacilioConstants.ContextNames.MODULE_NAME, noteModuleName);
        context.put(FacilioConstants.ContextNames.PARENT_MODULE_NAME, parentModuleName);
        context.put(FacilioConstants.ContextNames.NOTE, noteContext);

        chain.execute();

    }
}
