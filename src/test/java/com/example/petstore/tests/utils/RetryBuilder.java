package com.example.petstore.tests.utils;

import java.time.Duration;
import java.util.function.Supplier;

public class RetryBuilder<T> {
    private int maxAttempts = 3;
    private Duration interval = Duration.ofSeconds(1);

    public RetryBuilder<T> withMaxAttempts(int attempts) {
        this.maxAttempts = attempts;
        return this;
    }

    public RetryBuilder<T> withInterval(Duration interval) {
        this.interval = interval;
        return this;
    }

    @SuppressWarnings("BusyWait")
    public void run(Supplier<T> action) {
        int attempt = 0;
        Throwable lastException = null;

        while (attempt < maxAttempts) {
            try {
                action.get();
                return;
            } catch (Throwable t) {
                lastException = t;
                attempt++;
                if (attempt < maxAttempts) {
                    try { Thread.sleep(interval.toMillis()); } catch (InterruptedException ignored) {}
                }
            }
        }
        throw new RuntimeException("Retry failed after " + maxAttempts + " attempts", lastException);
    }
}
