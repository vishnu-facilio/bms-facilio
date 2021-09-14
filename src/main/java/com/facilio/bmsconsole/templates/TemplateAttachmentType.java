package com.facilio.bmsconsole.templates;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.context.TemplateFileFieldContext;
import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateAttachmentType implements FacilioIntEnum {

	FILE(
		TemplateFileContext.class,
		ModuleFactory.getTemplateFileModule(),
		FieldFactory.getTemplateFileFields()
	),
	FIELD(
		TemplateFileFieldContext.class,
		ModuleFactory.getTemplateFileFieldAttachmentModule(),
		FieldFactory.getTemplateFileFieldFields()
	),
	URL(
		TemplateUrlContext.class,
		ModuleFactory.getTemplateUrlAttachmentModule(),
		FieldFactory.getTemplateUrlFields()
	)
	;


	private Class<? extends TemplateAttachment> attachmentClass;

	private FacilioModule module;

	private List<FacilioField> fields;
	public  List<FacilioField> getFields() {
		return new ArrayList<>(fields);
	}

	public static TemplateAttachmentType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}

}
