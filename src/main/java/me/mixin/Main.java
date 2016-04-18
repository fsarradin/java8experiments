package me.mixin;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class Main {

    interface MyMixture extends Runnable {}

    public static void main(String[] args) {
        MyMixture mixture1 = () -> System.out.println("hello");
        MyMixture mixture2 = () -> System.out.println("hello");
    }

}
