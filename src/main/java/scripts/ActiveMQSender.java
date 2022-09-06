package scripts;

import java.util.concurrent.TimeUnit;
import javax.jms.MessageProducer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public class ActiveMQSender extends Script {

    private String connectionURL = "tcp://123.123.123.123:61616";
    private String requestQueue = "request";
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;
    

    public ActiveMQSender(String name, long minPacing, long maxPacing, boolean pacing, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, minPacing, maxPacing, pacing, loggerInfo, loggerEx, influxSet);
    }

    public ActiveMQSender() {
    }

    @Override
    public void init() {
        try {
            this.connectionFactory = new ActiveMQConnectionFactory(connectionURL);
            this.connection = connectionFactory.createConnection();
            this.connection.start();
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.destination = session.createQueue(requestQueue);
            this.producer = session.createProducer(destination);
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void action() {
        try {
            String message = "hello";
            TextMessage textmessage = session.createTextMessage(message);
            this.producer.send(textmessage);
            loggerInfo.info("Sent - Message: " + message + ", Queue: " + requestQueue + ", URL: " + connectionURL);
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void end() {
        try {
            this.producer.close();
            this.session.close();
            this.connection.close();
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
