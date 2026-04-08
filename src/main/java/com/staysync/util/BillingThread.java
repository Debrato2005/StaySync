package com.staysync.util;

import java.util.function.Consumer;

import com.staysync.model.Booking;

import javafx.application.Platform;

public class BillingThread extends Thread {
    private final Booking booking;
    private final Consumer<Double> onComplete;

    public BillingThread(Booking booking, Consumer<Double> onComplete) {
        this.booking = booking;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        double total = booking.calculateTotal();
        Platform.runLater(() -> onComplete.accept(total));
    }
}