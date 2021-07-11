package suhoy.obj;

import org.apache.logging.log4j.Logger;
import suhoy.utils.ThreadExecutor;

/**
 *
 * @author suh1995
 */
public class Action /*implements Comparable<Action>*/ {

    private String action;
    private long time;
    private long startTime;
    private long finishTime;
    private User[] users;
    private Script[] scripts;
    //private long priority;
    private boolean done = false;
    private boolean activated = false;
    ThreadExecutor threadExecutor = new ThreadExecutor();

    public Action(String action, long time, User [] users, Script[] script) {
        this.action = action;
        this.time = time;
/*
        this.users = new User[users.length];
        this.scripts = new Script[script.length];
*/
        this.users = users;
        this.scripts = script;
       
    }

    public Action(String action, long time) {
        this.action = action;
        this.time = time;
    }

    private void checkAndActivate() {
        if (!this.activated) {
            this.startTime = System.currentTimeMillis();
            this.finishTime = System.currentTimeMillis() + time * 60 * 1000L;
            activateUsers();
            this.activated = true;
        }
    }

    public void work(Logger loggerInfo, Logger loggerEx) {
        try {
            checkAndActivate();
            switch (action) {
                case ("start"): {

                    for (int i = 0; i < users.length; i++) {
                        if (users[i].shouldIWork()) {
                            threadExecutor.execute(scripts[i]);
                            //loggerInfo.info("Started: " + scripts[i].id);
                        }
                    }
                    break;
                }
                case ("wait"): {
                    if (System.currentTimeMillis() >= this.finishTime) {
                        this.done = true;
                    }
                    break;
                }
                case ("stop"): {
                    for (int i = 0; i < users.length; i++) {
                        if (users[i].shouldIWork()) {
                            scripts[i].stop();
                            //loggerInfo.info("Stopped: " + scripts[i].getId());
                        }
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            loggerEx.error(ex.getMessage(), ex);
        }
    }

    private void activateUsers() {
        long period = (this.finishTime - this.startTime) / users.length;
        for (int i = 0; i < users.length; i++) {
            users[i].setActionTime(this.startTime + (period * i));
        }
    }

    public boolean done() {
        return this.done;
    }
    /*
    @Override
    public int compareTo(Action input) {
        if (this.priority == input.priority) {
            return 0;
        } else if (this.priority > input.priority) {
            return 1;
        } else {
            return -1;
        }
    }
     */
}
