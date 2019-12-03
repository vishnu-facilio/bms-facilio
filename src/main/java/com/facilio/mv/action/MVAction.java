	package com.facilio.mv.action;

	import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class MVAction extends FacilioAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	MVProjectWrapper mvProjectWrapper;
	
	Criteria criteria;
	
	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	int widgetId;
	public int getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(int widgetId) {
		this.widgetId = widgetId;
	}

	Boolean isOpen;

	long mvProjectId = -1;
	
	public Boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}
	public long getMvProjectId() {
		return mvProjectId;
	}

	public void setMvProjectId(long mvProjectId) {
		this.mvProjectId = mvProjectId;
	}

	public MVProjectWrapper getMvProjectWrapper() {
		return mvProjectWrapper;
	}

	public void setMvProjectWrapper(MVProjectWrapper mvProjectWrapper) {
		this.mvProjectWrapper = mvProjectWrapper;
	}

	public String addMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getAddMVProjectChain();
		addMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
	
	public String updateMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();

		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		
		context.put(MVUtil.MV_PROJECT_WRAPPER_OLD, MVUtil.getMVProject(mvProjectWrapper.getMvProject().getId()));
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getUpdateMVProjectChain();
		addMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}

	public String updateMVProjectMeta() throws Exception {

		FacilioContext context = new FacilioContext();

		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);

		FacilioChain updateMVProjectChain =  TransactionChainFactory.getUpdateMVProjectMetaChain();
		updateMVProjectChain.execute(context);

		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
	
	public String getMVProjectWidgetResult() throws Exception {
		
		mvProjectWrapper = MVUtil.getMVProject(mvProjectId);

		FacilioContext context = new FacilioContext();

		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		
		context.put(MVUtil.MV_PROJECTS_WIDGET_ID, widgetId);

		FacilioChain widgetResultChain =  ReadOnlyChainFactory.fetchMVWidgetResultChain();
		widgetResultChain.execute(context);

		setResult(MVUtil.RESULT_JSON, context.get(MVUtil.RESULT_JSON));
		return SUCCESS;
	}
	
	public String getMVProject() throws Exception {
		
		mvProjectWrapper = MVUtil.getMVProject(mvProjectId);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
	
	public String getMVProjectList() throws Exception {
		
		setResult(MVUtil.MV_PROJECTS, MVUtil.getMVProjects(isOpen,criteria));
		return SUCCESS;
	}
	
	public String deleteMVProject() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		
		FacilioChain deleteMVProjectChain =  TransactionChainFactory.getDeleteMVProjectChain();
		deleteMVProjectChain.execute(context);
		
		setResult(MVUtil.MV_PROJECT_WRAPPER, mvProjectWrapper);
		return SUCCESS;
	}
}
