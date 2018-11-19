package com.facilio.bmsconsole.modules;

import com.facilio.fs.FileInfo.FileFormat;

public class FileField extends FacilioField {
	
	private static final long serialVersionUID = 1L;
	
	public FileField() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	protected FileField(FileField field) { // Do not forget to Handle here if new property is added
		// TODO Auto-generated constructor stub
		super(field);
		this.format = field.format;
	}
	
	@Override
	public FileField clone() {
		// TODO Auto-generated method stub
		return new FileField(this);
	}
	
	private FileFormat format;
	public FileFormat getFormatEnum() {
		return format;
	}
	public int getFormat() {
		if (format != null) {
			return format.getIntVal();
		}
		return -1;
	}
	public void setFormat(FileFormat format) {
		this.format = format;
	}
	public void setFormat(int formatVal) {
		this.format = FileFormat.getFileFormat(formatVal);
	}

}
