package com.expansemc.bending.plugin.util;

import com.expansemc.bending.api.util.Completion;

public final class CallbackCompletion implements Completion {

    private final Runnable onComplete;
    private final Runnable onCancel;

    private boolean completed = false;
    private boolean cancelled = false;

    public CallbackCompletion(final Runnable onComplete, final Runnable onCancel) {
        this.onComplete = onComplete;
        this.onCancel = onCancel;
    }

    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void complete() {
        this.completed = true;

        this.onComplete.run();
    }

    @Override
    public void cancel() {
        this.completed = true;
        this.cancelled = true;

        this.onCancel.run();
    }
}