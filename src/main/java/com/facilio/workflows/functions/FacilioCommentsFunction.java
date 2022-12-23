package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.COMMENTS_FUNCTION)
public class FacilioCommentsFunction {
    public Object addOrUpdateCommentsSharing(Map<String, Object> globalParam, Object... objects) throws Exception {

        checkParam(objects);
        NotesAPI.addCommentSharing((String) objects[0],(Long) objects[1],(List<String>) objects[2]);

        return null;
    }

    public void checkParam(Object... objects) throws Exception {
        if(objects.length <= 0) {
            throw new FunctionParamException("Required Object is null");
        }
    }
}
