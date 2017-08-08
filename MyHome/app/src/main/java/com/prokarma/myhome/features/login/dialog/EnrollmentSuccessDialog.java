package com.prokarma.myhome.features.login.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prokarma.myhome.R;

/*
 * Fragment dialog to display the password criteria.
 *
 * Created by cmajji on 1/03/17.
 */
public class EnrollmentSuccessDialog extends DialogFragment {

    public EnrollmentSuccessDialog() {
        // Required empty public constructor
    }

    public static EnrollmentSuccessDialog newInstance() {
        EnrollmentSuccessDialog fragment = new EnrollmentSuccessDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_enroll_success, container, false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        // Use the Builder class for convenient dialog construction
        TextView textView = new TextView(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.tradeGothicLTStd_Dynamic18);
        } else {
            textView.setTextAppearance(getActivity(), R.style.tradeGothicLTStd_Dynamic18);
        }
        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        float density = getResources().getDisplayMetrics().density;
        int margin = (int) (density * 16);
        params.setMargins(margin, margin, margin, 0);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setLayoutParams(params);
        textView.setText(R.string.enrollment_success_dialog_tittle);
        layout.addView(textView);
        builder.setCustomTitle(layout)

                .setMessage(R.string.enrollment_success_dialog_msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
