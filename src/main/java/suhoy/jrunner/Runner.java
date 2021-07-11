package suhoy.jrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.StrBuilder;
import scripts.ExampleWebScript;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;
import suhoy.obj.ActionPool;
import suhoy.obj.Controller;
import suhoy.utils.ThreadExecutor;

/**
 *
 * @author suh1995
 */
public class Runner {

    final static Map<String, List<String>> args = new HashMap<>();
    final static Logger logger_info = LogManager.getLogger("lr_info");
    final static Logger logger_ex = LogManager.getLogger("lr_exception");
    final static Properties properties = new Properties();
    final static ThreadExecutor threadExecutor = new ThreadExecutor();

    public static List<Controller> controlles = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ReadParams(args);
            logger_info.info(returnParams());
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));

            int scriptsCount = Integer.parseInt(properties.getProperty("scripts.count"));
            
            Controller testController = new Controller();
            
            //по количеству скриптов
            for (int i = 0; i < scriptsCount; i++) {
                String scriptName = properties.getProperty("script" + scriptsCount + ".name");
                boolean scriptPacingEnabled = Boolean.parseBoolean(properties.getProperty("script" + scriptsCount + ".pacing.enabled"));
                long scriptPacingMin = Long.parseLong(properties.getProperty("script" + scriptsCount + ".pacing.value").split(",")[0]);
                long scriptPacingMax = Long.parseLong(properties.getProperty("script" + scriptsCount + ".pacing.value").split(",")[1]);
                int scriptStepsCount = Integer.parseInt(properties.getProperty("script" + scriptsCount + ".steps"));

                Object scriptInstance = Class.forName("scripts." + scriptName).getConstructor(String.class, long.class, long.class, boolean.class).newInstance(scriptName, scriptPacingMin, scriptPacingMax, scriptPacingEnabled);
                //

                ActionPool scriptActionPool = new ActionPool();
                //получаем скрипт
                //добавляем скрипт в контроллер
                //по количеству шагов
                for (int j = 0; j < scriptStepsCount; j++) {
                    
                    //ActionPool.add
                    //добавляем шаги в контроллер
                    /*
                    script1.step1=start,10,10
                    script1.step2=wait,10
                    script1.step3=start,5,5
                    script1.step4=wait,5
                    script1.step5=stop,15,1
                     */
                }
                testController.fillActionPool(scriptActionPool);
            }
            threadExecutor.execute(testController);
            

        } catch (Exception ex) {
            logger_ex.error(ex.getMessage(), ex);
        }
    }

    public static void ReadParams(String[] arg) {
        List<String> options = null;
        for (int i = 0; i < arg.length; i++) {
            final String a = arg[i];

            //символ после которого идут аргументы
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return;
                }

                options = new ArrayList<>();
                args.put(a.substring(1), options);
            } else if (options != null) {
                options.add(a);
            } else {
                System.err.println("Illegal parameter usage");
                return;
            }
        }
    }

    public static String returnParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("Arguments:\r\n");
        for (String key : args.keySet()) {
            sb.append(String.format("%s = %s\r\n", key, String.join(", ", args.get(key))));
        }
        return sb.toString();
    }

}
