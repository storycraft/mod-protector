package com.storyboard.modProtector.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflect {

    public static <T, C>WrappedField<T, C> getField(Object obj, String name) {
        return (WrappedField<T, C>) getField(obj.getClass(), obj, name);
    }

    public static <T, C>WrappedField<T, C> getField(Class<?> c, String name) {
        return (WrappedField<T, C>) getField(c, null, name);
    }

    public static <T, C>WrappedField<T, C> getField(Class<C> c, Object obj, String name) {
        try {
            Field field = getDeclaredField(c, name);

            return new WrappedField<>(field);
        } catch (Exception e) {
            System.out.println("Error to get " + name + " : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static Field getDeclaredField(Class<?> c, String name) {
        try {
            Field field = c.getDeclaredField(name);
            field.setAccessible(true);

            return field;
        } catch (Exception e) {
            System.out.println(name + " field in " + c.getName() + " not found");
            e.printStackTrace();
        }

        return null;
    }

    public static <T, C>WrappedMethod<T, C> getMethod(Object obj, String name, Class... params) {
        return (WrappedMethod<T, C>) getMethod(obj.getClass(), obj, name, params);
    }

    public static <T, C>WrappedMethod<T, C> getMethod(Class<?> c, String name, Class... params) {
        return (WrappedMethod<T, C>) getMethod(c, null, name, params);
    }

    public static <T, C>WrappedMethod<T, C> getMethod(Class<C> c, Object obj, String name, Class... params) {
        try {
            Method method = getDeclaredMethod(c, name, params);

            return new WrappedMethod<>(method);

        } catch (Exception e) {
            System.out.println("Error to get " + name + " : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static <T>WrappedConstructor<T> getConstructor(Class<T> c, Class... params) {
        try {
            Constructor<T> constructor = getDeclaredConstructor(c, params);

            return new WrappedConstructor<>(constructor);

        } catch (Exception e) {
            System.out.println("Error to get class " + c.getName() + " Constructor : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static Method getDeclaredMethod(Class<?> c, String name, Class<?>... classes) {
        try {
            Method method = c.getDeclaredMethod(name, classes);
            method.setAccessible(true);

            return method;
        } catch (Exception e) {
            System.out.println(name + " method in " + c.getName() + " not found");
            e.printStackTrace();
        }

        return null;
    }

    private static Constructor getDeclaredConstructor(Class<?> c, Class<?>... classes) {
        try {
            Constructor constructor = c.getDeclaredConstructor(classes);
            constructor.setAccessible(true);

            return constructor;
        } catch (Exception e) {
            System.out.println("Selected Constructor in " + c.getName() + " not found");
            e.printStackTrace();
        }

        return null;
    }

    public static class WrappedField<T, C> {

        private static Field modifiersField;
        
        static {
            try {
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            
        }

        private Field field;
        private boolean finalLocked;

        public WrappedField(Field field){
            this.field = field;
            this.finalLocked = true;
        }

        public Field getField() {
            return field;
        }

        public String getName(){
            return field.getName();
        }

        public T get(C object){
            try {
                return (T) field.get(object);
            } catch (Exception e) {
                System.out.println("Error to get " + getName() + " : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        public void set(C object, T value){
            try {
                field.set(object, value);
            } catch (Exception e) {
                System.out.println("Error to set " + getName() + " : " + e.getMessage());
                e.printStackTrace();
            }
        }

        public boolean isFinalLocked() {
            return finalLocked;
        }

        public void unlockFinal() {
            if (!isFinalLocked())
                return;

            try {
                modifiersField.setInt(getField(), getField().getModifiers() & ~Modifier.FINAL);
                finalLocked = false;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                System.out.println("Error to unlock final " + getName() + " : " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public static class WrappedMethod<T, C> {

        private Method method;

        public WrappedMethod(Method method){
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public String getName(){
            return method.getName();
        }

        public T invoke(C object, Object... objects){
            try {
                return (T) method.invoke(object, objects);
            } catch (Exception e) {
                System.out.println("Error to invoke " + getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            return null;
        }
    }

    public static class WrappedConstructor<T> {

        private Constructor<T> constructor;

        public WrappedConstructor(Constructor<T> constructor){
            this.constructor = constructor;
        }

        public Constructor getConstructor() {
            return constructor;
        }

        public String getName(){
            return constructor.getName();
        }

        public T createNew(Object... objects){
            try {
                return constructor.newInstance(objects);
            } catch (Exception e) {
                System.out.println("Error to invoke " + getName() + " : " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            return null;
        }
    }
}