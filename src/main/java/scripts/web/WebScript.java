package scripts.web;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import junit.framework.Assert;
import templates.Script;

/**
 *
 * @author suh1995
 */
public class WebScript extends Script {

    public WebScript(String name, int user, long minPacing, long maxPacing, boolean pacing) {
        super(name, user, minPacing, maxPacing, pacing);
    }

        
    
    public void init() {
    }

    public void action() {
        try (final WebClient webClient = new WebClient()) {

            //включили поддержку js
            webClient.getOptions().setJavaScriptEnabled(true);
            //включили поддержку css
            webClient.getOptions().setCssEnabled(true);
            //сделали css тихим
            webClient.setCssErrorHandler(new SilentCssErrorHandler());

            long s = System.currentTimeMillis();
            HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");
            System.out.println(page.getTitleText());

            //проверка что title = "HtmlUnit – Welcome to HtmlUnit"
            Assert.assertEquals("HtmlUnit – Welcome to HtmlUnit", page.getTitleText());
            long f = System.currentTimeMillis();

            System.out.println("f-s=" + (f - s));

            final String pageAsXml = page.asXml();
            Assert.assertTrue(pageAsXml.contains("<body class=\"topBarDisabled\">"));

            final String pageAsText = page.asText();
            Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void end() {
    }
}
