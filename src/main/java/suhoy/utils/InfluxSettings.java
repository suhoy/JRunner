package suhoy.utils;

import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.InfluxDB;

/**
 *
 * @author suh1995
 */
public class InfluxSettings {

    public String endpoint;
    public String database;
    public String retention;

    public String user;
    public String pass;
    public int batch;

    public InfluxSettings(String endpoint, String database, String retention, String user, String pass, int batch) {
        this.endpoint = endpoint;
        this.database = database;
        this.retention = retention;
        this.user = user;
        this.pass = pass;
        this.batch = batch;
    }

}
