package com.facilio.bmsconsole.context;

import com.facilio.modules.FieldUtil;
import org.apache.commons.beanutils.PropertyUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.templates.TemplateAttachmentType;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateFileFieldContext extends TemplateAttachment {
	
	private long fieldId;
	
	@Override
	protected long fetchFileId(Object record) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(fieldId);
		if (record instanceof ModuleBaseWithCustomFields) {
			ModuleBaseWithCustomFields bean = (ModuleBaseWithCustomFields) record;
			Object prop = null;
			String key = field.getName()+"Id";
			if (field.isDefault()) {
				if (PropertyUtils.getPropertyDescriptor(bean, key) != null) {
					prop = FieldUtil.getProperty(record, key);
				}
			}
			else {
				prop = bean.getDatum(key);
			}
			
			if (prop != null) {
				long fileId = (long) prop;
				FileStore fs = FacilioFactory.getFileStore();
				FileInfo fileInfo = fs.getFileInfo(fileId);
				setFileName(fileInfo.getFileName());
				return fileId;
			}
		}
		return -1l;
	}
	
	@Override
	public TemplateAttachmentType getType() {
		return TemplateAttachmentType.FIELD;
	}

}
