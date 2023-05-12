package com.facilio.fw.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.facilio.bmsconsole.ModuleWidget.ModuleWidgetsUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetConfig;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigChain;
import com.facilio.activity.ActivityType;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.backgroundactivity.util.BackgroundActivityUtil;
import com.facilio.bmsconsoleV3.commands.AddSignupDataCommandV3;
import com.facilio.client.app.beans.ClientAppBean;
import com.facilio.client.app.pojo.ClientAppConfig;
import com.facilio.client.app.util.ClientAppUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.TransactionMonitor;
import com.facilio.db.util.DBConf;
import com.facilio.db.util.SQLScriptRunner;
import com.facilio.filters.HealthCheckFilter;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.LRUCache;
import com.facilio.fw.cache.RedisManager;
import com.facilio.fw.validators.Date;
import com.facilio.identity.client.IdentityClient;
import com.facilio.fw.validators.*;
import com.facilio.iam.accounts.util.DCUtil;
import com.facilio.jmx.FacilioQueryCounter;
import com.facilio.jmx.FacilioQueryCounterMBean;
import com.facilio.logging.SysOutLogger;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.queue.FacilioDBQueueExceptionProcessor;
import com.facilio.security.requestvalidator.type.TypeFactory;
import com.facilio.server.ServerInfo;
import com.facilio.serviceportal.actions.PortalAuthInterceptor;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.kafka.notification.NotificationProcessor;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.tasker.FacilioInstantJobScheduler;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.translation.TranslationConf;
import com.facilio.util.FacilioUtil;
import com.facilio.util.ValidatePermissionUtil;
import com.facilio.util.ValueGeneratorUtil;
import com.facilio.v3.util.ChainUtil;
import com.facilio.weather.service.WeatherServiceType;
import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class FacilioContextListener implements ServletContextListener {

	private Timer timer = new Timer();
	private static final Logger LOGGER = LogManager.getLogger(FacilioContextListener.class.getName());
	private static String instanceId = null;

	public void contextDestroyed(ServletContextEvent event) {
		if(RedisManager.getInstance() != null) {
			RedisManager.getInstance().release();// destroying redis connection pool
		}
		FacilioScheduler.stopSchedulers();
		FacilioInstantJobScheduler.stopExecutors();
		timer.cancel();
		FacilioConnectionPool.INSTANCE.close();
	}

	@SneakyThrows
	public void contextInitialized(ServletContextEvent event) {

		try {
			System.setOut(new SysOutLogger("SysOut"));
		} catch (FileNotFoundException e1) {
			LOGGER.info("Exception occurred ", e1);
		}
		try {
			System.setErr(new SysOutLogger("SysErr"));
		} catch (FileNotFoundException e1) {
			LOGGER.info("Exception occurred ", e1);
		}

		try {
			setVersion(event);

			//All these init should be moved to config
			initDBConnectionPool();

			if(RedisManager.getInstance() != null) {
				RedisManager.getInstance().connect(true); // creating redis connection pool
			}
			LRUCache.getRoleNameCachePs();

			timer.schedule(new TransactionMonitor(), 0L, 3000L);

			ValidatePermissionUtil.initialize();
			ValueGeneratorUtil.initialize();
			Operator.getOperator(1);
			registerMBeans();
			TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION,1);
			QAndARuleType type = QAndARuleType.WORKFLOW;
			ActivityType.getActivityType(1);
			FieldUtil.init();
			FacilioEnum.getEnumValues("CostType");
			DCUtil.init();
			TranslationConf.getTranslationImpl("test");
			AddSignupDataCommandV3.initSignUpDataClasses();

			initializeDB();
			if(FacilioProperties.isScheduleServer()) {
				LOGGER.info("Starting FacilioDBQueueExceptionProcessor");
				timer.schedule(new FacilioDBQueueExceptionProcessor(), 0L, 900000L); // 30 minutes
			}

			ServerInfo.registerServer();
			if( !FacilioProperties.isDevelopment() && StringUtils.isNotEmpty(FacilioProperties.getQueueSource())) {
				 new Thread(new NotificationProcessor()).start();
			}
			BeanFactory.initBeans();
			FacilioScheduler.initScheduler();
			FacilioInstantJobScheduler.init();
			ChainUtil.initRESTAPIHandler("com.facilio.apiv3");
			ModuleWidgetsUtil.initWidgetConfigHandler();
			WidgetConfigChain.initWidgetConfigHandler();
			WeatherServiceType.init();
			IdentityClient.init(FacilioProperties.getIdentityServerURL(), FacilioProperties.getRegion());
			BackgroundActivityUtil.init();
			/*HashMap customDomains = getCustomDomains();
			if(customDomains!=null) {
				event.getServletContext().setAttribute("custom domains", customDomains);
				LOGGER.info("Custom domains loaded " + customDomains);
			}*/

			if (!FacilioProperties.isProduction()) {
				downloadEnvironmentFiles();
			}
			if (FacilioProperties.isMessageProcessor()) {
				MessageQueueFactory.start();
			}
			//AgentIntegrationQueueFactory.startIntegrationQueues();

			PortalAuthInterceptor.setPortalDomain(FacilioProperties.getOccupantAppDomain());// event.getServletContext().getInitParameter("SERVICEPORTAL_DOMAIN");
			LOGGER.info("Loading the domain name as ######" + PortalAuthInterceptor.getPortalDomain());
			initLocalHostName();
			initClientAppConfig();
			registerValidatorTypes();
			WmsBroadcaster.getBroadcaster();
			HealthCheckFilter.setStatus(200);
		} catch (Exception e) {
			sendFailureEmail(e);
			LOGGER.info("Shutting down, because of an exception", e);
			throw e;
		}
	}

	private void registerValidatorTypes() {
		TypeFactory.registerType("customfields", CustomFields::new);
		TypeFactory.registerType("v3fields", V3fields::new);
		TypeFactory.registerType("date", Date::new);
		TypeFactory.registerType("date_time", DateTime::new);
		TypeFactory.registerType("formData", FormData::new);
		TypeFactory.registerType("lookup",LookUpDataType::new);

	}

	private void initClientAppConfig() throws Exception {
		ClientAppConfig clientAppConfig = ClientAppUtil.getClientAppConfig(ClientAppBean.DEFAULT_CLIENT_APP);
	}

	private void setVersion(ServletContextEvent event) {
		InputStream versionFile;
		try {
			versionFile = SQLScriptRunner.class.getClassLoader().getResourceAsStream("version.txt");
			Properties prop = new Properties();
			prop.load(versionFile);
			event.getServletContext().setAttribute("buildinfo", prop);
			LOGGER.info("Loaded build properties "+prop);

		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
	}

	private void downloadEnvironmentFiles() throws Exception {
		File secretsDir = new File(FacilioUtil.normalizePath("/tmp/secrets"));
		if (!secretsDir.exists()) {
			if (secretsDir.mkdir()) {
				File file = new File(FacilioUtil.normalizePath("/tmp/secrets/google_app_credentials.json"));
				if (file.exists()) {
					if (file.delete()) {
						try (FileOutputStream outputStream = new FileOutputStream(file); InputStream inputStream = FacilioFactory.getFileStore().getSecretFile("GOOGLE_APP_CREDENTIALS");) {
							int read;
							byte[] bytes = new byte[1024];
							while (inputStream != null && (read = inputStream.read(bytes)) != -1) {
								outputStream.write(bytes, 0, read);
							}
							return;
						} catch (IOException e) {
							LOGGER.info("Error while downloading Google app credentials " + e.getMessage());
						}
					}
				}
			}
		}
		LOGGER.info("Unable write secret file.");
	}

	private void registerMBeans() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = new ObjectName("com.facilio.db:type=Query");
			FacilioQueryCounterMBean mbean = new FacilioQueryCounter();
			mbs.registerMBean(mbean, name);
		} catch (Exception e) {
			LOGGER.info("Exception while registering Facilio MBeans");
		}
	}

	private void initializeDB() {
		if (FacilioProperties.isDevelopment()) {
			Map<String, String> map = new HashMap<>();
			map.put("appDomain",  FacilioProperties.getMainAppDomain());
			map.put("devDomain", FacilioProperties.getDeveloperAppDomain());
			createTables(FacilioUtil.normalizePath("conf/db/" + DBConf.getInstance().getDBName() + "/PublicDB.sql"), map);
			createTables(FacilioUtil.normalizePath("conf/db/" + DBConf.getInstance().getDBName() + "/AppDB.sql"), Collections.singletonMap("defaultAppDB", FacilioProperties.getDefaultAppDB()));
		}
	}

	private void createTables(String fileName, Map<String, String> paramValues) {
		URL url = SQLScriptRunner.class.getClassLoader().getResource(fileName);
		if(url != null) {
			File file = new File(url.getFile());
			SQLScriptRunner scriptRunner = new SQLScriptRunner(file, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
			try {
				scriptRunner.runScript();
			} catch (Exception e) {
				LOGGER.info("Error while executing script " + fileName);
			}
		} else {
			LOGGER.warn("Couldn't find : " + fileName);
		}
	}

	private void sendFailureEmail(Exception e) {
		if(FacilioProperties.isDevelopment()) {
			return;
		}
		JSONObject json = new JSONObject();
		json.put("sender", EmailClient.getFromEmail("error"));
		json.put("to", "error@facilio.com");
		json.put("subject", "Startup Error at " + getInstanceId());
		json.put("message", ExceptionUtils.getStackTrace(e));
		try {
			FacilioFactory.getEmailClient().sendEmail(json);
		} catch (Exception e1) {
			LOGGER.info("Exception while sending email ", e1);
		}

	}

	private void migrateSchemaChanges() {
		LOGGER.info("Flyway migration handler started...");
		try {
			DataSource ds = FacilioConnectionPool.getInstance().getDataSource();
			long startTime = System.currentTimeMillis();
			Flyway flyway = new Flyway();
			flyway.setLocations(FacilioUtil.normalizePath("db/migration/") + DBConf.getInstance().getDBName());
			flyway.setDataSource(ds);
			flyway.setBaselineOnMigrate(true);
			int mig_status = flyway.migrate();

			LOGGER.info("Flyway migration status: " + mig_status + " time taken in ms : " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
	}

	private void initDBConnectionPool() throws Exception {
		LOGGER.info("Initializing DB Connection Pool");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select 1"); //Test Connection

			while(rs.next()) {
				LOGGER.info("testing connection : " + rs.getInt(1));
			}
		} catch(Exception e) {
			LOGGER.error("Exception occurred ", e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, stmt, rs);
		}
	}

	private HashMap getCustomDomains() {
		URL url = this.getClass().getClassLoader().getResource(FacilioUtil.normalizePath("conf/customdomains.xml"));
		if(url != null) {
			File beansxml = new File(url.getFile());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				Document doc = dBuilder.parse(beansxml);
				NodeList nList = doc.getElementsByTagName("domain");

				HashMap customdomains = new HashMap();

				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);

					Element eElement = (Element) nNode;
					String customdomain = eElement.getAttribute("host");
					String orgdomain = eElement.getAttribute("orgdomain");
					customdomains.put(customdomain, orgdomain);
				}
				return customdomains;

			} catch (SAXException | IOException | ParserConfigurationException e) {
				LOGGER.info("Exception occurred ", e);
			}
		} else {
			LOGGER.info("Couldn't find custom domains file.");
		}
		return null;
	}

	private void initLocalHostName() {
		try {
			instanceId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LOGGER.info("Exception occurred ", e);
		}
	}

	public static String getInstanceId(){
		return instanceId;
	}
}