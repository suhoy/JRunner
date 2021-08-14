package sripts;

import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import scripts.Correlation_Challenge_Mod;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public class TestRunSomeCode {
    
    InfluxSettings influxSet;
    Logger logger = LogManager.getLogger("lr_debug");
    Properties properties = new Properties();
    long minPacing = 2_000;
    long maxPacing = 3_000;
    boolean pacing = true;

    //http://loadrunnertips.com/LoadRunner_Correlation_Challenge_Exp.aspx
    //http://loadrunnertips.com/LoadRunner_Correlation_Challenge_Mod.aspx
    @Before
    public void setup() {
        try {
            System.out.println("before works");
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            //read influx
            influxSet = new InfluxSettings(
                    properties.getProperty("influx.endpoint"),
                    properties.getProperty("influx.database"),
                    properties.getProperty("influx.retention"),
                    properties.getProperty("influx.user"),
                    properties.getProperty("influx.pass"),
                    Integer.parseInt(properties.getProperty("influx.batch")));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
    
    @Test
    public void run() {
        /*String name = "Correlation_Challenge_Mod";
        Correlation_Challenge_Mod ex = new Correlation_Challenge_Mod(name, minPacing, maxPacing, pacing, logger, logger, influxSet);
        ex.initvars();
        ex.init();
        ex.action();
        ex.end();*/
    }
    
}
