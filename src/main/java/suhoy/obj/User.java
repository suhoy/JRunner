package suhoy.obj;

/**
 *
 * @author suh1995
 */
public class User {

    private long actionTime;
    private boolean done;

    public void setActionTime(long time) {
        this.actionTime = time;
        this.done = false;
    }

    public boolean ready() {
        return actionTime <= System.currentTimeMillis();
    }

    public boolean done() {
        return this.done;
    }

    public void use() {
        this.done = true;
    }

}
