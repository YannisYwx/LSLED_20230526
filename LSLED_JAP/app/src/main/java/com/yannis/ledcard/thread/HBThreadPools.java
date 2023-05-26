package com.yannis.ledcard.thread;

/**
 * Author: YWX
 * Date: 2021/12/14 11:44
 * Description:
 */
public class HBThreadPools extends AbsThreadPoolManager {

    public static AbsThreadPoolManager getInstance() {
        return HBThreadPoolsHandler.INSTANCE;
    }

    @Override
    protected String getThreadPoolName() {
        return "HB-ThreadPools";
    }

    public static final class HBThreadPoolsHandler {
        private static final HBThreadPools INSTANCE = new HBThreadPools();
    }

}
