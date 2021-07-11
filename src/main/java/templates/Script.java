package templates;

import utils.Utils;

/**
 *
 * @author suh1995
 */
public abstract class Script implements Runnable {

    protected String name;
    protected long minPacing;
    protected long maxPacing;
    protected int user;
    protected boolean run = true;
    protected boolean pacing = false;

    public Script(String name, int user, long minPacing, long maxPacing, boolean pacing) {
        this.name = name;
        this.user = user; 
        this.minPacing = minPacing;
        this.maxPacing = maxPacing;
        this.pacing = pacing;
    }

    @Override
    final public void run() {
        try {
            init();
            while (run) {
                action();
                Thread.sleep(Utils.getPacing(this.minPacing, this.maxPacing));
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
}
