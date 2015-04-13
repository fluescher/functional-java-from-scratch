package ch.florianluescher.salary;

import java.util.function.Supplier;

public interface Try<T> {

    T getOrElse(T fallback);

    static <T> Try<T> to(Supplier<T> f) {
        try {
            return new Success(f.get());
        } catch (Exception ex) {
            return new Failure<T>(ex);
        }
    }

    class Success<T> implements Try<T> {

        private final T value;

        private Success(T t) {
            value = t;
        }

        @Override
        public T getOrElse(T fallback) {
            return this.value;
        }
    }

    class Failure<T> implements Try<T> {
        private Exception catchedException;

        private Failure(Exception ex) {

            catchedException = ex;
        }

        @Override
        public T getOrElse(T fallback) {
            return fallback;
        }
    }
}
