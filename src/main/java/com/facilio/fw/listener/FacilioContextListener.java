package com.facilio.fw.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
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

import com.facilio.wms.message.NotificationProcessor;
import com.facilio.wms.message.NotificationProcessorFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
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
import com.facilio.queue.InstantJobExecutor;
import com.facilio.server.ServerInfo;
import com.facilio.serviceportal.actions.PortalAuthInterceptor;
import com.facilio.sql.DBUtil;
import com.facilio.sql.SQLScriptRunner;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.transaction.TransactionMonitor;

public class FacilioContextListener implements ServletContextListener {

	private Timer timer = new Timer();
	private static Logger log = LogManager.getLogger(FacilioContextListener.class.getName());

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
//		System.out.println("Listener Destroyed");
		if(RedisManager.getInstance() != null) {
			RedisManager.getInstance().release();// destroying redis connection pool
		}
		FacilioScheduler.stopSchedulers();
		InstantJobExecutor.INSTANCE.stopExecutor();
	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			System.setOut(new SysOutLogger("SysOut"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e1);
		}
		try {
			System.setErr(new SysOutLogger("SysErr"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e1);
		}
		if("true".equals(AwsUtil.getConfig("enable.transaction"))) {
			timer.schedule(new TransactionMonitor(), 0L, 3000L);
		}

		if("true".equals(AwsUtil.getConfig("schedulerServer")) && "production".equals(AwsUtil.getConfig("environment"))) {
			timer.schedule(new FacilioExceptionProcessor(), 0L, 900000L); // 30 minutes
		}


		// TODO Auto-generated method stub
		initDBConnectionPool();
		Operator test = Operator.OPERATOR_MAP.get(1);
		try {
			try {
			migrateSchemaChanges();
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
			}
			ServerInfo.registerServer();
			//timer.schedule(new ServerInfo(), 30000L, 30000L);
			String environment = AwsUtil.getConfig("environment");
			String schedulerProp = AwsUtil.getConfig("schedulerServer");
			boolean scheduler = false;
			if(schedulerProp != null) {
			 scheduler = Boolean.parseBoolean(schedulerProp.trim());
			}

			//handle if server is both user and scheduler.
			if( ("stage".equalsIgnoreCase(environment) || "production".equalsIgnoreCase(environment)) && ( ! scheduler)) {
				new Thread(() -> NotificationProcessor.run(new NotificationProcessorFactory())).start();
			}

			BeanFactory.initBeans();
			
			FacilioScheduler.initScheduler();
			InstantJobExecutor.INSTANCE.startExecutor();
		//	FacilioTransactionManager.INSTANCE.getTransactionManager();
			if(RedisManager.getInstance() != null) {
				RedisManager.getInstance().connect(); // creating redis connection pool
			}
			HashMap customdomains = getCustomDomains();
			
			if(customdomains!=null)
			{
				event.getServletContext().setAttribute("customdomains", customdomains);
				System.out.println("Custom domains loaded" + customdomains);
			}
			
			/*File file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/leedconsole.sql").getFile());
			SQLScriptRunner scriptRunner = new SQLScriptRunner(file, true, null);
			//Connection c = FacilioConnectionPool.getInstance().getConnection();
			try
			{
			scriptRunner.runScript();
			}
			catch(Exception e)
			{
				
			}
			finally
			{
				
			}
			file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/eventconsole.sql").getFile());
			scriptRunner = new SQLScriptRunner(file, true, null);
		//	c = FacilioConnectionPool.getInstance().getConnection();
			try
			{
			scriptRunner.runScript();
			}
			catch(Exception e)
			{
				
			}*/

			try {
				if(("true".equalsIgnoreCase(AwsUtil.getConfig("enable.kinesis"))) && "true".equalsIgnoreCase(AwsUtil.getConfig("kinesisServer"))) {
					new Thread(KinesisProcessor::startProcessor).start();
				}
			} catch (Exception e){
				log.info("Exception occurred ", e);
			}
			InputStream versionfile;
			try {
				versionfile = SQLScriptRunner.class.getClassLoader().getResourceAsStream("version.txt");
				//String version = FileUtils.readFileToString(versionfile);
				Properties prop = new Properties();
				prop.load(versionfile);
				event.getServletContext().setAttribute("buildinfo", prop);
				
				System.out.println("Loaded build properties "+prop);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}

PortalAuthInterceptor.PORTALDOMAIN = com.facilio.aws.util.AwsUtil.getConfig("portal.domain");// event.getServletContext().getInitParameter("SERVICEPORTAL_DOMAIN");
			System.out.println("Loading the domain name as ######"+PortalAuthInterceptor.PORTALDOMAIN );
			
			initLocalHostName();
			HealthCheckFilter.setStatus(200);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
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
	
	/*private void createTables() throws SQLException, IOException {
		File file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/createTables.sql").getFile());
		SQLScriptRunner scriptRunner = new SQLScriptRunner(file, true, false);
		scriptRunner.runScript();
	}*/
	
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
	
	private HashMap getCustomDomains()
	{
		File beansxml = new File(this.getClass().getClassLoader().getResource("conf/customdomains.xml").getFile());
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
				customdomains.put(customdomain,orgdomain);
			}
			return customdomains;

		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		return null;
	}
	public static String INSTANCEID = null;

	private void initLocalHostName() {
	
		try {
			INSTANCEID = InetAddress.getLocalHost().getHostName();

		} catch (UnknownHostException e) {
			log.info("Exception occurred ", e);
		}
	}

}
