package com.example.android.contentprovidersample.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.android.contentprovidersample.data.CursorLiveData;

public abstract class CursorViewModel<D extends CursorLiveData> extends AndroidViewModel {

    @NonNull
    protected final D mCursorLiveData;

    public CursorViewModel(@NonNull Application application) {
        super(application);
        mCursorLiveData = createCursorLiveData(application);
    }

    @NonNull
    public abstract D createCursorLiveData(@NonNull Application application);

    @Override
    protected void onCleared() {
        Cursor cursor = mCursorLiveData.getValue();
        if (cursor != null) {
            cursor.close();
        }
    }
}
