package com.facilio.fw.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import org.flywaydb.core.Flyway;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.cache.RedisManager;
import com.facilio.fw.BeanFactory;
import com.facilio.kinesis.KinesisProcessor;
import com.facilio.logging.SysOutLogger;
import com.facilio.serviceportal.actions.PortalAuthInterceptor;
import com.facilio.sql.DBUtil;
import com.facilio.sql.SQLScriptRunner;
import com.facilio.tasker.FacilioScheduler;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.transaction.TransactionMonitor;

public class FacilioContextListener implements ServletContextListener {

	private Timer timer = new Timer();

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
//		System.out.println("Listener Destroyed");
		RedisManager.getInstance().release(); // destroying redis connection pool
		FacilioScheduler.stopSchedulers();
	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			System.setOut(new SysOutLogger("SysOut"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.setErr(new SysOutLogger("SysErr"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if("true".equals(AwsUtil.getConfig("enable.transaction")) && false) {
			timer.schedule(new TransactionMonitor(), 0L, 3000L);
		}


		// TODO Auto-generated method stub
		initDBConnectionPool();
		Operator test = Operator.OPERATOR_MAP.get(1);
		try {
			try {
			migrateSchemaChanges();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			BeanFactory.initBeans();
			
			if(Boolean.parseBoolean(AwsUtil.getConfig("schedulerServer"))) {
				FacilioScheduler.initScheduler();
			}
		//	FacilioTransactionManager.INSTANCE.getTransactionManager();
			
			RedisManager.getInstance().connect(); // creating redis connection pool
			HashMap customdomains = getCustomDomains();
			
			if(customdomains!=null)
			{
				event.getServletContext().setAttribute("customdomains", customdomains);
				System.out.println("Custom domains loaded" + customdomains);
			}
			
			File file = new File(SQLScriptRunner.class.getClassLoader().getResource("conf/leedconsole.sql").getFile());
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
				
			}
			
			try {
				if(("true".equalsIgnoreCase(AwsUtil.getConfig("enable.kinesis"))) && "true".equalsIgnoreCase(AwsUtil.getConfig("kinesisServer"))) {
					new Thread(KinesisProcessor::startProcessor).start();
				}
			} catch (Exception e){
				e.printStackTrace();
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
				e.printStackTrace();
			}

PortalAuthInterceptor.PORTALDOMAIN = com.facilio.aws.util.AwsUtil.getConfig("portal.domain");// event.getServletContext().getInitParameter("SERVICEPORTAL_DOMAIN");
			System.out.println("Loading the domain name as ######"+PortalAuthInterceptor.PORTALDOMAIN );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			e.printStackTrace();
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

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

}
