package templates;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import obj.Action;
import threader.ThreadExecutor;

/**
 *
 * @author suh1995
 */
public class Controller implements Runnable {

    private ArrayList<Action> actionL = new ArrayList<>();
    //private ArrayList<Script> scriptA = new ArrayList<>();
    private ThreadExecutor threadEx;
    private boolean run = true;
    private final long tick = 200L;
    private Action currentAction;

    public Controller() {

        
    }
    
    public void addAction(String scriptName)
    {
        try{
        
        }
        catch(Exception ex)
        {
            
        }
    }

    public void run() {
        while (run) {
            try {

                
                Thread.sleep(this.tick);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stop() {
        this.run = false;
    }

    public void doAction() {

    }
}
