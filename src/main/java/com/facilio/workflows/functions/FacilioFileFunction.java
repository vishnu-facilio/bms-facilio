package com.facilio.workflows.functions;

import com.amazonaws.util.IOUtils;
import com.facilio.fs.FileInfo;
import com.facilio.ftp.SFTPUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.FILE_FUNCTION)
public class FacilioFileFunction {
	public Object getFileContent(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

		Long fileId = (long) Double.parseDouble(objects[0].toString());
		FileStore fs = FacilioFactory.getFileStore();


		try (InputStream is = fs.readFile(fileId)) {
			String content = IOUtils.toString(is);
			return content;
		}
		catch(Exception e) {
			throw new RuntimeException("Cannot Read File :: "+fileId,e);
		}
	}

	public Object putFileToRemoteFileServer(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		String stringContent = objects[5].toString();

		try(InputStream is = new ByteArrayInputStream(stringContent.getBytes())) {

			SFTPUtil.putFile((String)objects[0], objects[1].toString(), objects[2].toString(), objects[3].toString(), is, objects[4].toString());
		}

		return null;
	}

	public Object getFileFromRemoteFileServer(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		String filePath = objects[4].toString();

		String result = SFTPUtil.getFile((String)objects[0], objects[1].toString(), objects[2].toString(), objects[3].toString(), filePath);

		FileStore fs = FacilioFactory.getFileStore();

		long fileId = fs.addFile(objects[5].toString(), result, objects[6].toString());

		return fileId;
	}

	public Object getFileInfo(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		Long fileId = (long) Double.parseDouble(objects[0].toString());
		FileStore fs = FacilioFactory.getFileStore();

		FileInfo fileInfo = fs.getFileInfo(fileId);
		if (fileInfo != null) {
			return FieldUtil.getAsProperties(fileInfo);
		}
		return null;
	}
}
