package com.tk.mediapicker.callback;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by TK on 2016/9/30.
 * 图片File压缩策略：
 * 50k以下不压缩；
 * 150k以下一档 → 60k左右；
 * 150k以上三挡 → 一般不超过150k；
 * 视频File压缩策略：
 */

public abstract class CompressCallback implements Callback {
    private Context mContext;

    public CompressCallback(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onComplete(File source) {
        onStart();
        int gear = calculGear(source);
        if (gear == -1) {
            onSuccess(source);
            onFinish();
            return;
        }
        Luban.get(mContext)
                .load(source)
                .putGear(gear)
                .asObservable()
                .doOnTerminate(() -> onFinish())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    throwable.printStackTrace();
                    onFailure(throwable);
                })
                .subscribe(file -> {
                    onSuccess(file);
                });
    }

    @Override
    public void onComplete(List<File> sourceList) {
        if (sourceList.size() == 1) {
            onComplete(sourceList.get(0));
            return;
        }
        onStart();
        List<Observable<File>> obserableList = new ArrayList<Observable<File>>();
        List<File> resultList = new ArrayList<File>();
        for (int i = 0; i < sourceList.size(); i++) {
            File f = sourceList.get(i);
            int gear = calculGear(f);
            if (gear == -1) {
                obserableList.add(Observable.just(f));
            } else {
                obserableList.add(Luban.get(mContext)
                        .load(f)
                        .putGear(gear)
                        .asObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
            }
        }
        Observable.merge(obserableList, 1)
                .doOnTerminate(() -> onFinish())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onCompleted() {
                        onSuccess(resultList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        onFailure(e);
                    }

                    @Override
                    public void onNext(File file) {
                        resultList.add(file);
                    }
                });
    }

    protected abstract void onFailure(Throwable throwable);

    protected abstract void onSuccess(File compressFile);

    protected abstract void onSuccess(List<File> compressFileList);

    protected abstract void onStart();

    protected abstract void onFinish();

    protected int calculGear(File source) {
        int gear = -1;
        if (source.length() < 50 * 1024) {
            gear = -1;
        } else if (source.length() < 150 * 1024) {
            gear = Luban.FIRST_GEAR;
        } else {
            gear = Luban.THIRD_GEAR;
        }
        return gear;
    }
}
