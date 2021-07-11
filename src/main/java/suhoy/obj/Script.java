package suhoy.obj;

import suhoy.utils.Utils;

/**
 *
 * @author suh1995
 */
public abstract class Script implements Runnable {

    protected String name;
    protected long minPacing;
    protected long maxPacing;
    protected int user;
    protected boolean run = false;
    protected boolean pacing = false;

    public Script(String name, long minPacing, long maxPacing, boolean pacing) {
        this.name = name;
        this.minPacing = minPacing;
        this.maxPacing = maxPacing;
        this.pacing = pacing;
    }

    @Override
    final public void run() {
        this.run = true;
        try {
            init();
            while (run) {
                action();
                if (this.pacing) {
                    Thread.sleep(Utils.getPacing(this.minPacing, this.maxPacing));
                }
            }
            end();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    abstract protected void init();

    abstract protected void action();

    abstract protected void end();

    final public void stop() {
        this.run = false;
    }
    final public String getId() {
        return Thread.currentThread().getName()+"_"+Thread.currentThread().getId()+"_"+name;
    }
}
