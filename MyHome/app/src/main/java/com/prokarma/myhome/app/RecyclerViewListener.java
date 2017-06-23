package com.prokarma.myhome.app;

/**
 * Created by kwelsh on 5/15/17.
 * This interface defines the contract for a RecyclerView's item click
 */

public interface RecyclerViewListener<T> {
    void onItemClick(T model, int position);
    void onPinClick(T model, int position);
}
