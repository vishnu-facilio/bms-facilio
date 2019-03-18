package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.fs.FileInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewEmailScheduler extends FacilioJob {

	private Logger log = LogManager.getLogger(ViewEmailScheduler.class.getName());

	@Override
	public void execute(JobContext jc) {
		
		long jobId = jc.getJobId();
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getViewScheduleInfoModule().getTableName())
					.select(FieldFactory.getViewScheduleInfoFields())
					.andCustomWhere("ID = ?", jobId);
			List<Map<String, Object>> props = builder.get();
			if(props != null && !props.isEmpty() && props.get(0) != null) {
				Map<String, Object> prop = props.get(0);

				Template template = TemplateAPI.getTemplate((long) prop.get("templateId"));

				Long viewId = (long) prop.get("viewId");
				FacilioView view = ViewAPI.getView(viewId);

				String fileUrl = ExportUtil.exportModule(FileInfo.FileFormat.getFileFormat((int) prop.get("fileFormat")), view.getModuleName(), view.getName(), null);
				Map<String, String> files = new HashMap<>();
				String fileName = view.getDisplayName();
				FileInfo.FileFormat fileFormat = FileInfo.FileFormat.getFileFormat((int) prop.get("fileFormat"));
				files.put(fileName + fileFormat.getExtention(), fileUrl);

				Map<String, Object> parameters = new HashMap<>();
				CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), parameters);

				JSONObject emailTemplate = template.getTemplate(parameters);
				String toList;

				if (emailTemplate.get("to") instanceof JSONArray) {
					JSONArray array = (JSONArray) emailTemplate.get("to");
					toList = StringUtils.join(array, ",");
				}
				else {
					toList = (String) emailTemplate.get("to");
				}

				emailTemplate.replace("to", toList);
				AwsUtil.sendEmail(emailTemplate, files);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		
	}
	
}