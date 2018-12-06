package com.facilio.fw.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.cache.RedisManager;
import com.facilio.filters.HealthCheckFilter;
import com.facilio.fw.BeanFactory;
import com.facilio.kinesis.KinesisProcessor;
import com.facilio.logging.SysOutLogger;
import com.facilio.queue.FacilioExceptionProcessor;
import com.facilio.server.ServerInfo;
import com.facilio.serviceportal.actions.PortalAuthInterceptor;
import com.facilio.sql.DBUtil;
import com.facilio.sql.SQLScriptRunner;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.tasker.executor.InstantJobExecutor;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.transaction.TransactionMonitor;
import com.facilio.wms.message.NotificationProcessor;
import com.facilio.wms.message.NotificationProcessorFactory;

public class FacilioContextListener implements ServletContextListener {

	private Timer timer = new Timer();
	private static final Logger log = LogManager.getLogger(FacilioContextListener.class.getName());
	private static String instanceId = null;

	public void contextDestroyed(ServletContextEvent event) {
		if(RedisManager.getInstance() != null) {
			RedisManager.getInstance().release();// destroying redis connection pool
		}
		FacilioScheduler.stopSchedulers();
		InstantJobExecutor.INSTANCE.stopExecutor();
	}

	public void contextInitialized(ServletContextEvent event) {

		String schedulerProp = AwsUtil.getConfig("schedulerServer");
		boolean scheduler = false;
		if(schedulerProp != null) {
			scheduler = Boolean.parseBoolean(schedulerProp.trim());
		}

		try {
			System.setOut(new SysOutLogger("SysOut"));
		} catch (FileNotFoundException e1) {
			log.info("Exception occurred ", e1);
		}
		try {
			System.setErr(new SysOutLogger("SysErr"));
		} catch (FileNotFoundException e1) {
			log.info("Exception occurred ", e1);
		}

		if("true".equals(AwsUtil.getConfig("enable.transaction"))) {
			timer.schedule(new TransactionMonitor(), 0L, 3000L);
		}

		if(scheduler && AwsUtil.isProduction()) {
			timer.schedule(new FacilioExceptionProcessor(), 0L, 900000L); // 30 minutes
		}


		initDBConnectionPool();
		Operator.OPERATOR_MAP.get(1);
		try {
			try {
				migrateSchemaChanges();
			} catch(Exception e) {
				log.info("Exception occurred ", e);
			}
			ServerInfo.registerServer();
			//timer.schedule(new ServerInfo(), 30000L, 30000L);
			String environment = AwsUtil.getConfig("environment");


			//handle if server is both user and scheduler.
			if( "stage".equalsIgnoreCase(environment)){
				new Thread(new com.facilio.kafka.notification.NotificationProcessor()).start();
			} else if((AwsUtil.isProduction() && ( ! scheduler))) {
				new Thread(() -> NotificationProcessor.run(new NotificationProcessorFactory())).start();
			}

			BeanFactory.initBeans();
			
			FacilioScheduler.initScheduler();
			InstantJobExecutor.INSTANCE.startExecutor();

			if(RedisManager.getInstance() != null) {
				RedisManager.getInstance().connect(); // creating redis connection pool
			}

			HashMap customDomains = getCustomDomains();
			if(customDomains!=null) {
				event.getServletContext().setAttribute("custom domains", customDomains);
				log.info("Custom domains loaded " + customDomains);
			}
			
			if(AwsUtil.isDevelopment() || AwsUtil.disableCSP()) {
				initializeDB();
			}

			try {
				if(("true".equalsIgnoreCase(AwsUtil.getConfig("enable.kinesis"))) && "true".equalsIgnoreCase(AwsUtil.getConfig("kinesisServer"))) {
					new Thread(KinesisProcessor::startKinesis).start();
				}
			} catch (Exception e){
				log.info("Exception occurred ", e);
			}
			InputStream versionFile;
			try {
				versionFile = SQLScriptRunner.class.getClassLoader().getResourceAsStream("version.txt");
				Properties prop = new Properties();
				prop.load(versionFile);
				event.getServletContext().setAttribute("buildinfo", prop);
				log.info("Loaded build properties "+prop);

			} catch (Exception e) {
				log.info("Exception occurred ", e);
			}

			PortalAuthInterceptor.setPortalDomain(com.facilio.aws.util.AwsUtil.getConfig("portal.domain"));// event.getServletContext().getInitParameter("SERVICEPORTAL_DOMAIN");
			log.info("Loading the domain name as ######"+PortalAuthInterceptor.getPortalDomain());
			initLocalHostName();
			HealthCheckFilter.setStatus(200);
			
		} catch (Exception e) {
			sendFailureEmail(e);
			log.info("Exception occurred ", e);
		}
		
	}

	private void initializeDB() {
		createTables("conf/leedconsole.sql");
		createTables("conf/eventconsole.sql");
	}

	private void createTables(String fileName) {
		URL url = SQLScriptRunner.class.getClassLoader().getResource(fileName);
		if(url != null) {
			File file = new File(url.getFile());
			SQLScriptRunner scriptRunner = new SQLScriptRunner(file, true, null);
			try {
				scriptRunner.runScript();
			} catch (Exception e) {
				log.info("Error while executing script " + fileName);
			}
		} else {
			log.warn("Couldn't find : " + fileName);
		}
	}

	private void sendFailureEmail(Exception e) {
		if(AwsUtil.isDevelopment()) {
			return;
		}
		JSONObject json = new JSONObject();
		json.put("sender", "error@facilio.com");
		json.put("to", "error@facilio.com");
		json.put("subject", "Startup Error at " + getInstanceId());
		json.put("message", e.getMessage());
		try {
			AwsUtil.sendEmail(json);
		} catch (Exception e1) {
			log.info("Exception while sending email ", e1);
		}

	}
	
	private void migrateSchemaChanges() {
		System.out.println("Flyway migration handler started...");
		
		DataSource ds = FacilioConnectionPool.getInstance().getDataSource();
		long startTime = System.currentTimeMillis();
		Flyway flyway = new Flyway();
		flyway.setDataSource(ds);
		flyway.setBaselineOnMigrate(true);
		int mig_status = flyway.migrate();
		
		System.out.println("Flyway migration status: "+mig_status +" time taken in ms : " + (System.currentTimeMillis() - startTime));
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
				System.out.println(rs.getInt(1));
			}
		}
		catch(SQLException e) {
			log.info("Exception occurred ", e);
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
				log.info("Exception occurred ", e);
			}
		} else {
			log.info("Couldn't find custom domains file.");
		}
		return null;
	}

	private void initLocalHostName() {
		try {
			instanceId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.info("Exception occurred ", e);
		}
	}

	public static String getInstanceId(){
		return instanceId;
	}
}