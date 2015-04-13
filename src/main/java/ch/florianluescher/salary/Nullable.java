package ch.florianluescher.salary;

import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Nullable <T> {
    boolean isPresent();
    T get();
    <U> Nullable<U> map(Function<T,U> f);

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
        public <U> Nullable<U> map(Function<T, U> f) {
            return Nullable.of(f.apply(this.value));
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
        public <U> Nullable<U> map(Function<T, U> f) {
            return new Null<>();
        }
    }
}
