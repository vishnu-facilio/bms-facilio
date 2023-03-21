package com.facilio.bmsconsole.context;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.templates.TemplateAttachmentType;
import com.facilio.pdf.PdfUtil;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateUrlContext extends TemplateAttachment {

	private String urlString;

	@Override
	protected long fetchFileId(Object record) throws Exception {
		return PdfUtil.exportUrlAsFileId(urlString, getFileName());
	}

	@Override
	public TemplateAttachmentType getType() {
		return TemplateAttachmentType.URL;
	}

	@Override
	@JsonIgnore
    @JSON(serialize = false)
	public String getFileName() {
		return "Pdf - " + DateTimeUtil.getFormattedTime(System.currentTimeMillis(), "dd-MM-yyyy HH-mm");
	}

}
