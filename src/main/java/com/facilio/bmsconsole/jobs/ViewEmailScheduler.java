package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.mailtracking.MailConstants;
import com.facilio.mailtracking.context.MailSourceType;
import com.facilio.service.FacilioService;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class ViewEmailScheduler extends FacilioJob {

	private Logger log = LogManager.getLogger(ViewEmailScheduler.class.getName());

	@Override
	public void execute(JobContext jc) {
		
		long jobId = jc.getJobId();
		try {
			log.error("asdfasf afExecution view email scheduling: " + jobId);
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

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule viewModule = modBean.getModule(view.getModuleId());
				view.setModuleName(viewModule.getName());

				// Setting createdBy as currentUser to apply Scoping Config on report generation
				long sysCreatedBy = prop.containsKey("createdBy") ? (long) prop.get("createdBy") : -1;
				User createdByUser = getCreatedByUser(view, sysCreatedBy);
				if (createdByUser != null) {
					setCurrentAccount(AccountUtil.getCurrentOrg(), createdByUser);
				}

				String fileUrl = ExportUtil.exportModule(FileInfo.FileFormat.getFileFormat((int) prop.get("fileFormat")), view.getModuleName(), view.getName(), null,null, true, false, 2000);
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

				emailTemplate.put(MailConstants.Params.SOURCE_TYPE, MailSourceType.SCHEDULED_VIEW.name());
				log.error("to list: " + StringUtils.join(toList, ", "));
				log.error("email template " + emailTemplate.toJSONString());
				log.error(fileUrl);
				emailTemplate.replace("to", toList);
				FacilioFactory.getEmailClient().sendEmail(emailTemplate, files);
			}
		} catch (Exception e) {
			CommonCommandUtil.emailAlert("Exception occurred ViewEmailScheduler", "View ID: "+ jc.getJobId());
			log.info("Exception occurred ViewEmailScheduler", e);
		}
		
	}

	private static User getCreatedByUser(FacilioView view, long peopleId) throws Exception {
		long appId = view.getAppId() > 0 ? view.getAppId() : ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        return (appId > 0 && peopleId > 0) ? V3PeopleAPI.getUserContext(peopleId, appId) : null;
	}

	private static void setCurrentAccount(Organization org, User user) throws Exception {
		Account account = new Account(org, null);
		AccountUtil.setCurrentAccount(account);

		account.setUser(user);
		AccountUtil.setScopingMap(account);
	}
	
}