package com.facilio.bundle.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.bundle.context.InstalledBundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.xml.builder.XMLBuilder;

import io.jsonwebtoken.lang.Collections;

public class AddInstalledBundleEntryCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BundleFolderContext rootFolder = (BundleFolderContext) context.get(BundleConstants.BUNDLE_FOLDER);
		
		BundleFileContext bundleXmlFileContext = rootFolder.getFile(BundleConstants.BUNDLE_FILE_NAME+"."+BundleConstants.XML_FILE_EXTN);
		
		XMLBuilder BundleXML = bundleXmlFileContext.getXmlContent();
		
		String globalName = BundleXML.getElement(BundleConstants.GLOBAL_NAME).getText();
		Double incommingVersion = Double.parseDouble(BundleXML.getElement(BundleConstants.VERSION).getText());
		
		InstalledBundleContext lastInstalledBundle = getLastInstalledVersion(globalName);
		
		List<Double> toBeInstalledVersionList = new ArrayList<Double>();
		
		if(lastInstalledBundle != null) {
			
			if(lastInstalledBundle.getInstalledVersion() >= incommingVersion) {
				throw new Exception("Incomming version - "+incommingVersion+" cannot be less than or equal to current version "+lastInstalledBundle.getInstalledVersion());
			}
			
			Double installFrom = lastInstalledBundle.getInstalledVersion()+1;
			
			toBeInstalledVersionList = IntStream.range(installFrom.intValue(), incommingVersion.intValue()+1).asDoubleStream().boxed().collect(Collectors.toList());
		}
		else {
			
			toBeInstalledVersionList = IntStream.range(1, incommingVersion.intValue()+1).asDoubleStream().boxed().collect(Collectors.toList());
		}
		
		context.put(BundleConstants.TO_BE_INSTALLED_VERSIONS_LIST, toBeInstalledVersionList);
		
		
		addInstalledBundleEntry(globalName,incommingVersion);
		return false;
	}

	private InstalledBundleContext addInstalledBundleEntry(String globalName, double incommingVersion) throws Exception {
		
		
		InstalledBundleContext installedBundleContext = new InstalledBundleContext();
		
		installedBundleContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		installedBundleContext.setInstalledTime(DateTimeUtil.getCurrenTime());
		installedBundleContext.setInstalledVersion(incommingVersion);
		installedBundleContext.setBundleGlobalName(globalName);
		
		Map<String, Object> props = FieldUtil.getAsProperties(installedBundleContext);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getInstalledBundleModule().getTableName())
				.fields(FieldFactory.getInstalledBundleFields())
				.addRecord(props);
		
		insert.save();
		
		Long id = (Long)props.get("id");
		
		installedBundleContext.setId(id);
		
		return installedBundleContext;
		
	}

	private InstalledBundleContext getLastInstalledVersion(String globalName) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, FacilioField> installedBundleFieldMap = FieldFactory.getAsMap(FieldFactory.getInstalledBundleFields());
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getInstalledBundleModule().getTableName())
				.select(FieldFactory.getInstalledBundleFields())
				.andCondition(CriteriaAPI.getCondition(installedBundleFieldMap.get("bundleGlobalName"), globalName, StringOperators.IS))
				.orderBy("INSTALLED_TIME desc")
				.limit(1)
				;
		
		List<Map<String, Object>> props = builder.get();
		
		if(!Collections.isEmpty(props)) {
			
			InstalledBundleContext bundle = FieldUtil.getAsBeanFromMap(props.get(0), InstalledBundleContext.class);
			
			return bundle;
		}
		
		return null;
	}

}
