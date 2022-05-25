package suhoy.obj;

import org.apache.logging.log4j.Logger;
import suhoy.utils.Utils;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.InfluxDB;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public abstract class Script implements Runnable {

    public String id = "Not ready";
    protected String name;
    protected long minPacing;
    protected long maxPacing;
    protected int user;
    protected boolean run = false;
    protected boolean pacing = false;
    protected Logger loggerInfo;
    protected Logger loggerEx;
    protected InfluxDB influxDB = null;
    protected BatchPoints batchPoints = null;
    protected int batch = 10;
    protected InfluxSettings influxSet;
    
    protected long start;
    protected long finish;

    public Script(String name, long minPacing, long maxPacing, boolean pacing, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        this.name = name;
        this.minPacing = minPacing;
        this.maxPacing = maxPacing;
        this.pacing = pacing;
        this.loggerInfo = loggerInfo;
        this.loggerEx = loggerEx;
        this.influxSet = influxSet;
    }

    protected Script(Script script) {
        this.name = script.name;
        this.minPacing = script.minPacing;
        this.maxPacing = script.maxPacing;
        this.pacing = script.pacing;
        this.loggerInfo = script.loggerInfo;
        this.loggerEx = script.loggerEx;
        this.influxSet = script.influxSet;
    }
    public Script(){}

    @Override
    final public void run() {
        try {
            initvars();
            init();
            loggerInfo.info(id + ": thread started");
            while (this.run) {
                long start = System.currentTimeMillis();
                action();
                writepoints();
                long finish = System.currentTimeMillis();
                long duration = finish - start;
                if (this.pacing) {
                    long p = Utils.getPacing(this.minPacing, this.maxPacing) - duration;
                    if (p > 0) {
                        loggerInfo.trace(id + ": duration = " + duration + "ms, and i am sleep for " + p + " ms");
                        Thread.sleep(p);
                    }
                }
            }
            end();
            lastwrite();
            loggerInfo.info(id + ": thread stopped");
        } catch (Exception ex) {
            loggerEx.error(id + ex.getMessage(), ex);
        }
    }

    abstract protected void init();

    abstract protected void action();

    abstract protected void end();

    abstract protected void addpoint(String metric, String tagName, String tag, String filedName, long filedValue, String status);

    public final void stop() {
        this.run = false;
    }

    public final boolean running() {
        return this.run;
    }

    public void initvars() {
        this.run = true;
        this.id = this.name + "-" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId();

        this.influxDB = InfluxDBFactory.connect(this.influxSet.endpoint, this.influxSet.user, this.influxSet.pass);
        this.batchPoints = BatchPoints.database(this.influxSet.database).retentionPolicy(this.influxSet.retention).build();
        this.batch = this.influxSet.batch;
        this.influxDB.enableBatch();

    }

    private void writepoints() {
        if (this.batchPoints.getPoints().size() >= this.batch) {
            this.influxDB.write(this.batchPoints);
            this.batchPoints = BatchPoints.database(this.influxSet.database).retentionPolicy(this.influxSet.retention).build();
        }
    }

    private void lastwrite() {
        this.influxDB.write(batchPoints);
        this.influxDB.flush();
        this.influxDB.close();
    }

}
