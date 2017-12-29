package com.prokarma.myhome.features.fad.details.booking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.auth.AuthManager;

/**
 * Fragment dialog to ask user whether they want to save info from Booking Flow
 */
public class BookingSaveProfileDialog extends DialogFragment {
    public static final String BOOKING_SAVE_PROFILE_DIALOG_TAG = "BookingSaveProfileDialog";
    public static final String PROFILE_KEY = "profile";

    private Profile formsProfile;
    private BookingSaveProfileInterface saveProfileInterface;


    public BookingSaveProfileDialog() {
        // Required empty public constructor
    }

    public static BookingSaveProfileDialog newInstance() {
        BookingSaveProfileDialog fragment = new BookingSaveProfileDialog();
        return fragment;
    }

    public static BookingSaveProfileDialog newInstance(Profile profile) {
        BookingSaveProfileDialog fragment = new BookingSaveProfileDialog();
        Bundle args = new Bundle();
        args.putParcelable(PROFILE_KEY, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            formsProfile = args.getParcelable(PROFILE_KEY);
        }

        return inflater.inflate(R.layout.fragment_enroll_success, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.save_profile_title))
                .setMessage(getString(R.string.save_profile_message))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ProfileManager.updateProfile(AuthManager.getInstance().getBearerToken(), formsProfile, null);
                        dismiss();

                        if (saveProfileInterface != null) {
                            saveProfileInterface.onPromptDismissed(true);
                        }
                        getActivity().getWindow().getDecorView().announceForAccessibility(getResources().getString(R.string.review_your_booking));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();

                        if (saveProfileInterface != null) {
                            saveProfileInterface.onPromptDismissed(false);
                        }
                        getActivity().getWindow().getDecorView().announceForAccessibility(getResources().getString(R.string.review_your_booking));
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setSaveProfileInterface(BookingSaveProfileInterface saveProfileInterface) {
        this.saveProfileInterface = saveProfileInterface;
    }
}
