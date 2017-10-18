/*
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */

package com.americanwell.sdksample.services;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;

import com.americanwell.sdk.entity.Language;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.util.NamedArrayAdapter;
import com.americanwell.sdksample.util.NothingSelectedSpinnerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment for selecting providers by language
 *
 * @since AWSDK 3.0
 */
public class ProviderLanguageSpokenDialogFragment extends DialogFragment {
    @BindView(R.id.language_spinner)
    Spinner languageSpinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.dialog_provider_language, null);
        ButterKnife.bind(this, view);
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton(R.string.app_ok, null)
                .setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final NothingSelectedSpinnerAdapter adapter = new NothingSelectedSpinnerAdapter(
                new NamedArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        ((ServiceActivity) getActivity()).getPresenter().getLanguages()
                ),
                R.layout.spinner_row_unselected_none,
                android.R.layout.simple_spinner_item,
                getContext()
        );
        adapter.setAllowNone(true);
        languageSpinner.setAdapter(adapter);
        final Language language = ((ServiceActivity) getActivity()).getPresenter().getLanguageSpoken();
        if (language != null) {
            languageSpinner.setSelection(adapter.getPosition(language));
        }

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ServiceActivity) getActivity()).getPresenter().setLanguageSpoken((Language) languageSpinner.getSelectedItem());
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }
}
