package com.yannis.ledcard.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: YWX
 * Date: 2021/12/14 11:26
 * Description:
 */
public class ThreadPoolsUtil {
    private static Map<String, ThreadPoolExecutor> map = new Hashtable<>();
    private Handler uiThreadHandler;
    private ThreadPoolExecutor executor;
    /**
     * 阻塞任务队列数
     */
    private int waitingCount;
    /**
     * 线程池的名字，e.g:子系统的包名(com.tool.me)
     */
    @SuppressWarnings("unused")
    private String name;

    /**
     * 创建线程池
     *
     * @param name            线程池的名字，eg:子系统的包名(com.tool.me)
     * @param corePoolSize    核心线程池大小 the number of threads to keep in the pool, even if
     *                        they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize 最大线程池大小 the maximum number of threads to allow in the pool
     * @param keepAliveTime   线程池中超过corePoolSize数目的空闲线程最大存活时间 when the number of threads is
     *                        greater than the core, this is the maximum time that excess
     *                        idle threads will wait for new tasks before terminating.
     * @param unit            keepAliveTime时间单位 the time unit for the {@code keepAliveTime}
     *                        argument
     * @param workQueue       阻塞任务队列 the queue to use for holding tasks before they are
     *                        executed. This queue will hold only the {@code Runnable} tasks
     *                        submitted by the {@code execute} method.
     */
    public ThreadPoolsUtil(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                           BlockingQueue<Runnable> workQueue) {
        synchronized (ThreadPoolsUtil.this) {
            this.name = name;
            this.waitingCount = workQueue.size();
            this.uiThreadHandler = new Handler(Looper.getMainLooper());
            String key = buildKey(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue.size(), "#");
            if (map.containsKey(key)) {
                executor = map.get(key);
            } else {
                executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
                map.put(key, executor);
            }
        }
    }

    /**
     * 创建线程池
     *
     * @param name            线程池的名字，eg:子系统的包名(com.tool.me)
     * @param corePoolSize    核心线程池大小 the number of threads to keep in the pool, even if
     *                        they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize 最大线程池大小 the maximum number of threads to allow in the pool
     * @param keepAliveTime   线程池中超过corePoolSize数目的空闲线程最大存活时间 when the number of threads is
     *                        greater than the core, this is the maximum time that excess
     *                        idle threads will wait for new tasks before terminating.
     * @param unit            keepAliveTime时间单位 the time unit for the {@code keepAliveTime}
     *                        argument
     * @param waitingCount    阻塞任务队列数
     */
    public ThreadPoolsUtil(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                           int waitingCount) {
        synchronized (ThreadPoolsUtil.this) {
            this.name = name;
            this.waitingCount = (int) (waitingCount * 1.5);
            uiThreadHandler = new Handler(Looper.getMainLooper());
            String key = buildKey(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, waitingCount, "#");
            if (map.containsKey(key)) {
                executor = map.get(key);
            } else {
                executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                        new LinkedBlockingQueue<Runnable>(this.waitingCount));
                map.put(key, executor);
            }
        }
    }

    /**
     * 组装map中的key
     *
     * @param name            线程池的名字，eg:子系统的包名(com.tool.me)
     * @param corePoolSize    核心线程池大小 the number of threads to keep in the pool, even if
     *                        they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize 最大线程池大小 the maximum number of threads to allow in the pool
     * @param keepAliveTime   线程池中超过corePoolSize数目的空闲线程最大存活时间 when the number of threads is
     *                        greater than the core, this is the maximum time that excess
     *                        idle threads will wait for new tasks before terminating.
     * @param unit            keepAliveTime时间单位 the time unit for the {@code keepAliveTime}
     *                        argument
     * @param workQueue       阻塞任务队列 the queue to use for holding tasks before they are
     *                        executed. This queue will hold only the {@code Runnable} tasks
     *                        submitted by the {@code execute} method.
     * @param delimiter       分割符
     */
    private String buildKey(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                            int waitingCount, String delimiter) {
        StringBuilder result = new StringBuilder();
        result.append(name).append(delimiter);
        result.append(corePoolSize).append(delimiter);
        result.append(maximumPoolSize).append(delimiter);
        result.append(keepAliveTime).append(delimiter);
        result.append(unit.toString()).append(delimiter);
        result.append(waitingCount);
        return result.toString();
    }

    /**
     * 添加任务到线程池（execute）中
     *
     * @param runnable the task to execute
     */
    public void execute(Runnable runnable) {
        checkQueneSize();
        executor.execute(runnable);
    }

    /**
     * 添加任务到线程池（execute）中
     *
     * @param runnable the task to execute
     * @param call     after execute call in ui thread
     */
    public void executeWithCallback(Runnable runnable, Call call) {
        checkQueneSize();
        executor.execute(() -> {
            runnable.run();
            uiThreadHandler.post(() -> {
                if (call != null) {
                    call.runOnForeground();
                }
            });
        });
    }

    private void checkQueneSize() {
        while (getTaskSize() >= waitingCount) {//如果线程池中的阻塞队列数 > wattingCount 则继续等待
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this collection
     */
    public int getTaskSize() {
        return executor.getQueue().size();
    }

    public interface Call {
        void runOnForeground();
    }
}
