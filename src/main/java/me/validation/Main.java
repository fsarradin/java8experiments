package me.validation;

import java.util.Optional;

public class Main {

    public static Validation<Exception, Integer> toInt(String s) {
        try {
            return new Validation.Success<>(Integer.valueOf(s.trim()));
        } catch (Exception e) {
            return new Validation.Failure<>(e);
        }
    }

    public static void main(String[] args) {
        Validation<Exception, Integer> value = toInt(null);


        System.out.println(value.toOptional());
        System.out.println(value.swap().toOptional());
    }

}

abstract class Validation<E, T> {
    private Validation() {}

    public abstract Optional<T> toOptional();
    public abstract Validation<T, E> swap();

    static class Success<E, T> extends Validation<E, T> {
        private final T value;

        public Success(T value) {

            this.value = value;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }

        @Override
        public Failure<T, E> swap() {
            return new Failure<>(value);
        }
    }
    static class Failure<E, T> extends Validation<E, T> {
        private final E error;

        public Failure(E error) {

            this.error = error;
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }

        @Override
        public Success<T, E> swap() {
            return new Success<>(error);
        }
    }
}
