package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class NewPermissionAction extends FacilioAction {
	List<NewPermission> permissions;

	public List<NewPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<NewPermission> permissions) {
		this.permissions = permissions;
	}

	long tabId;

	public long getTabId() {
		return tabId;
	}

	public void setTabId(long tabId) {
		this.tabId = tabId;
	}

	long permissionId;

	public long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}

	List<Long> permissionIds;

	public List<Long> getPermissionIds() {
		return permissionIds;
	}

	public void setPermissionIds(List<Long> permissionIds) {
		this.permissionIds = permissionIds;
	}

	public String addNewPermissions() throws Exception {
		FacilioChain chain = TransactionChainFactory.getAddNewPermissionChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.WEB_TAB_ID, tabId);
		context.put(FacilioConstants.ContextNames.NEW_PERMISSIONS, getPermissions());
		chain.execute();
		return SUCCESS;
	}
	
	public String deleteNewPermissions() throws Exception {
		FacilioChain chain = TransactionChainFactory.getDeleteNewPermissionChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, getPermissionIds());
		chain.execute();
		return SUCCESS;
	}
	
	public String getTabPermissions() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getTabPermissionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WEB_TAB_ID, getTabId());
        chain.execute();

        setResult(FacilioConstants.ContextNames.PERMISSIONS, context.get(FacilioConstants.ContextNames.PERMISSIONS));

        return SUCCESS;
    }

}
