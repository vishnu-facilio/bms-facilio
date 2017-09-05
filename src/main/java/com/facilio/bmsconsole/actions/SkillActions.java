package com.facilio.bmsconsole.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.FormLayout;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.SkillAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class SkillActions extends ActionSupport {

	private SetupLayout setup;

	public SetupLayout getSetup() {
		return this.setup;
	}

	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}

	private ActionForm actionForm;

	public ActionForm getActionForm() {
		return actionForm;
	}

	public String execute() {

		try {
			setSkills(SkillAPI.getAllSkill(OrgInfo.getCurrentOrgInfo().getOrgid()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	private List formlayout;

	public List getFormlayout() {
		return this.formlayout;
	}

	public void setFormlayout(List formlayout) {
		this.formlayout = formlayout;
	}

	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private List<FacilioField> fields;

	public String newSkill() throws Exception {
		setSetup(SetupLayout.getNewSkillLayout());
		FacilioContext context = new FacilioContext();
		Chain newSkill = FacilioChainFactory.getNewSkillChain();
		newSkill.execute(context);

		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));

		return SUCCESS;
	}

	public void setActionForm(ActionForm actionForm) {
		this.actionForm = actionForm;
	}

	/*
	 * public String newSkill() throws Exception {
	 * 
	 * Connection conn = null; try { ModuleBean modBean = (ModuleBean)
	 * BeanFactory.lookup("ModuleBean", OrgInfo.getCurrentOrgInfo().getOrgid());
	 * fields = modBean.getAllFields(FacilioConstants.ContextNames.SKILL);
	 * 
	 * conn = FacilioConnectionPool.INSTANCE.getConnection();
	 * setModuleName("Skill");
	 * setFormlayout(FormLayout.getNewSkillLayout(fields)); } catch(SQLException
	 * e) { throw e; } finally { if(conn != null) { try { conn.close(); }
	 * catch(SQLException e) { e.printStackTrace(); } } } return SUCCESS; }
	 */

	public String newSkillDialog() throws Exception {

		Connection conn = null;
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", OrgInfo.getCurrentOrgInfo().getOrgid());
			fields = modBean.getAllFields(FacilioConstants.ContextNames.SKILL);

			conn = FacilioConnectionPool.INSTANCE.getConnection();
			setModuleName("Skill");
			setFormlayout(FormLayout.getNewSkillLayout(fields));
		} catch (SQLException e) {
			throw e;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return SUCCESS;
	}

	public String addSkill() throws Exception {

		//skill.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SKILL, skill);
		Chain addSkill = FacilioChainFactory.getAddSkillChain();
		addSkill.execute(context);
		context.get(FacilioConstants.ContextNames.SKILL);
		
		//long skillId = SkillAPI.addSkill(skill);

		setSkillId(skill.getId());

		return SUCCESS;
	}
	
	public String editSkill() throws Exception {
		
		setSetup(SetupLayout.getEditSkillLayout());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, skillId);
		Chain editSkill = FacilioChainFactory.getSkillChain();
		editSkill.execute(context);
		context.get(FacilioConstants.ContextNames.SKILL);
		setSkill((SkillContext) context.get(FacilioConstants.ContextNames.SKILL));
		setActionForm((ActionForm) context.get(FacilioConstants.ContextNames.ACTION_FORM));
		return SUCCESS;
	}

	public String updateSkill() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SKILL, getSkill());
		Command updateSkill = FacilioChainFactory.getUpdateSkillCommand();
		updateSkill.execute(context);
		
		return SUCCESS;
	}
	
	public String deleteSkill() throws Exception
	{
		FacilioContext context = new FacilioContext();
		//context.put(FacilioConstants.ContextNames.SKILL, getSkill());
		context.put(FacilioConstants.ContextNames.ID, getSkillId());
		Command deleteSkill = FacilioChainFactory.getDeleteSkillCommand();
		deleteSkill.execute(context);
		
		return SUCCESS;
	}
	
	private SkillContext skill;

	public SkillContext getSkill() {
		return skill;
	}

	public void setSkill(SkillContext skill) {
		this.skill = skill;
	}

	private long skillId;

	public long getSkillId() {
		return skillId;
	}

	public void setSkillId(long skillId) {
		this.skillId = skillId;
	}

	private List<SkillContext> skills = null;

	public List<SkillContext> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillContext> skills) {
		this.skills = skills;
	}

	public String getModuleLinkName() {
		return FacilioConstants.ContextNames.SKILL;
	}

	public ViewLayout getViewlayout() {
		return ViewLayout.getViewSkillLayout();
	}

	public String getViewName() {
		return "Skills";
	}

	public String getNewActionType() {
		return "dialog"; // dialog or empty string
	}

	public List<SkillContext> getRecords() {
		return skills;
	}

	public String skillsList() throws Exception {

		setSetup(SetupLayout.getSkillsListLayout());
		setSkills(SkillAPI.getAllSkill(OrgInfo.getCurrentOrgInfo().getOrgid()));

		return SUCCESS;
	}
}