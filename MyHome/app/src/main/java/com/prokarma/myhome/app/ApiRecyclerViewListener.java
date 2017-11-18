package com.prokarma.myhome.app;

/**
 * Created by kwelsh on 11/17/17.
 * This interface defines the contract for a RecyclerView's item click
 */

public interface ApiRecyclerViewListener<T> {
    void onPreferenceChanged(T model, int position, boolean isChecked);
}
