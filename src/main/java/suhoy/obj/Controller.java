package suhoy.obj;

import java.util.ArrayList;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author suh1995
 */
public class Controller implements Runnable {

    private ArrayList<ActionPool> actionPool = new ArrayList<>();
    private Logger loggerInfo;
    private Logger loggerEx;
    private boolean run = true;
    private final long tick = 100L;

    public Controller(Logger loggerInfo, Logger loggerEx) {
        this.loggerInfo = loggerInfo;
        this.loggerEx = loggerEx;
    }

    public void addActionPool(ActionPool ap) {
        try {
            actionPool.add(ap);
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    public void run() {
        while (run) {
            try {
                run = false;
                long start = System.currentTimeMillis();
                for (ActionPool currentActionPool : actionPool) {

                    currentActionPool.doAction();
                    if (!currentActionPool.done()) {
                        run = true;
                    }
                }
                long finish = System.currentTimeMillis();
                long sleep = this.tick - finish - start;
                if (sleep > 0) {
                    Thread.sleep(this.tick);
                }
            } catch (Exception ex) {
                loggerEx.error(ex.getMessage(), ex);
            }
        }
    }

    public void stop() {
        this.run = false;
    }
}
