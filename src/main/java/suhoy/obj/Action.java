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
    //private long priority;
    private boolean done = false;
    private boolean activated = false;
    ThreadExecutor threadExecutor = new ThreadExecutor();

    //start
    public Action(String action, long time, User[] users, Script[] script) {
        this.action = action;
        this.time = time;
        this.users = users;
        this.scripts = script;
    }

    //stop
    public Action(String action, long time, User[] users) {
        this.action = action;
        this.time = time;
        this.users = users;
    }

    //wait
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
                            users[i].setDone();
                            if (i == users.length - 1) {
                                this.done = true;
                            }
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
                            loggerInfo.trace(i + " user ready to stop = " + users[i].shouldIWork());
                            boolean find = false;
                            //находим в старых экшенах скрипт и стопаем его
                            for (Action act : actionP) {
                                if (act.scripts != null) {
                                    for (Script sc : act.scripts) {
                                        loggerInfo.trace("Found script = " + sc.id + ", script is running = " + sc.running());
                                        if (sc.running()) {
                                            sc.stop();
                                            find = true;
                                            users[i].setDone();
                                            if (i == users.length - 1) {
                                                //последний стопнулся
                                                this.done = true;
                                            }
                                            break;
                                        }
                                        if (find) {
                                            break;
                                        }
                                    } // end for scripts
                                }
                                if (find) {
                                    break;
                                }
                            } //end for actions
                            if (!find) {
                                loggerInfo.error("Cant find this amount of scripts to stop from all actions");
                                this.done = true;
                                break;
                            }

                        } // end if shouldIWork
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
