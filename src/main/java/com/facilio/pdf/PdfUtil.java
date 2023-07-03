package com.facilio.pdf;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.auth.cookie.FacilioCookie;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.util.GlobalScopeUtil;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.executor.CommandExecutor;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.PublicFileUtil;
import com.facilio.util.RequestUtil;
import com.opensymphony.xwork2.ActionContext;
@Log4j
public class PdfUtil {

    private static final String NODE = FacilioProperties.getNodeJSLocation();
    private static final String RENDER_PUPETTEER_JS = FacilioProperties.getPdfjsLocation() + "/puppeteer-render.js";
    private static final String RENDER_PUPETTEER_JS_WIDGET = FacilioProperties.getPdfjsLocation() + "/puppeteer-render-widget.js";
    private static final Logger LOGGER = LogManager.getLogger(PdfUtil.class.getName());

	public static String convertUrlToPdf(String url, String htmlContent, JSONObject additionalInfo , FileFormat... formats) throws Exception {

		FacilioUtil.throwRunTimeException(StringUtils.isEmpty(url),"Invalid URL");

		isAllowedDomains(url);

		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}
		String pdfFileLocation = getFileLocation(format);
		String serverName = getServerName(url);
		if (StringUtils.isEmpty(htmlContent)) {
			htmlContent = "false";
		}
		else {
			htmlContent = Base64.getEncoder().encodeToString(htmlContent.getBytes());
		}
		if (additionalInfo == null) {
			additionalInfo = new JSONObject();
		}
		setAdditionalInfo(additionalInfo);
		if(additionalInfo.containsKey("switchSiteValue")){
			url = PdfUtil.addCurrentScopedSiteInUrl(url);
		}

		String[] command = new String[] {NODE, RENDER_PUPETTEER_JS, url, pdfFileLocation, getToken(), serverName, htmlContent, additionalInfo.toString()};
		int exitStatus = CommandExecutor.execute(command);
		LOGGER.debug("Converted to pdf with exit status : " + exitStatus + " and file " + pdfFileLocation);
		
