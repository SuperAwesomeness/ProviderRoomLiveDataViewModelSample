package com.example.android.contentprovidersample.data;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;
import android.util.Log;

public abstract class CursorLiveData
        extends LiveData<Cursor> {

    private static final String TAG = "CursorLiveData";

    @NonNull
    private final Context mContext;

    @NonNull
    private final ForceLoadContentObserver mObserver;

    @Nullable
    private CancellationSignal mCancellationSignal;

    public CursorLiveData(@NonNull Application application) {
        super();
        mContext = application.getApplicationContext();
        mObserver = new ForceLoadContentObserver();
    }

    @Nullable
    public abstract String[] getCursorProjection();

    @Nullable
    public abstract String getCursorSelection();

    @Nullable
    public abstract String[] getCursorSelectionArgs();

    @Nullable
    public abstract String getCursorSortOrder();

    @NonNull
    public abstract Uri getCursorUri();

    private void loadData() {
        loadData(false);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadData(boolean forceQuery) {
        Log.d(TAG, "loadData()");

        if (!forceQuery) {
            Cursor cursor = getValue();
            if (cursor != null
                    && !cursor.isClosed()) {
                return;
            }
        }

        new AsyncTask<Void, Void, Cursor>() {

            @Override
            protected Cursor doInBackground(Void... params) {
                try {
                    synchronized (CursorLiveData.this) {
                        mCancellationSignal = new CancellationSignal();
                    }
                    try {
                        Cursor cursor = ContentResolverCompat.query(
                                mContext.getContentResolver(),
                                getCursorUri(),
                                getCursorProjection(),
                                getCursorSelection(),
                                getCursorSelectionArgs(),
                                getCursorSortOrder(),
                                mCancellationSignal
                        );
                        if (cursor != null) {
                            try {
                                // Ensure the cursor window is filled.
                                cursor.getCount();
                                cursor.registerContentObserver(mObserver);
                            } catch (RuntimeException ex) {
                                cursor.close();
                                throw ex;
                            }
                        }
                        return cursor;
                    } finally {
                        synchronized (CursorLiveData.this) {
                            mCancellationSignal = null;
                        }
                    }
                } catch (OperationCanceledException e) {
                    if (hasActiveObservers()) {
                        throw e;
                    }
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                setValue(cursor);
            }

        }.execute();
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "onActive()");
        loadData();
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive()");
        synchronized (CursorLiveData.this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    @Override
    protected void setValue(Cursor newCursor) {
        Cursor oldCursor = getValue();
        if (oldCursor != null) {
            Log.d(TAG, "setValue() oldCursor.close()");
            oldCursor.close();
        }

        super.setValue(newCursor);
    }

    public final class ForceLoadContentObserver
            extends ContentObserver {

        public ForceLoadContentObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "ForceLoadContentObserver.onChange()");
            loadData(true);
        }

    }

}
