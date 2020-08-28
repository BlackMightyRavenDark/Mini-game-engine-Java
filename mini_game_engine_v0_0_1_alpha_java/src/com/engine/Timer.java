package com.engine;

public class Timer {

    public static final long NS_PER_SECOND = 1000000000L;

    private long lastTime;
    private long lastTimeDelta;

    public Timer() {
        lastTime = System.nanoTime();
        lastTimeDelta = lastTime;
    }

    public void reset() {
        lastTime = System.nanoTime();
    }

    public double getElapsedTime() {
        return (System.nanoTime() - lastTime) / (double)NS_PER_SECOND;
    }

    public double getDeltaTime() {
        long currentTime = System.nanoTime();
        double dt = (currentTime - lastTimeDelta) / (double)NS_PER_SECOND;
        lastTimeDelta = currentTime;
        return dt;
    }
}
