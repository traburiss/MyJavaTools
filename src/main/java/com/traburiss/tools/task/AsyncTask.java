package com.traburiss.tools.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ltc on 2017/9/8.
 *
 * 参考android的AsyncTask设计的简易版AsyncTask，用于快速进行多线程处理的工具，
 * <br><br/>使用方法：继承后重写doInNewThread()方法
 * <br><br/>自动根据RunType来运行线程，默认为parallel
 * <br><br/>parallel：模式默认最多运行CPU核心数*2的线程，
 * <br><br/>serial：模式则会单独开辟一条线程，然后将所有以serial模式运行的AsyncTask按照顺序执行
 * <br><br/>unlimited：则会无限制的启动线程来运行
 */
public abstract class AsyncTask implements Runnable {

    private RunType runType;
    private static long threadNum = 0;

    @Override
    public void run() {

        doInNewThread();
    }

    /**
     * 需要在另一个线程执行的方法
     */
    public abstract void doInNewThread();

    /**
     * 以parallel模式执行线程
     */
    public final void execute() {

        this.runType = RunType.parallel;
        Runner.getInstance().addTask(this, runType);
    }

    /**
     * 执行线程
     * @param runType 执行的方式
     */
    public final void execute(RunType runType) {

        this.runType = runType;
        Runner.getInstance().addTask(this, runType);
    }

    /**
     * 执行线程
     * @param executor 采用自己构造的线程池执行线程
     */
   public final void execute(ExecutorService executor) {

        Runner.getInstance().addTask(this, executor);
    }

    /**
     * 获取运行类型
     * @see com.traburiss.tools.task.AsyncTask.RunType
     * @return serial, parallel, unlimited;
     */
    public RunType getRunType() {
        return runType;
    }

    /**
     * <br><br/>parallel：模式默认最多运行CPU核心数*2的线程，
     * <br><br/>serial：模式则会单独开辟一条线程，然后将所有以serial模式运行的AsyncTask按照顺序执行
     * <br><br/>unlimited：则会无限制的启动线程来运行
     */
    public enum RunType {

        serial, parallel, unlimited;

        @Override
        public String toString() {

            switch (this) {

                case serial:
                    return "serial";
                case parallel:
                    return "parallel";
                case unlimited:
                    return "unlimited";
                default:
                    return "";
            }
        }
    }

    private final static int MAX_THREAD = Runtime.getRuntime().availableProcessors();

    public static int getParallelAMaxThread() {

        return MAX_THREAD;
    }

    public static synchronized boolean parallelSuitable(){

        return getParallelActivityNum() < getParallelAMaxThread() * 2;
    }

    public static synchronized int getParallelActivityNum(){

        return Runner.getInstance().getParallelActivityNum();
    }

    static class Runner {

        private volatile static Runner instance;

        static Runner getInstance() {

            if (instance == null) {

                synchronized (Runner.class) {

                    if (instance == null) {

                        instance = new Runner();
                    }
                }
            }
            return instance;
        }

        private Runner() {
            init();
        }

        private ThreadPoolExecutor parallel;
        private ExecutorService serial;
        private ExecutorService unlimited;
        private ThreadGroup threadGroup;

        private void init() {

            threadGroup = new ThreadGroup("AsyncTask");
            unlimited = Executors.newCachedThreadPool(new DefaultFactory(threadGroup, "UnlimitedTask"));
            serial = Executors.newSingleThreadExecutor(new DefaultFactory(threadGroup, "SerialTask"));
            parallel =  new ThreadPoolExecutor(
                    MAX_THREAD, MAX_THREAD,
                    1L, TimeUnit.MICROSECONDS,
                    new LinkedBlockingQueue<>(),
                    new DefaultFactory(threadGroup, "ParallelTask"));
        }

        synchronized int getParallelActivityNum(){

            return parallel.getActiveCount();
        }

        void addTask(AsyncTask asyncTask, RunType runType) {

            if (null != asyncTask) {

                switch (runType) {
                    case parallel:

                        parallel.submit(asyncTask);
                        break;
                    case serial:

                        serial.submit(asyncTask);
                        break;
                    case unlimited:

                        unlimited.submit(asyncTask);
                }

            }
        }

        void addTask(AsyncTask asyncTask, ExecutorService executorService) {

            if (null != asyncTask && null != executorService) {

                executorService.execute(asyncTask);
            }
        }

        private class DefaultFactory implements ThreadFactory{

            private ThreadGroup group;
            private String prefix;

            private DefaultFactory(ThreadGroup group, String prefix){

                this.group = group;
                this.prefix = prefix;
            }

            @Override
            public Thread newThread(Runnable r) {

                Thread t = new Thread(group, r, prefix+threadNum++,0);
                if (t.isDaemon())
                    t.setDaemon(false);
                if (t.getPriority() != Thread.NORM_PRIORITY)
                    t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        }
    }
}
