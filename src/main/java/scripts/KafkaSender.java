package scripts;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public class KafkaSender extends Script {

    private String servers = "host-01:9094,host-02:9094,host-03:9094";
    private String topic = "topic";
    private String user = "user";
    private String password = "pass";

    private String autoCommit = "true";
    private int intervalCommit = 1000;
    private String offset = "earliest";
    private int sessionTimeOut = 30000;

    Producer<Long, String> producer;

    public KafkaSender(String name, long counterV, long minPacing, long maxPacing, boolean pacing, boolean counterB, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, counterV, minPacing, maxPacing, pacing, counterB, loggerInfo, loggerEx, influxSet);
    }

    public KafkaSender() {
    }

    private Producer<Long, String> ProducerCreator() {
        Properties props = new Properties();

        //props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put("bootstrap.servers", servers);
        //props.put(ProducerConfig.CLIENT_ID_CONFIG, "producer" + this.id);
        props.put("group.id", "producer" + this.id);

        props.put("enable.auto.commit", autoCommit);
        props.put("auto.commit.interval.ms", intervalCommit);
        props.put("auto.offset.reset", offset);
        props.put("session.timeout.ms", sessionTimeOut);

        //props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        //props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        String serializer = StringSerializer.class.getName();
        String deserializer = StringDeserializer.class.getName();
        props.put("key.deserializer", deserializer);
        props.put("value.deserializer", deserializer);
        props.put("key.serializer", serializer);
        props.put("value.serializer", serializer);

        //auth
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + this.user + "\" password=\"" + this.password + "\";");
        return new KafkaProducer<>(props);
    }

    @Override
    public void init() {
        try {
            this.producer = ProducerCreator();
            loggerInfo.info("producer created");
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void action() {
        try {
            String message = "hello";
            ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(topic, message);
            RecordMetadata metadata = this.producer.send(record).get();
            loggerInfo.info("Sent - Message: " + message + ", Topic: " + topic + ", Partition: " + metadata.partition() + ", Offset: " + metadata.offset());
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void end() {
        try {
            this.producer.flush();
            this.producer.close();
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
