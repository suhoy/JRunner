package scripts;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;
import suhoy.utils.Utils;

/**
 *
 * @author suh1995
 */
public class ExampleWebScript0 extends Script {

    public ExampleWebScript0(String name, long counterV, long minPacing, long maxPacing, boolean pacing, boolean counterB, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, counterV, minPacing, maxPacing, pacing, counterB, loggerInfo, loggerEx, influxSet);
    }

    public ExampleWebScript0(Script script) {
        super(script);
    }

    public ExampleWebScript0() {
    }

    Iterable<CSVRecord> records = null;
    ArrayList<CSVRecord> param = new ArrayList<>();

    @Override
    public void init() {
        //читаем файл
        try (Reader in = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("pools/example.csv"))) {
            this.records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);

            for (CSVRecord record : records) {
                this.param.add(record);
            }
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void action() {
        /*
        берём случайное значение
        CSVRecord record = Utils.getRandomCSV(param);
        System.out.println(record.get("login"));
        System.out.println(record.get("pass"));
        System.out.println(record.get("name"));*/
        try {

            long timeStart = System.currentTimeMillis();
            long sleep = Utils.getRand(2000, 2500);
            long timeFinish = System.currentTimeMillis();

            addpoint(timeStart, "times", "script", "ex0", "resp", (timeFinish - timeStart), "true");
            Thread.sleep(sleep);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /*
        try (final WebClient webClient = new WebClient()) {

            //включили поддержку js
            webClient.getOptions().setJavaScriptEnabled(true);
            //включили поддержку css
            webClient.getOptions().setCssEnabled(true);
            //сделали css тихим
            webClient.setCssErrorHandler(new SilentCssErrorHandler());

            //long s = System.currentTimeMillis();
            HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
            //System.out.println(page.getTitleText());

            //проверка что title = "HtmlUnit – Welcome to HtmlUnit"
            //Assert.assertEquals("HtmlUnit – Welcome to HtmlUnit", page.getTitleText());
            //long f = System.currentTimeMillis();
            //System.out.println("f-s=" + (f - s));
            final String pageAsXml = page.asXml();
            Assert.assertTrue(pageAsXml.contains("<body class=\"topBarDisabled\">"));

            final String pageAsText = page.asText();
            Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }

    @Override
    public void end() {
    }

    @Override
    public void addpoint(long startTime, String metric, String tagName, String tag, String filedName, long filedValue, String status) {
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
