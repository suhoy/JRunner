package suhoy.obj;

import java.util.ArrayList;
import suhoy.utils.ThreadExecutor;

/**
 *
 * @author suh1995
 */
public class ActionPool {
    private ArrayList<Action> actions = new ArrayList<>();
    private ArrayList<Script> scripts = new ArrayList<>();
    ThreadExecutor threadExecutor = new ThreadExecutor();
    
    public void addAction(Action action, Script script)
    {
        this.actions.add(action);
        this.scripts.add(script);
    }
    
    public void doAction()
    {
        
    }
}
