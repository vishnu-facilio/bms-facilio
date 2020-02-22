package com.facilio.bmsconsole.context;

public class FeedbackKioskContext extends DeviceContext {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long feedbackTypeId=-1;


	public long getFeedbackTypeId() {
		return feedbackTypeId;
	}

	public void setFeedbackTypeId(long feedbackTypeId) {
		this.feedbackTypeId = feedbackTypeId;
	}

	FeedbackTypeContext feedbackType;

	public FeedbackTypeContext getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackTypeContext feedbackType) {
		this.feedbackType = feedbackType;
	}
    
 
    
}
