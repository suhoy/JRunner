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
    //private ArrayList<Script[]> scripts = new ArrayList<>();
    private Logger loggerInfo;
    private Logger loggerEx;
    private boolean done = false;

    public void addAction(Action action, /*Script[] script,*/ Logger loggerInfo, Logger loggerEx) {
        this.actions.add(action);
        //this.scripts.add(script);
        this.loggerInfo = loggerInfo;
        this.loggerEx = loggerEx;
    }

    public void doAction() {
        this.done = true;
        /*if (actions.size() != scripts.size()) {
            loggerEx.error("ActionPool: actions.size()!=scripts.size()");
            return;
        }*/
        //по пулу actions
        for (int i = 0; i < actions.size(); i++) {
            //если action ещё не выполнен - делаем
            if (!actions.get(i).done()) {
                //работаем
                this.done = false;
                actions.get(i).work(loggerInfo, loggerEx, actions);
                break;
                /*//если после работы задача не выполнена
                if (!actions.get(i).done()) {
                    //выходим из цикла т.к. action ещё не доделан
                    break;
                }*/
            } else {
                //если action выполнен - пропускаем и берём следующий action
                continue;
            }
        }
    }

    public boolean done() {
        return this.done;
    }
}
