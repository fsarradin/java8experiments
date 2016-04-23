package me.mixin;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class Main {

    interface WithName {
        Map<WithName, String> names = Collections.synchronizedMap(new WeakHashMap<>());

        default String getName() {
            return names.get(this);
        }

        default void setName(String name) {
            names.put(this, name);
        }
    }

    interface MyMixture extends Runnable, WithName {}

    public static void main(String[] args) {
        MyMixture mixture1 = createMixture("hello");
        MyMixture mixture2 = createMixture("hello");

        mixture1.setName("mixture 1");
        mixture2.setName("mixture 2");

        System.out.println(mixture1.getName());
        System.out.println(mixture2.getName());
    }

    private static MyMixture createMixture(String message) {
        return () -> System.out.println(message);
    }

}
