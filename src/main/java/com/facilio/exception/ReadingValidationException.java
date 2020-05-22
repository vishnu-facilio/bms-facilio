package com.facilio.exception;

import com.facilio.fw.FacilioException;

public class ReadingValidationException extends FacilioException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String resultEvaluator;
	public String getResultEvaluator() {
		return resultEvaluator;
	}

	public void setResultEvaluator(String resultEvaluator) {
		this.resultEvaluator = resultEvaluator;
	}
	
	private long readingFieldId;
	public long getReadingFieldId() {
		return readingFieldId;
	}

	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
	public ReadingValidationException(long readingFieldId, String message, String resultEvaluator) {
		super(message);
		this.readingFieldId = readingFieldId;
		this.resultEvaluator = resultEvaluator;
	}
}
