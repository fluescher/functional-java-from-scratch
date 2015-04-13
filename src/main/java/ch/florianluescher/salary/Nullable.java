package ch.florianluescher.salary;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Nullable <T> {
    boolean isPresent();
    T get();
    T getOrElse(T fallback);
    <U> Nullable<U> map(Function<T,U> f);
    <U> Nullable<U> flatMap(Function<T, Nullable<U>> f);
    Nullable<T> filter(Predicate<T> predicate);

    static <T> Nullable<T> of(T reference) {
        if(reference == null) return new Null();

        return new Some(reference);
    }

    class Some<T> implements Nullable<T> {

        private final T value;

        private Some(T value) {
            if(value == null) throw new RuntimeException("Tried to initialize Some instance with null");

            this.value = value;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public T getOrElse(T fallback) {
            return value;
        }

        @Override
        public <U> Nullable<U> map(Function<T, U> f) {
            return Nullable.of(f.apply(this.value));
        }

        @Override
        public <U> Nullable<U> flatMap(Function<T, Nullable<U>> f) {
            return f.apply(this.value);
        }

        @Override
        public Nullable<T> filter(Predicate<T> predicate) {
            if(predicate.test(this.value)) {
                return this;
            } else {
                return new Null<T>();
            }
        }
    }

    class Null<T> implements Nullable<T> {

        private Null() {
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public T get() {
            throw new NoSuchElementException();
        }

        @Override
        public T getOrElse(T fallback) {
            return fallback;
        }

        @Override
        public <U> Nullable<U> map(Function<T, U> f) {
            return new Null<>();
        }

        @Override
        public <U> Nullable<U> flatMap(Function<T, Nullable<U>> f) {
            return new Null<>();
        }

        @Override
        public Nullable<T> filter(Predicate<T> predicate) {
            return this;
        }
    }
}
