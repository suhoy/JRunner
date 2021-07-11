package suhoy.obj;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import suhoy.obj.Action;
import suhoy.obj.ActionPool;
//import threader.ThreadExecutor;

/**
 *
 * @author suh1995
 */
public class Controller implements Runnable {

    private ArrayList<ActionPool> actionP = new ArrayList<>();
    //private ThreadExecutor threadEx;
    private boolean run = true;
    private final long tick = 20L;

    public Controller() {

    }

    public void fillActionPool(ActionPool ap) {
        try {
            actionP.add(ap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (run) {
            try {
                for (ActionPool currentActionPool : actionP) {
                    //пройтись по пулу и что-то запустить
                }

                Thread.sleep(this.tick);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stop() {
        this.run = false;
    }
}
