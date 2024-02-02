package com.facilio.workflows.functions;

import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.COMMENTS_FUNCTION)
public class FacilioCommentsFunction {
    public Object addOrUpdateCommentsSharing(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        NotesAPI.addCommentSharing((String) objects[0],(Long) objects[1],(List<String>) objects[2]);

        return null;
    }
    public Object getNotesForNotesModule(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {
        if(objects.length <= 2) {
            throw new FunctionParamException("Kindly provide necessary arguments");
        }
        String notesModuleName = (String) objects[0];
        if(notesModuleName == null){
            throw new FunctionParamException("Notes module name is mandatory");
        }
        Long recordId = (Long) objects[1];
        if(recordId == null){
            throw new FunctionParamException("Record Id is mandatory");
        }
        String parentModuleName = (String) objects[2];
        List<NoteContext> notesList = NotesAPI.getAllNotes(notesModuleName, recordId, parentModuleName);

        return FieldUtil.getAsMapList(notesList,NoteContext.class);
    }

    public void checkParam(Object... objects) throws Exception {
        if(objects.length <= 0) {
            throw new FunctionParamException("Required Object is null");
        }
    }
}
