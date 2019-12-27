package com.storyboard.modProtector.storage;

import com.storyboard.modProtector.util.AsyncTask;

import java.io.IOException;

public abstract class Storage<T> {

    public abstract boolean saveSync(T object, String name) throws IOException;

    public AsyncTask<Boolean> saveAsync(T object, String name) throws IOException{
        return new AsyncTask<>(new AsyncTask.AsyncCallable<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    return saveSync(object, name);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });
    }

    public abstract T getSync(String name) throws IOException;

    public AsyncTask<T> getAsync(String name){
        return new AsyncTask<>(new AsyncTask.AsyncCallable<T>() {
            @Override
            public T get() {
                try {
                    return getSync(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });
    }
}
