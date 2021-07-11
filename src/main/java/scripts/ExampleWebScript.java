package scripts;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import junit.framework.Assert;
import org.apache.logging.log4j.Logger;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public class ExampleWebScript extends Script {

    public ExampleWebScript(String name, long minPacing, long maxPacing, boolean pacing, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, minPacing, maxPacing, pacing, loggerInfo, loggerEx, influxSet);
    }

    public ExampleWebScript(Script script) {
        super(script);
    }

    @Override
    public void init() {
    }

    @Override
    public void action() {

        try {
            long sleep = 3000;
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
}
