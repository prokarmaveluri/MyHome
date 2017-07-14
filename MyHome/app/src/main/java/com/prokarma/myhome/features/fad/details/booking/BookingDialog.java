package com.prokarma.myhome.features.fad.details.booking;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

/**
 * Created by kwelsh on 7/14/17.
 * Special Dialog that can handle the back button pressed
 */
class BookingDialog extends Dialog {
    private DialogInterface dialogInterface;

    public BookingDialog(@NonNull Context context) {
        super(context);
    }

    public BookingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected BookingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(dialogInterface != null){
            dialogInterface.onBackPressed();
        }
    }

    public void setDialogInterface(DialogInterface dialogInterface) {
        this.dialogInterface = dialogInterface;
    }
}