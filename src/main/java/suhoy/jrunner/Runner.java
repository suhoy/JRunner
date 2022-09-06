package suhoy.jrunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Properties;
import suhoy.obj.Action;
import suhoy.obj.ActionPool;
import suhoy.obj.Controller;
import suhoy.obj.Script;
import suhoy.obj.User;
import suhoy.utils.InfluxSettings;
import suhoy.utils.ThreadExecutor;

/**
 *
 * @author suh1995
 */
public class Runner {

    final static Map<String, List<String>> args = new HashMap<>();
    final static Logger loggerInfo = LogManager.getLogger("lr_info");
    final static Logger loggerEx = LogManager.getLogger("lr_exception");
    final static Properties properties = new Properties();
    final static ThreadExecutor threadExecutor = new ThreadExecutor();

    public static List<Controller> controlles = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ReadParams(args);
            loggerInfo.info(returnParams());
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));

            //read influx
            InfluxSettings influxSet = new InfluxSettings(
                    properties.getProperty("influx.endpoint"),
                    properties.getProperty("influx.database"),
                    properties.getProperty("influx.retention"),
                    properties.getProperty("influx.user"),
                    properties.getProperty("influx.pass"),
                    Integer.parseInt(properties.getProperty("influx.batch")));

            int scriptsCount = Integer.parseInt(properties.getProperty("scripts.count"));

            Controller testController = new Controller(loggerInfo, loggerEx);

            //по количеству скриптов
            for (int i = 1; i < scriptsCount + 1; i++) {
                //получаем параметры скрипта
                String scriptName = properties.getProperty("script" + i + ".name");
                boolean scriptPacingEnabled = Boolean.parseBoolean(properties.getProperty("script" + i + ".pacing.enabled"));
                long scriptPacingMin = Long.parseLong(properties.getProperty("script" + i + ".pacing.value").split(",")[0]);
                long scriptPacingMax = Long.parseLong(properties.getProperty("script" + i + ".pacing.value").split(",")[1]);
                int scriptStepsCount = Integer.parseInt(properties.getProperty("script" + i + ".steps"));

                //получаем скрипт
                //Object scriptInstance = Class.forName("scripts." + scriptName).getConstructor(String.class, long.class, long.class, boolean.class).newInstance(scriptName, scriptPacingMin, scriptPacingMax, scriptPacingEnabled);
                //создаем пул
                ActionPool actionPool = new ActionPool();

                //по количеству шагов добавляем шаги в пул, а потом в контроллер
                for (int j = 1; j < scriptStepsCount + 1; j++) {
                    String stepAction = properties.getProperty("script" + i + ".step" + j).toLowerCase();
                    String[] action = stepAction.split(",");
                    switch (action[0]) {
                        case ("start"): {
                            //заполняем массив скриптов инстансами скрипта
                            Script[] scripts = new Script[Integer.parseInt(action[1])];
                            User[] users = new User[Integer.parseInt(action[1])];
                            for (int u = 0; u < Integer.parseInt(action[1]); u++) {
                                //add if statement on scriptName if you want use diffrent constructor for specific scripts
                                scripts[u] = (Script) Class.forName("scripts." + scriptName)
                                        .getConstructor(String.class, long.class, long.class, boolean.class, Logger.class, Logger.class, InfluxSettings.class)
                                        .newInstance(scriptName, scriptPacingMin, scriptPacingMax, scriptPacingEnabled, loggerInfo, loggerEx, influxSet);
                                users[u] = new User();
                            }
                            actionPool.addAction(new Action(action[0], Long.parseLong(action[2]), users, scripts), loggerInfo, loggerEx);
                            break;
                        }
                        case ("stop"): {
                            //заполняем массив юзеров
                            User[] users = new User[Integer.parseInt(action[1])];
                            for (int u = 0; u < Integer.parseInt(action[1]); u++) {
                                //System.out.println("created user to stop"+u);
                                users[u] = new User();
                            }
                            actionPool.addAction(new Action(action[0], Long.parseLong(action[2]), users), loggerInfo, loggerEx);
                            break;
                        }
                        case ("wait"): {
                            actionPool.addAction(new Action(action[0], Long.parseLong(action[1])), loggerInfo, loggerEx);
                            break;
                        }
                    } // end switch  
                } // end for scriptStepsCount
                testController.addActionPool(actionPool);
            } // end for scriptsCount
            threadExecutor.execute(testController);

        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
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
