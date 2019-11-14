package com.facilio.fw.listener;

import java.io.File;
import java.io.FileNotFoundException;
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

import com.facilio.agentIntegration.AgentIntegrationQueue.AgentIntegrationQueue;
import com.facilio.agentIntegration.AgentIntegrationQueue.AgentIntegrationQueueFactory;
import com.facilio.services.kafka.notification.NotificationProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.facilio.activity.ActivityType;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.cache.RedisManager;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.TransactionMonitor;
import com.facilio.db.util.DBConf;
import com.facilio.db.util.SQLScriptRunner;
import com.facilio.filters.HealthCheckFilter;
import com.facilio.fw.BeanFactory;
import com.facilio.jmx.FacilioQueryCounter;
import com.facilio.jmx.FacilioQueryCounterMBean;
import com.facilio.logging.SysOutLogger;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.queue.FacilioDBQueueExceptionProcessor;
import com.facilio.queue.FacilioExceptionProcessor;
import com.facilio.service.FacilioService;
import com.facilio.serviceportal.actions.PortalAuthInterceptor;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.executor.FacilioInstantJobExecutor;
import com.facilio.tasker.executor.InstantJobExecutor;

public class FacilioContextListener implements ServletContextListener {

	private Timer timer = new Timer();
	private static final Logger LOGGER = LogManager.getLogger(FacilioContextListener.class.getName());
	private static String instanceId = null;

	public void contextDestroyed(ServletContextEvent event) {
		if(RedisManager.getInstance() != null) {
			RedisManager.getInstance().release();// destroying redis connection pool
		}
		FacilioScheduler.stopSchedulers();
		if(FacilioProperties.isProduction()) {
			InstantJobExecutor.INSTANCE.stopExecutor();
		}else {
			FacilioInstantJobExecutor.INSTANCE.stopExecutor();
		}
		
		FacilioService.shutDown();
		timer.cancel();
		FacilioConnectionPool.INSTANCE.close();
	}

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

		timer.schedule(new TransactionMonitor(), 0L, 3000L);
		
		if(FacilioProperties.isScheduleServer() && FacilioProperties.isProduction()) {
			timer.schedule(new FacilioExceptionProcessor(), 0L, 900000L); // 30 minutes
		}
		if(!FacilioProperties.isProduction()) {
			LOGGER.info("##Facilio exception queue Pull method calling");
			timer.schedule(new FacilioDBQueueExceptionProcessor(), 0L, 900000L); // 30 minutes
		}

		initDBConnectionPool();
		Operator.getOperator(1);
		registerMBeans();
		TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION,1);
		ActivityType.getActivityType(1);
		FieldUtil.inti();
		FacilioEnum.getEnumValues("CostType");

		try {
//			migrateSchemaChanges();
			// ServerInfo.registerServer();
			//timer.schedule(new ServerInfo(), 30000L, 30000L);
			initializeDB();
			if( !FacilioProperties.isDevelopment()) {
				new Thread(new NotificationProcessor()).start();
			}

			BeanFactory.initBeans();
			
			FacilioScheduler.initScheduler();
			if(FacilioProperties.isProduction()) {
				InstantJobExecutor.INSTANCE.startExecutor();
			}else {
				FacilioInstantJobExecutor.INSTANCE.startExecutor();
			}
			

			if(RedisManager.getInstance() != null) {
				RedisManager.getInstance().connect(); // creating redis connection pool
			}

			HashMap customDomains = getCustomDomains();
			if(customDomains!=null) {
				event.getServletContext().setAttribute("custom domains", customDomains);
				LOGGER.info("Custom domains loaded " + customDomains);
			}
			
			/*if(AwsUtil.isDevelopment() || AwsUtil.disableCSP()) {
				initializeDB();
			}*/

			try {
				if (FacilioProperties.isMessageProcessor()) {
					FacilioFactory.getMessageQueue().start();
				}
				AgentIntegrationQueueFactory.startIntegrationQueues();
			} catch (Exception e){
				LOGGER.info("Exception occurred ", e);
			}
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

			PortalAuthInterceptor.setPortalDomain(FacilioProperties.getConfig("portal.domain"));// event.getServletContext().getInitParameter("SERVICEPORTAL_DOMAIN");
			LOGGER.info("Loading the domain name as ######"+PortalAuthInterceptor.getPortalDomain());
			initLocalHostName();
			HealthCheckFilter.setStatus(200);
			
		} catch (Exception e) {
			sendFailureEmail(e);
			LOGGER.info("Exception occurred ", e);
		}
		
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
			createTables("conf/db/" + DBConf.getInstance().getDBName() + "/PublicDB.sql", null);
			createTables("conf/db/" + DBConf.getInstance().getDBName() + "/AppDB.sql", Collections.singletonMap("defaultAppDB", FacilioProperties.getDefaultAppDB()));
		}
//		createTables("conf/leedconsole.sql");
		//createTables("conf/db/" + DBConf.getInstance().getDBName() + "/eventconsole.sql");
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
		json.put("sender", "error@facilio.com");
		json.put("to", "error@facilio.com");
		json.put("subject", "Startup Error at " + getInstanceId());
		json.put("message", e.getMessage());
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
			flyway.setLocations("db/migration/" + DBConf.getInstance().getDBName());
			flyway.setDataSource(ds);
			flyway.setBaselineOnMigrate(true);
			int mig_status = flyway.migrate();

			LOGGER.info("Flyway migration status: " + mig_status + " time taken in ms : " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			LOGGER.info("Exception occurred ", e);
		}
	}
	
	private void initDBConnectionPool() {
		System.out.println("Initializing DB Connection Pool");
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
			LOGGER.info("Exception occurred ", e);
		}
		finally {
			DBUtil.closeAll(conn, stmt, rs);
		}
	}
	
	private HashMap getCustomDomains() {
		URL url = this.getClass().getClassLoader().getResource("conf/customdomains.xml");
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