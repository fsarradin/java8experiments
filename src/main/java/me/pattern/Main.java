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


    static <T> Matcher<T> match() {
        return new Matcher<>();
    }


    static class Matcher<T> {

        <R> When<R> when(Predicate<? super T> predicate) {
            return new When<>(predicate);
        }

        <R> When<R> whenIs(Object pattern) {
            return when(pattern::equals);
        }

        <R> When<R> whenTypeIs(Class<?> cls) {
            return when(v -> cls.isAssignableFrom(v.getClass()));
        }

        <R> Otherwise<R> otherwise(Function<? super T, ? extends R> function) {
            return new Otherwise<R>(function, Collections.emptyList());
        }


        class When<R> {

            final Predicate<? super T> predicate;
            final List<Case<R>> cases;

            When(Predicate<? super T> predicate) {
                this(predicate, Collections.emptyList());
            }

            When(Predicate<? super T> predicate, List<Case<R>> cases) {
                this.predicate = predicate;
                this.cases = cases;
            }

            Then then(Function<T, ? extends R> function) {
                List<Case<R>> cases = new ArrayList<>();
                cases.addAll(this.cases);
                cases.add(new Case<>(When.this.predicate, function));

                return new Then(cases);
            }

            class Then implements Function<T, R> {

                final List<Case<R>> cases;

                Then(List<Case<R>> cases) {
                    this.cases = cases;
                }

                @Override
                public R apply(T value) {
                    return cases.stream()
                            .filter(c -> c.isApplicable(value))
                            .findFirst()
                            .map(c -> c.apply(value))
                            .orElseThrow(IllegalArgumentException::new);
                }

                When<R> when(Predicate<? super T> predicate) {
                    return new When<R>(predicate, cases);
                }

                When<R> whenIs(Object pattern) {
                    return when(pattern::equals);
                }

                When<R> whenTypeIs(Class<?> cls) {
                    return when(v -> cls.isAssignableFrom(v.getClass()));
                }

                Otherwise<R> otherwise(Function<? super T, ? extends R> function) {
                    return new Otherwise<R>(function, cases);
                }

            }

        }

        class Otherwise<R> implements Function<T, R> {

            final Function<? super T, ? extends R> function;
            final List<Case<R>> cases;

            Otherwise(Function<? super T, ? extends R> function, List<Case<R>> cases) {
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


        class Case<R> implements Function<Object, R> {

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
