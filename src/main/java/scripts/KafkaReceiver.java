package scripts;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public class KafkaReceiver extends Script {

    private String servers = "host-01:9094,host-02:9094,host-03:9094";
    private String topic = "topic";
    private String user = "user";
    private String password = "pass";

    // 1000L is the time in milliseconds consumer will wait if no record is found at broker.
    private long pollTime = 1000L;
    private int maxPoll = 1;
    private String offset = "earliest";
    private String autoCommit = "true";

    Consumer<Long, String> consumer;

    public KafkaReceiver(String name, long counterV, long minPacing, long maxPacing, boolean pacing, boolean counterB, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, counterV, minPacing, maxPacing, pacing, counterB, loggerInfo, loggerEx, influxSet);
    }

    public KafkaReceiver() {
    }

    private Consumer<Long, String> ConsumerCreator() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.id);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPoll);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset);

        //auth
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + this.user + "\" password=\"" + this.password + "\";");

        Consumer<Long, String> consumer = new KafkaConsumer<>(props);

        //Collections.singletonList(topic)
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }

    @Override
    public void init() {
        try {
            this.consumer = ConsumerCreator();
            loggerInfo.info("consumer created");
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void action() {
        try {
            ConsumerRecords<Long, String> consumerRecords = consumer.poll(pollTime);
            if (consumerRecords.count() > 0) {
                //Print each record. 
                consumerRecords.forEach(record -> {
                    loggerInfo.info("Received - Key: " + record.key() + ", Value: " + record.value() + ", Partition: " + record.partition() + ", Offset: " + record.offset());
                });
            }
            // Commits the offset of record to broker. 
            consumer.commitAsync();
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void end() {
        try {
            this.consumer.close();
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
