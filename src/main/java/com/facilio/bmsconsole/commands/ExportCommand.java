package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

public class ExportCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FileFormat fileFormat = (FileFormat) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String viewName = (String) context.get(FacilioConstants.ContextNames.SUB_VIEW);
		String filters = (String) context.get(FacilioConstants.ContextNames.FILTERS);
		boolean isS3Value = (boolean) context.get(FacilioConstants.ContextNames.IS_S3_VALUE);
		boolean specialFields = (boolean) context.get(FacilioConstants.ContextNames.SPECIAL_FIELDS);
		Integer viewLimit = (Integer) context.get(FacilioConstants.ContextNames.VIEW_LIMIT);
		String fileUrl = ExportUtil.exportModule(fileFormat, moduleName, viewName, filters, isS3Value, specialFields, viewLimit);
		context.put(FacilioConstants.ContextNames.FILE_URL, fileUrl);
		
		return false;
	}

}
