package com.facilio.workflows.functions;

import com.amazonaws.HttpMethod;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.CONNECTION_FUNCTION)
public class FacilioConnectionFunctions {
	public Object get(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ConnectionContext connectionContext = (ConnectionContext)objects[0];

		String url = (String) objects[1];

		Map<String,String> params = null;
		Map<String,String> headerParams = null;
		String bodyString = null;
		String bodyType = null;

		if(objects.length >2) {
			params = (Map<String, String>) objects[2];
		}
		if(objects.length >3) {
			headerParams = (Map<String, String>) objects[3];
		}

		String res = ConnectionUtil.getUrlResult(connectionContext, url, params, HttpMethod.GET,bodyString,bodyType,headerParams,null);

		return res;
	}

	public Object post(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ConnectionContext connectionContext = (ConnectionContext)objects[0];

		String url = (String) objects[1];

		String bodyString = null;
		String bodyType = null;
		Map<String,String> params = null;
		Map<String,String> headerParams = null;
		if(objects.length == 3) {
			params = (Map<String, String>) objects[2];
		}
		else if (objects.length >= 4) {
			bodyString = objects[2].toString();
			bodyType = objects[3].toString();
			if(objects.length == 5) {
				headerParams = (Map<String, String>) objects[4];
			}
		}

		String res = ConnectionUtil.getUrlResult(connectionContext, url, params, HttpMethod.POST,bodyString,bodyType,headerParams,null);

		return res;
	}

	public Object postWithFiles(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ConnectionContext connectionContext = (ConnectionContext)objects[0];

		String url = (String) objects[1];

		Map<String,String> headers = null;
		Map<String,String> params = null;
		Map<String,File> files = null;
		if(objects.length > 1) {
			headers = (Map<String, String>) objects[2];
			if(objects.length > 2) {
				params = (Map<String, String>) objects[3];
				if(objects.length > 3) {
					Map<String,Long> fileIdMap = (Map<String,Long>) objects[4];

					if(fileIdMap != null) {
						files = new HashMap<String, File>();

						FileStore fs = FacilioFactory.getFileStore();

						for(Entry<String, Long> set : fileIdMap.entrySet()) {
							FileInfo fileInfo = fs.getFileInfo(set.getValue());
							InputStream ipStream = fs.readFile(set.getValue());
							File file = new File(fileInfo.getFileName());
							FileUtils.copyInputStreamToFile(ipStream, file);
							files.put(set.getKey(), file);
						}
					}
				}
			}

		}

		String res = ConnectionUtil.getUrlResult(connectionContext, url, params, HttpMethod.POST,null,null,headers,files);

		return res;
	}

	public Object getAccessToken(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ConnectionContext connectionContext = (ConnectionContext)objects[0];

		return connectionContext.getAccessToken();
	}

	public Object asMap(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}
		ConnectionContext connectionContext = (ConnectionContext)objects[0];

		return FieldUtil.getAsProperties(connectionContext);
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}