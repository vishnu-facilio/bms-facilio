/**
 * 
 */
package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;

/**
 * @author facilio
 *
 */
public class AutoCommissionAction extends FacilioAction {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AutoCommissionAction.class.getName());

	@SuppressWarnings("unused")
	public String  autoCommissionData()throws Exception{

		FacilioContext context = new FacilioContext();

		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
		context.put(FacilioConstants.ContextNames.AUTO_COMMISSION_DATA, markedData);
		context.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
		try {
			if(markedData != null && controllerId != -1 ) {
				Chain mappingChain = TransactionChainFactory.updateAutoCommissionCommand();
				mappingChain.execute(context);
			}
		}
		catch(Exception e) {
			throw new IllegalArgumentException("Parameter should not be null in AutoCommission  :");

		}

		setResult("result", "success");
		return SUCCESS;

	}

	@SuppressWarnings("unused")
	public String getControllerAsset() throws Exception {
		// TODO Auto-generated method stub

		if(isExistingControllerId(controllerId)) {
			setResult("controllerId", true);
		}


		return SUCCESS;
	}

	
	public static boolean isExistingControllerId(long controllerId) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioModule assetCategoryModule = modBean.getModule("controller");

		SelectRecordsBuilder<AssetCategoryContext> builder = new SelectRecordsBuilder<AssetCategoryContext>()
				.module(assetCategoryModule)
				.beanClass(AssetCategoryContext.class)
				.select(modBean.getAllFields(assetCategoryModule.getName()));

		System.out.println("####Fields is .."+modBean.getAllFields(assetCategoryModule.getName()));
		List<Map<String, Object>> cist = builder.getAsProps();	
		for (Map<String, Object> map : cist) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				if(key.equalsIgnoreCase("controllerId")) {
					long id = (long) entry.getValue();
					if(id == controllerId) {
						System.out.println("Id is "+id+"controlerrid id :"+controllerId);
						return true;
					}


				}
			}
		}


		return false;
	}

	public static long getAssetId(long controllerId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		
		SelectRecordsBuilder<ResourceContext> selectBuilder = new SelectRecordsBuilder<ResourceContext>()
				.moduleName(module.getName())
				.beanClass(ResourceContext.class)
				.select(modBean.getAllFields(module.getName()))
				.table(module.getTableName())
				.andCustomWhere("CONTROLLER_ID = ?", controllerId)
				;
		List<ResourceContext> assets = selectBuilder.get();
		if(assets != null && !assets.isEmpty()) {
			return assets.get(0).getId();
		}
		return -1;
	}
	private long spaceId=-1;
	
	public long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	private long siteId=-1;
	public long getSiteId() {
		return siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	private List<Map<String, Object>> markedData;
	public List<Map<String, Object>> getMarkedData() {
		return markedData;
	}

	public void setMarkedData(List<Map<String, Object>> markedData) {
		this.markedData = markedData;
	}

	private long controllerId =-1;

	public long getControllerId() {
		return controllerId;
	}

	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
}