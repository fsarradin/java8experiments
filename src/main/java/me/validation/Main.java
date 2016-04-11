package me.validation;

import java.util.Optional;

public class Main {

    public static Optional<Integer> toInt(String s) {
        try {
            return Optional.of(Integer.valueOf(s.trim()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        Optional<Integer> value = toInt("s42");

        System.out.println(value);
    }

}
