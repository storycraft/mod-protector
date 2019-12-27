package com.storyboard.modProtector.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Parallel {
    
    private static final int NUM_CORES = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService forPool = Executors.newFixedThreadPool(NUM_CORES * 2);

    public static <T> void forEach(T[] elements, Function<T, Void> operation) {
        try {
            forPool.invokeAll(createCallable(elements, operation));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T> void forEach(Iterable<T> elements, Function<T, Void> operation) {
        try {
            forPool.invokeAll(createCallable(elements, operation));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T> void forEach(Iterable<T> elements, Operation<T> operation) {
        try {
            forPool.invokeAll(createCallable(elements, operation));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T, U> void forEachBi(Iterable<Map.Entry<T, U>> elements, BiFunction<T, U, Void> operation) {
        try {
            forPool.invokeAll(createBiCallable(elements, operation));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static <T> Collection<Callable<Void>> createCallable(final Iterable<T> elements, final Function<T, Void> func) {
        List<Callable<Void>> callables = new LinkedList<>();

        for (final T element : elements) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call() {
                    return func.apply(element);
                }
            });
        }

        return callables;
    }

    private static <T> Collection<Callable<Void>> createCallable(final T[] elements, final Function<T, Void> func) {
        List<Callable<Void>> callables = new LinkedList<>();

        for (final T element : elements) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call() {
                    return func.apply(element);
                }
            });
        }

        return callables;
    }

    private static <T, U> Collection<Callable<Void>> createBiCallable(final Iterable<Map.Entry<T, U>> elements, final BiFunction<T, U, Void> func) {
        List<Callable<Void>> callables = new LinkedList<>();

        for (Map.Entry<T, U> element : elements) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call() {
                    return func.apply(element.getKey(), element.getValue());
                }
            });
        }

        return callables;
    }

    @FunctionalInterface
    public interface Operation<T> extends Function<T, Void> {
        void run(T param);

        default Void apply(T param) {
            run(param);
            return null;
        }
    }

}