package suhoy.utils;


import java.util.concurrent.Executor;

/**
 *
 * @author suh1995
 */
public class ThreadExecutor implements Executor{
    public void execute(Runnable r)
    {
        new Thread(r).start();
    }
}

