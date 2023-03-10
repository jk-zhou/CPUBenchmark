package com.jkzhou.cpubenchmark;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by @author J.K. Zhou
 * Date: 2023-03-10
 */
public class PrimeCounterThread extends Thread {
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger(0);

    private final int tid;
    private final long from;
    private final long to;
    private int result;

    public PrimeCounterThread(long from, long to) {
        super("PrimeCounter-" + INSTANCE_COUNTER.get());
        this.tid = INSTANCE_COUNTER.getAndIncrement();
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
         System.out.printf("[%s] started: tid=%d, from=%d, to=%d%n", getName(), tid, from, to);
        int primeCount = 0;
        for (long i = from; i < to; i++)
            if (isPrimeNumber(i))
                primeCount++;
        this.result = primeCount;
    }

    public int getTid() {
        return this.tid;
    }

    public int getResult() {
        return this.result;
    }

    private boolean isPrimeNumber(long n) {
        if (n < 2 || n % 2 == 0)
            return false;
        long m = (long) Math.sqrt(n) + 1;
        for (long i = 3; i < m; i += 2)
            if (n % i == 0)
                return false;
        return true;
    }
}
