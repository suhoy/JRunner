package scripts;

import java.util.concurrent.TimeUnit;
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

    public ExampleWebScript0(String name, long minPacing, long maxPacing, boolean pacing, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, minPacing, maxPacing, pacing, loggerInfo, loggerEx, influxSet);
    }

    public ExampleWebScript0(Script script) {
        super(script);
    }

    @Override
    public void init() {
    }

    @Override
    public void action() {

        try {
            long sleep=Utils.getRand(2000, 2500);
            addpoint("times", "script", "ex0", "resp", sleep);
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
    public void addpoint(String metric, String tagName, String tag, String filedName, long filedValue) {
        try {
            Point influxPoint = Point.measurement(metric)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag(tagName, tag)
                    .tag("user", this.id)
                    .addField(filedName, filedValue)
                    .addField("count", 1)
                    .build();
            batchPoints.point(influxPoint);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
