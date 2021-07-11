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
    //private long priority;
    private boolean done = false;
    private boolean activated = false;

    public Action(String action, long time, int users) {
        this.action = action;
        this.time = time;

        this.users = new User[users];
        for (int i = 0; i < users; i++) {
            this.users[i] = new User();
        }
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

    public void work(Script script, ThreadExecutor threadExecutor,Logger loggerInfo) {
        checkAndActivate();
        switch (action) {
            case ("start"): {
                for (User currentUser : users) {
                    if (currentUser.shouldIWork()) {
                        threadExecutor.execute(script);
                        loggerInfo.info("Started: "+script.getId());
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

                for (User currentUser : users) {
                    if (currentUser.shouldIWork()) {
                        script.stop();
                        loggerInfo.info("Stopped: "+script.getId());
                    }
                }
                break;
            }
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
