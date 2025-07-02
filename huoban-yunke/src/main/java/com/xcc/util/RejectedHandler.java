package com.xcc.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedHandler implements RejectedExecutionHandler {
    private final BlockingQueue<Runnable> queue;

    public RejectedHandler(BlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            queue.put(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
