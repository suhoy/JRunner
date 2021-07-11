package suhoy.jrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.StrBuilder;
import scripts.web.WebScript;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;
import templates.Controller;

/**
 *
 * @author suh1995
 */
public class Runner {

    final static Map<String, List<String>> args = new HashMap<>();
    final static Logger logger_info = LogManager.getLogger("lr_info");
    final static Logger logger_ex = LogManager.getLogger("lr_exception");
    final static Properties properties = new Properties();
    
    public static List<Controller> controlles = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ReadParams(args);
            logger_info.info(returnParams());
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
            
            //по количеству скриптов
            for (int i = 0; i < 10; i++) {
                Object object = Class.forName("scripts.web.WebScript").getConstructor(String.class,int.class, long.class,long.class,boolean.class).newInstance("name", 1, 50L, 50L, true);
                //Controller c = new Controller();
                
                
                //получаем скрипт
                //добавляем скрипт в контроллер
                //по количеству шагов
                for (int j = 0; j < 10; j++) {
                    //добавляем шаги в контроллер
                }
                
                
                //controlles.add();
            }
            
            
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
