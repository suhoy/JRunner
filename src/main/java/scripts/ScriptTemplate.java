package scripts;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public class ScriptTemplate extends Script {

    private String someStringData = "example";
    private int someIntData = 1000;

    public ScriptTemplate(String name, long minPacing, long maxPacing, boolean pacing, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, minPacing, maxPacing, pacing, loggerInfo, loggerEx, influxSet);
    }

    public ScriptTemplate() {
    }


    @Override
    public void init() {
        try {
            //do something once, open connection, open file, etc
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void action() {
        try {
            //write logic that should be done in loop
            long timeStart = System.currentTimeMillis();
            loggerInfo.info("something happend");
            long timeFinish = System.currentTimeMillis();
            
            //send metric to influx
            addpoint(timeStart, "times", "script", "ex0", "resp", (timeFinish - timeStart), "true");
            
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void end() {
        try {
            //do something once, close connection, close file, etc
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void addpoint(long startTime, String metric,
            String tagName, String tag,
            String filedName, long filedValue, String status
    ) {
        try {
            Point influxPoint = Point.measurement(metric)
                    .time(startTime, TimeUnit.MILLISECONDS)
                    .tag(tagName, tag)
                    .tag("user", this.id)
                    .tag("status", status)
                    .addField(filedName, filedValue)
                    .addField("count", 1)
                    .build();
            batchPoints.point(influxPoint);
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

}
