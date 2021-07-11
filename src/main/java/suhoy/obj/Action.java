package suhoy.obj;

import java.util.ArrayList;
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
    private Thread[] threads;
    //private long priority;
    private boolean done = false;
    private boolean activated = false;
    ThreadExecutor threadExecutor = new ThreadExecutor();

    public Action(String action, long time, User[] users, Script[] script) {
        this.action = action;
        this.time = time;
        /*
        this.users = new User[users.length];
        this.scripts = new Script[script.length];
         */
        this.users = users;
        this.scripts = script;
        this.threads = new Thread[script.length];

    }

    public Action(String action, long time) {
        this.action = action;
        this.time = time;
    }

    private void checkAndActivate() {
        if (!this.activated) {
            this.startTime = System.currentTimeMillis();
            this.finishTime = System.currentTimeMillis() + time * 60 * 1000L;
            if (!action.equalsIgnoreCase("wait")) {
                activateUsers();
            }
            this.activated = true;
        }
    }

    public void work(Logger loggerInfo, Logger loggerEx, ArrayList<Action> actionP) {
        checkAndActivate();
        try {
            switch (action) {
                case ("start"): {

                    for (int i = 0; i < users.length; i++) {
                        if (users[i].shouldIWork()) {
                            threadExecutor.execute(scripts[i]);
                            if (i == users.length - 1) {
                                this.done = true;
                            }
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
                            boolean find = false;
                            //находим в старых экшенах скрипт и стопаем его
                            for (Action act : actionP) {
                                for (Script sc : act.scripts) {
                                    if (sc.running()) {
                                        sc.stop();
                                        find = true;
                                        break;
                                    }
                                    break;
                                } // end for scripts
                                break;
                            } //end for actions
                            if (!find) {
                                this.done = true;
                                break;
                            }
                        } // end if shouldIWork
                        if (i == users.length - 1) {
                            this.done = true;
                        }
                    }
                    /*
                    for (int i = 0; i < users.length; i++) {
                        if (users[i].shouldIWork()) {
                            scripts[i].stop();
                            loggerInfo.info("try to stop: " + scripts[i].id);
                            if (i == users.length - 1) {
                                loggerInfo.info("ITS LAST ONE to STOP: " + scripts[i].id);
                                this.done = true;
                            }
                        }
                    }*/
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
