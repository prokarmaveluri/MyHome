package com.dignityhealth.myhome.features.fad.details.booking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/*
 * Fragment dialog to display the password criteria.
 *
 * Created by cmajji on 1/03/17.
 */
public class BookingSaveProfileDialog extends DialogFragment {
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
                        sendUpdatedProfile(AuthManager.getInstance().getBearerToken(), formsProfile);
                        dismiss();

                        if (saveProfileInterface != null) {
                            saveProfileInterface.onPromptDismissed(true);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();

                        if (saveProfileInterface != null) {
                            saveProfileInterface.onPromptDismissed(false);
                        }
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * Sends the profile information to the server to update the values
     *
     * @param bearer         the bearer token needed to provide authentication
     * @param updatedProfile the updated profile information being attempted
     */
    private void sendUpdatedProfile(String bearer, Profile updatedProfile) {
        NetworkManager.getInstance().updateProfile(bearer, updatedProfile).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(formsProfile);
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("Something failed! :/");
            }
        });
    }

    public void setSaveProfileInterface(BookingSaveProfileInterface saveProfileInterface) {
        this.saveProfileInterface = saveProfileInterface;
    }
}