		return pdfFileLocation;
	}

	private static void isAllowedDomains(String url) throws Exception {
			URI uri = new URI(url);
			String domain = uri.getHost();
			LOGGER.info("Current App domain : "+ domain);
			AppDomain appDomain = IAMAppUtil.getAppDomain(domain);
			FacilioUtil.throwRunTimeException(appDomain == null,"Invalid App Domain");
	}
	
	public static String convertWidgetToPdf(String url, JSONObject exportOptions, JSONObject widgetContext, FileFormat... formats) {
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}
		String pdfFileLocation = getFileLocation(format);
		String serverName = getServerName(url);
		JSONObject additionalInfo = new JSONObject();
		setAdditionalInfo(additionalInfo);
		
		if (exportOptions == null) {
			exportOptions = new JSONObject();
		}
		
		if (widgetContext == null) {
			widgetContext = new JSONObject();
		}

		String[] command = new String[] {NODE, RENDER_PUPETTEER_JS_WIDGET, url, pdfFileLocation, getToken(), serverName, format.toString(), additionalInfo.toString(), exportOptions.toString(), widgetContext.toString()};
		int exitStatus = CommandExecutor.execute(command);
		LOGGER.debug("Converted to pdf with exit status : " + exitStatus + " and file " + pdfFileLocation);
		
		return pdfFileLocation;
	}
	
	private static String getServerName(String url) {
		HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
		if (request == null) {
			String serverName = FacilioProperties.getAppDomain();
			return serverName.split(":")[0];
		}
		else {
			try {
				URI uri = new URI(url);
				return uri.getHost();
			} catch (URISyntaxException e) {
				LOGGER.info("Exception occurred", e);
			}
			return request.getServerName().replace(RequestUtil.getProtocol(request)+"://", StringUtils.EMPTY);
		}
	}
	
	private static String getToken() {
		if(AccountUtil.getCurrentUser().isPortalUser()){
			HttpServletRequest request = ServletActionContext.getRequest();
			String Faciliotoken = null;
			Faciliotoken = FacilioCookie.getUserCookie(request, "fc.idToken.facilio");
			return Faciliotoken;
		}
		return IAMUserUtil.createJWT("id", "auth0", String.valueOf(AccountUtil.getCurrentUser().getUid()), System.currentTimeMillis()+60*60000);
	}
	
	private static String getFileLocation(FileFormat format) {
		File pdfDirectory = new File(System.getProperty("java.io.tmpdir")+"/"+AccountUtil.getCurrentOrg().getOrgId()+"/");
		String pdfFileLocation = null;
		boolean directoryExits = (pdfDirectory.exists() && pdfDirectory.isDirectory());
		if( ! directoryExits) {
			directoryExits = pdfDirectory.mkdirs();
		}
		if(directoryExits) {
			try {
				File pdfFile = File.createTempFile("report-", format.getExtention(), pdfDirectory);
				pdfFileLocation = pdfFile.getAbsolutePath();
			} catch (IOException e) {
				LOGGER.info("Exception occurred", e);
			}
		}
		return pdfFileLocation;
	}
	
	private static void setAdditionalInfo(JSONObject additionalInfo) {
		additionalInfo.put("orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));
		additionalInfo.put("orgDomain", AccountUtil.getCurrentOrg().getDomain());
		if (AccountUtil.getCurrentSiteId() != -1) {
			additionalInfo.put("currentSite", AccountUtil.getCurrentSiteId());
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		if(request != null ){
			String switchVariable = request.getHeader("X-Switch-Value");
			if (StringUtils.isNotEmpty(switchVariable))
			{
				additionalInfo.put("switchSiteValue", switchVariable);
			}
		}
	}

	public static String exportUrlAsPdf(String url, FileFormat... formats) throws Exception {
		return exportUrlAsPdf(url, false, null, formats);
	}

	public static String exportUrlAsPdf(String url, boolean isPublicUrl, String name, FileFormat... formats) throws Exception {
		return exportUrlAsPdf(url, isPublicUrl, name, null, formats);
	}

	public static String exportUrlAsPdf(String url, boolean isPublicUrl, String name, JSONObject additionalInfo, FileFormat... formats) throws Exception {
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}                
		String pdfFileLocation = convertUrlToPdf(url, null, additionalInfo, format);         
		File pdfFile = new File(pdfFileLocation);
		if(pdfFileLocation != null) {

			FileStore fs = FacilioFactory.getFileStore();
			long fileId = 0;
			try {
				fileId = fs.addFile(name != null ? name+format.getExtention() : pdfFile.getName(), pdfFile, format.getContentType());
				if (isPublicUrl) {
					return fs.getOrgiDownloadUrl(fileId);
				}
				return fs.getDownloadUrl(fileId);
			} catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}


		}
		return null;
	}
	
	public static Long exportUrlAsPDF(String url, String name, JSONObject additionalInfo, FileFormat... formats) throws Exception {
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}                
		String pdfFileLocation = convertUrlToPdf(url, null, additionalInfo, format);         
		File pdfFile = new File(pdfFileLocation);
		if(pdfFileLocation != null) {

			FileStore fs = FacilioFactory.getFileStore();
			long fileId = 0;
			try {
				fileId = fs.addFile(name != null ? name+format.getExtention() : pdfFile.getName(), pdfFile, format.getContentType());
				return fileId;
			} catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}


		}
		return null;
	}
	
	public static String exportWidget(String url, JSONObject exportOptions, JSONObject widgetContext, FileFormat format){      	
		
		String pdfFileLocation = convertWidgetToPdf(url, exportOptions, widgetContext, format);         
		
		if (pdfFileLocation != null) {
			
			File pdfFile = new File(pdfFileLocation);

			FileStore fs = FacilioFactory.getFileStore();
			long fileId = 0;
			try {
				fileId = fs.addFile(pdfFile.getName(), pdfFile, format.getContentType());
				return fs.getDownloadUrl(fileId);
			} catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}
		}
		return null;
	}

	public static String exportUrlAsPublicFilePdf(String url, boolean isPublicUrl, String name, JSONObject additionalInfo, long expiry, FileFormat... formats) throws Exception {
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}                
		String pdfFileLocation = convertUrlToPdf(url, null, additionalInfo, format);         
		File pdfFile = new File(pdfFileLocation);
		if(pdfFileLocation != null) {
			try {
				String publicFileUrl = PublicFileUtil.createPublicFile(pdfFile, name != null ? name+format.getExtention() : pdfFile.getName(), format.getExtention().substring(1), format.getContentType(), expiry);
				return publicFileUrl;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static File exportUrlAsFile(String url, String name, String htmlContent, JSONObject additionalInfo, FileFormat... formats) throws Exception {
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}        

		String pdfFileLocation = convertUrlToPdf(url, htmlContent, additionalInfo, format);

		File pdfFile = new File(pdfFileLocation);
		return pdfFile;
	}

	public static File exportUrlAsFile(String url, String name, FileFormat... formats) throws Exception {
		return exportUrlAsFile(url, name, null, null, formats);
	}

	public static long exportUrlAsFileId(String url, String name, FileFormat... formats) throws Exception {
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}
		String pdfFileLocation = convertUrlToPdf(url, null, null, format);
		File pdfFile = new File(pdfFileLocation);
		if(pdfFileLocation != null) {

			FileStore fs = FacilioFactory.getFileStore();
			long fileId = 0;
			try {
				fileId = fs.addFile(name != null ? name+format.getExtention() : pdfFile.getName(), pdfFile, format.getContentType());
			} catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}
			return fileId;
		}
		return -1;
	}

	public static String addCurrentScopedSiteInUrl(String url)
	{
		try
		{
			StringBuilder sb = null;
			List<SiteContext> sites_list = SpaceAPI.getAllSites();
			if(sites_list != null && sites_list.size() > 0 && sites_list.size() == 1){
				sb=new StringBuilder();
				for(SiteContext site : sites_list){
					sb.append(site.getName()).append(" , ");
				}
			}
			String site_names = sb.toString();
			if(site_names != null && site_names.length() > 0) {
				site_names = site_names.substring(0, site_names.length()-2);
				URI uri = new URI(url);
				String str ="";
				if(uri != null && uri.getQuery() != null){
					return new StringBuilder(url).append("&scoping_site=").append(site_names).toString();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.info("Error while decoding current site");
		}
		return url;
	}
	public static void addScopingSite(String sites, String url)throws Exception{
		URI uri = new URI(url);
		String str ="";
	}
}
