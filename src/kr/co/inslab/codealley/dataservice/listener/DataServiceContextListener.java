package kr.co.inslab.codealley.dataservice.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import kr.co.inslab.codealley.dataservice.config.Config;
import kr.co.inslab.codealley.dataservice.log.SLog;



/**
 * Application Lifecycle Listener implementation class DataServiceContextListener
 *
 */
@WebListener
public class DataServiceContextListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public DataServiceContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event)  { 
    	SLog.i("-------------------- contextInitialized --------------------");
    	if(Config.instance == null)
			Config.getInstance().init(event.getServletContext());
    }
	
}
