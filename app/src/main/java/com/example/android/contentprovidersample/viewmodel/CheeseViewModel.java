package com.example.android.contentprovidersample.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.example.android.contentprovidersample.data.CheeseLiveData;
import com.example.android.contentprovidersample.data.CursorLiveData;

public class CheeseViewModel extends CursorViewModel {

    private CheeseLiveData mAllCheeses;

    public CheeseViewModel(@NonNull Application application) {
        super(application);
        mAllCheeses = (CheeseLiveData) createCursorLiveData(application);
    }

    @NonNull
    @Override
    public CursorLiveData createCursorLiveData(@NonNull Application application) {
        return new CheeseLiveData(application);
    }

    public CheeseLiveData getAllCheeses() {
        return mAllCheeses;
    }
}
