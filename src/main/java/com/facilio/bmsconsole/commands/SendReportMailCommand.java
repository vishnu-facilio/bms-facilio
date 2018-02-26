package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.pdf.PdfUtil;

public class SendReportMailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT_CONTEXT);
		
		int type = (int) context.get(FacilioConstants.ContextNames.FILE_FORMAT);
		EMailTemplate eMailTemplate = (EMailTemplate) context.get(FacilioConstants.Workflow.TEMPLATE);
		eMailTemplate.setFrom("support@${org.orgDomain}.facilio.com");
		
		String fileName = "Report-" + (module.getDisplayName() != null && !module.getDisplayName().isEmpty() ? module.getDisplayName() : module.getName()) + "-" + reportContext.getId();
		String fileUrl = null;
		FileFormat fileFormat = FileFormat.getFileFormat(type);
		if(fileFormat == FileFormat.PDF) {
			fileUrl = PdfUtil.exportUrlAsPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(),AwsUtil.getConfig("clientapp.url")+"/app/wo/reports/view/"+reportContext.getId());
			fileName += ".pdf";
		}
		else {
			fileUrl = ExportUtil.exportData(fileFormat, module, view.getFields(), records);
			if (fileFormat == FileFormat.CSV) {
				fileName += ".csv";
			}
			else if(fileFormat == fileFormat.XLS) {
				fileName += ".xls";
			}
		}
		
		Map<String, Object> placeHolders = new HashMap<String,Object>();
		CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
 		AwsUtil.sendEmail(eMailTemplate.getTemplate(placeHolders), Collections.singletonMap(fileName, fileUrl));
 		
		return false;
	}

}
