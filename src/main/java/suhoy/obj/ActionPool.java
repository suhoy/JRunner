package suhoy.obj;

import java.util.ArrayList;
import org.apache.logging.log4j.Logger;
import suhoy.utils.ThreadExecutor;

/**
 *
 * @author suh1995
 */
public class ActionPool {

    private ArrayList<Action> actions = new ArrayList<>();
    private ArrayList<Script> scripts = new ArrayList<>();
    private Logger loggerInfo;
    private Logger loggerEx;
    ThreadExecutor threadExecutor = new ThreadExecutor();

    public void addAction(Action action, Script script, Logger loggerInfo, Logger loggerEx) {
        this.actions.add(action);
        this.scripts.add(script);
        this.loggerInfo = loggerInfo;
        this.loggerEx = loggerEx;

    }

    public void doAction() {

    }
}
