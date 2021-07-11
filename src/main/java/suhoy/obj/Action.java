package suhoy.obj;

/**
 *
 * @author suh1995
 */
public class Action implements Comparable<Action> {

    private String action;
    private long time;
    private long startTime;
    private long finishTime;
    private User[] users;
    private long priority;
    private boolean done;

    public Action(String action, long time, int users, long priority) {
        this.action = action;
        this.time = time;

        this.priority = priority;
        this.done = false;

        for (int i = 0; i < users; i++) {
            this.users[0] = new User();
        }
    }

    public void activate() {
        this.startTime = System.currentTimeMillis();
        this.finishTime = System.currentTimeMillis() + time * 60 * 1000L;
        activateUsers();
    }

    private void activateUsers() {
        long period = (this.finishTime - this.startTime) / users.length;
        for (int i = 0; i < users.length; i++) {
            users[i].setActionTime(this.startTime + (period * i));
        }
    }

    private boolean done() {
        return this.done();
    }

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

}
