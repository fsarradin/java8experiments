package me.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public class Main {

    public static void main(String[] args) {
        List<?> values = Arrays.asList("hello", 1, 1.5);

        values.stream()
                .map(
                        match()
                                .whenIs("hello").then(__ -> "hello world")
                                .whenTypeIs(Integer.class).then(i -> "int: " + i)
                                .otherwise(__ -> "what else!")
                )
                .forEach(System.out::println);
    }


    static <T, R> MatchCase<T, R> match() {
        return new MatchCase<>();
    }


    static class MatchCase<T, R> implements Function<T, R> {

        final List<Case> cases;

        MatchCase() {
            this(Collections.emptyList());
        }

        MatchCase(List<Case> cases) {
            this.cases = cases;
        }


        When when(Predicate<? super T> predicate) {
            return new When(predicate, cases);
        }

        When whenIs(Object pattern) {
            return when(pattern::equals);
        }

        When whenTypeIs(Class<?> cls) {
            return when(v -> cls.isAssignableFrom(v.getClass()));
        }

        Otherwise otherwise(Function<? super T, ? extends R> function) {
            return new Otherwise(function, cases);
        }


        @Override
        public R apply(T value) {
            return cases.stream()
                    .filter(c -> c.isApplicable(value))
                    .findFirst()
                    .map(c -> c.apply(value))
                    .orElseThrow(IllegalArgumentException::new);
        }


        class When {

            final Predicate<? super T> predicate;
            final List<Case> cases;

            When(Predicate<? super T> predicate, List<Case> cases) {
                this.predicate = predicate;
                this.cases = cases;
            }

            MatchCase<T, R> then(Function<T, ? extends R> function) {
                List<Case> cases = new ArrayList<>();
                cases.addAll(this.cases);
                cases.add(new Case(When.this.predicate, function));

                return new MatchCase<>(cases);
            }

        }

        class Otherwise implements Function<T, R> {

            final Function<? super T, ? extends R> function;
            final List<Case> cases;

            Otherwise(Function<? super T, ? extends R> function, List<Case> cases) {
                this.function = function;
                this.cases = cases;
            }

            @Override
            public R apply(T value) {
                return cases.stream()
                        .filter(c -> c.isApplicable(value))
                        .findFirst()
                        .map(c -> c.apply(value))
                        .orElseGet(() -> function.apply(value));
            }

        }


        class Case implements Function<Object, R> {

            final Predicate<Object> predicate;
            final Function<Object, ? extends R> function;

            @SuppressWarnings("unchecked")
            Case(Predicate<? super T> predicate, Function<? super T, ? extends R> function) {
                this.predicate = (Predicate<Object>) predicate;
                this.function = (Function<Object, ? extends R>) function;
            }

            boolean isApplicable(Object object) {
                return predicate.test(object);
            }

            @Override
            public R apply(Object object) {
                return function.apply(object);
            }

        }

    }

}
