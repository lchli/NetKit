package com.lch.netkit.v2.util;

public abstract class BackTask<RESULT_TYPE> {


    public final void execute() {
        ADExecutorService.run(new Runnable() {
            @Override
            public void run() {
                final RESULT_TYPE result = doInBackground();

                UiThread.run(new Runnable() {
                    @Override
                    public void run() {
                        doPost(result);
                    }
                });
            }
        });
    }

    protected abstract RESULT_TYPE doInBackground();

    protected  void doPost(RESULT_TYPE result){}


}
