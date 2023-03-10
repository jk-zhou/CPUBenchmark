package com.jkzhou.cpubenchmark;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author J.K. Zhou
 * Date: 2023-03-10
 */
public class CPUBenchmark {
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final long RANGE_START = 1_000_000_000L; // 1 billion
    private static final long INCREMENT = 50_000_000L; // 50 millions

    /**
     * threadCount: number of thread
     * workload: from 1 to 10.
     */
    public static void main(String[] args) throws Exception {
        printSysInfo();

        // get arguments
        int threadCount = Math.max(readInt(args, 0, AVAILABLE_PROCESSORS), 1);
        int workload = Math.min(readInt(args, 1, 1), 10);

        run(threadCount, workload);
    }

    private static void printCliUsage() {
        System.out.println("Usage: java com.jkzhou.cpubenchmark.CPUBenchmark [threadCount] [workload (1~10)]");
    }

    private static void printSysInfo() {
        System.out.println("\n********** System Info **********");
        System.out.printf("System available processors: %d%n%n", AVAILABLE_PROCESSORS);

        final Runtime runtime = Runtime.getRuntime();
        final long totalBytes = runtime.maxMemory();
        final long usedBytes = runtime.totalMemory() - runtime.freeMemory();
        final long freeBytes = totalBytes - usedBytes;
        System.out.println("\n********** Heap Stats [MB] **********");
        System.out.printf("Total Memory: %d%n", totalBytes >> 20);
        System.out.printf("Used  Memory: %d%n", usedBytes >> 20);
        System.out.printf("Free  Memory: %d%n", freeBytes >> 20);
        System.out.println();
    }

    private static int readInt(String[] args, int i, int defaultVal) {
        try {
            return i < args.length ? Integer.parseInt(args[i]) : defaultVal;
        } catch (Exception e) {
            e.printStackTrace();
            printCliUsage();
            throw e;
        }
    }

    private static void run(final int threadCount, final int workload) throws InterruptedException {
        // check numbers between 1 billion and 2 billion.
        final long totalRange = INCREMENT * workload;
        final long interval = totalRange / threadCount;

        System.out.println("\n********** In Progress **********");
        System.out.printf("Processing [%s] integers with [%d] threads ...%n",
                NumberFormat.getNumberInstance(Locale.US).format(totalRange), threadCount);

        final int[] results = new int[threadCount];
        final PrimeCounterThread[] threads = new PrimeCounterThread[threadCount];

        // Create PrimeCounterThread
        for (int i = 0; i < threadCount; i++) {
            long start = RANGE_START + interval * i;
            long end = i == threadCount - 1 ? RANGE_START + totalRange : start + interval;
            threads[i] = new PrimeCounterThread(start, end);
        }

        final long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(runBenchmark(threads, results));
        final double valPerSec = totalRange / (elapsedMillis / 1000d);

        // sum all results
        final long primeCount = Arrays.stream(results).boxed().mapToLong(i -> (long) i).sum();

        // format and output results
        System.out.println("\n********** Results **********");
        System.out.println("Total threads   : " + threadCount);
        System.out.println("Total processed : " + format(totalRange));
        System.out.println("Prime # count   : " + format(primeCount));
        System.out.println("Time used (sec) : " + format(TimeUnit.MILLISECONDS.toSeconds(elapsedMillis)));
        System.out.println("Speed (val/sec) : " + format(valPerSec));
    }

    private static long runBenchmark(final PrimeCounterThread[] threads, final int[] results) throws InterruptedException {
        // start all threads
        final long startTime = System.nanoTime();
        for (PrimeCounterThread thread : threads)
            thread.start();

        // wait threads to finish
        System.out.println("All threads spawned, waiting for results ...");
        for (PrimeCounterThread thread : threads)
            thread.join();
        final long endTime = System.nanoTime();

        for (PrimeCounterThread thread : threads)
            results[thread.getTid()] = thread.getResult();

        return endTime - startTime;
    }

    private static String format(Number num) {
        return NumberFormat.getNumberInstance(Locale.US).format(num);
    }
}