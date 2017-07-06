package com.prokarma.myhome.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.prokarma.myhome.R;

/**
 * Created by stomar on 7/6/17.
 */

/**
 * Dialog Fragment to pick date from spinner mode calendar.
 */
public class DatePickerDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String DATE_PICKER_DIALOG_TAG = "date_picker_dialog_tag";
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePicker datePicker;


    public DatePickerDialogFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.date_picker_dialog);
        datePicker = (DatePicker) dialog.findViewById(R.id.date_picker);
        datePicker.setMaxDate(System.currentTimeMillis());
        dialog.findViewById(R.id.btn_done).setOnClickListener(this);
        return dialog;
    }

    /**
     * Datesetlistener to send back the selected date in @{@link DatePicker}
     * @param dateSetListener
     */
    public void addDateSetListener(DatePickerDialog.OnDateSetListener dateSetListener){
        this.dateSetListener = dateSetListener;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_done){
            dismiss();
            if(datePicker != null){
                dateSetListener.onDateSet(datePicker,datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            }
        }
    }
}
