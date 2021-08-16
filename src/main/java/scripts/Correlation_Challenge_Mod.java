package scripts;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import suhoy.obj.Script;
import suhoy.utils.InfluxSettings;

/**
 *
 * @author suh1995
 */
public class Correlation_Challenge_Mod extends Script {

    public Correlation_Challenge_Mod(String name, long minPacing, long maxPacing, boolean pacing, Logger loggerInfo, Logger loggerEx, InfluxSettings influxSet) {
        super(name, minPacing, maxPacing, pacing, loggerInfo, loggerEx, influxSet);
    }

    public Correlation_Challenge_Mod(Script script) {
        super(script);
    }

    @Override
    public void init() {
    }

    @Override
    public void action() {
        try (final WebClient webClient = new WebClient()) {
            start = System.currentTimeMillis();

            //выключили поддержку js
            webClient.getOptions().setJavaScriptEnabled(false);
            //включили поддержку css
            webClient.getOptions().setCssEnabled(true);
            //сделали варнинги css тихими
            webClient.setCssErrorHandler(new SilentCssErrorHandler());

            //начальная страница
            HtmlPage pageStart = webClient.getPage("http://loadrunnertips.com/LoadRunner_Correlation_Challenge_Mod.aspx");
            loggerInfo.trace("step1 \t body=" + pageStart.getBody().asNormalizedText());

            //прожимаем кнопку старт
            HtmlForm formStart = pageStart.getHtmlElementById("form1");
            HtmlSubmitInput buttonStart = formStart.getInputByName("ctl00$head$btnStart");
            HtmlPage pageNumbers = buttonStart.click();

            //попали на 2 страницу
            loggerInfo.trace("step2 \t body=" + pageNumbers.getBody().asNormalizedText());
            HtmlForm formNumbers = pageNumbers.getHtmlElementById("form1");
            HtmlSpan spanNumbers = pageNumbers.getHtmlElementById("head_lblMagicno");
            HtmlTextInput textNumbers = formNumbers.getInputByName("ctl00$head$txtMacigNo");
            textNumbers.type(spanNumbers.getVisibleText());
            HtmlSubmitInput buttonNumbers = formNumbers.getInputByName("ctl00$head$btnNext");
            HtmlPage pageDropDown = buttonNumbers.click();

            //попали на 3 страницу
            loggerInfo.trace("step3 \t body=" + pageDropDown.getBody().asNormalizedText());
            //loggerInfo.trace("step3 \t body=" + pageDropDown.getBody().asXml());
            HtmlSelect select = (HtmlSelect) pageDropDown.getElementById("Select1");
            HtmlOption option = select.getSelectedOptions().get(0);
            HtmlPage pageFlight = webClient.getPage("http://loadrunnertips.com/LoadRunner_Correlation_Challenge_Mod.aspx?dd=1&game=" + option.asNormalizedText());

            //попали на 4 страницу
            loggerInfo.trace("step4 \t body=" + pageFlight.getBody().asNormalizedText());
            //HtmlForm formFlights = pageNumbers.getHtmlElementById("form1");
            try {
                int index = 1;
                while (true) {
                    //проставляем галачки пока находится такой эллемент и значение On Time
                    if (((HtmlSpan) pageFlight.getHtmlElementById("head_lblstatus" + index)).getVisibleText().equalsIgnoreCase("On Time")) {
                        HtmlCheckBoxInput checkBox = pageFlight.getHtmlElementById("head_chk" + index);
                        checkBox.setAttribute("checked", "checked");
                    }
                    index++;
                }
            } catch (Exception ex) {
                //do nothing, элементы head_lblstatus закончились и создался эксепшн
            }
            HtmlSubmitInput buttonFlights = pageFlight.getHtmlElementById("head_btnNext3");
            HtmlPage pageDone = buttonFlights.click();

            //попали на 5 страницу
            loggerInfo.trace("step5 \t body=" + pageDone.getBody().asNormalizedText());
            finish = System.currentTimeMillis();
            if (pageDone.asText().contains("Correlation Challenge Completed (manually). Try now it in Vegen")) {
                addpoint("times", "script", name, "resp", finish - start, "true");
            } else {
                addpoint("times", "script", name, "resp", finish - start, "false");
            }

        } catch (Exception ex) {
            finish = System.currentTimeMillis();
            addpoint("times", "script", name, "resp", finish - start, "false");
            loggerEx.trace(ex.getMessage(), ex);
        }
    }

    @Override
    public void addpoint(String metric, String tagName, String tag, String filedName, long filedValue, String status) {
        try {
            Point influxPoint = Point.measurement(metric)
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .tag(tagName, tag)
                    .tag("user", this.id)
                    .tag("status", status)
                    .addField(filedName, filedValue)
                    .addField("count", 1)
                    .addField("user", this.id)
                    .build();
            batchPoints.point(influxPoint);
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void end() {
    }
}
