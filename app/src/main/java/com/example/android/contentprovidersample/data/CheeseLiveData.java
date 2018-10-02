package com.example.android.contentprovidersample.data;

import android.app.Application;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.contentprovidersample.provider.CheeseContentProvider;

public class CheeseLiveData extends CursorLiveData {

    public CheeseLiveData(@NonNull Application application) {
        super(application);
    }

    @Nullable
    @Override
    public String[] getCursorProjection() {
        return new String[]{Cheese.COLUMN_NAME};
    }

    @Nullable
    @Override
    public String getCursorSelection() {
        return null;
    }

    @Nullable
    @Override
    public String[] getCursorSelectionArgs() {
        return null;
    }

    @Nullable
    @Override
    public String getCursorSortOrder() {
        return null;
    }

    @NonNull
    @Override
    public Uri getCursorUri() {
        return CheeseContentProvider.URI_CHEESE;
    }
}
