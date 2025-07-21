package com.grepp.funfun.app.domain.recommend.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AiRequestQueue {

    private final BlockingQueue<AiRequestTask<?>> requestQueue = new LinkedBlockingQueue<>();

    public <T> void addRequest(AiRequestTask<T> task) {
        requestQueue.offer(task);
    }

    @Scheduled(fixedDelay = 2000) // 30RPM => 2초 간격으로 1개 처리
    public void processQueue() {
        AiRequestTask<?> task = requestQueue.poll();
        if (task != null) {
            try {
                Object result = task.getCallable().call();
                completeTask(task, result);
            } catch (Exception e) {
                task.getFuture().completeExceptionally(e);
            }
        }
    }

    @SuppressWarnings("unckecked")
    private <T> void completeTask(AiRequestTask<T> task, Object result) {
        try{
            task.getFuture().complete((T) result);
        } catch (ClassCastException e) {
            task.getFuture().completeExceptionally(e);
        }
    }

    @Getter
    public static class AiRequestTask<T> {
        private final Callable<T> callable;
        private final CompletableFuture<T> future;

        public AiRequestTask(Callable<T> callable, CompletableFuture<T> future) {
            this.callable = callable;
            this.future = future;
        }
    }
}
