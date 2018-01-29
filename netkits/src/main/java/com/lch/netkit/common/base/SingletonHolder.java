package com.lch.netkit.common.base;

public abstract class SingletonHolder<T> {
    private volatile T mInstance;

    public SingletonHolder() {
    }

    protected abstract T create(Object... var1);

    public final T get(Object... args) {
        if(this.mInstance != null) {
            return this.mInstance;
        } else {
            synchronized(this) {
                if(this.mInstance == null) {
                    this.mInstance = this.create(args);
                }

                return this.mInstance;
            }
        }
    }
}