package com.storyboard.modProtector.util;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class AsyncTask<T> {

    private final static ExecutorService executor;

    static {
        executor = Executors.newCachedThreadPool();
    }

    private AsyncCallable<T> supplier;

    public AsyncTask(AsyncCallable<T> supplier) {
        this.supplier = supplier;
    }

    public CompletableFuture<T> run() {
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    public T getSync() {
        return supplier.get();
    }

    @FunctionalInterface
    public interface AsyncCallable<T> extends Supplier<T> {
        
    }
}
