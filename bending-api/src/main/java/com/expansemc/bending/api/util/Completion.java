package com.expansemc.bending.api.util;

public interface Completion {

    boolean isCompleted();

    boolean isCancelled();

    void complete();

    void cancel();
}