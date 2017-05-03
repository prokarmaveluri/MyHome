package com.dignityhealth.myhome.features.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class ProfileEditFragment extends BaseFragment {
    public static final String PROFILE_EDIT_TAG = "profile_edit_tag";

    View profileView;
    TextView firstName;
    TextView lastName;
    TextView dateOfBirth;
    TextView address;
    TextView city;
    TextView state;
    TextView zip;
    TextView phone;
    TextView email;

    TextView insuranceProvider;
    TextView memberId;
    TextView group;

    private final String placeholderText = "Not Available";

    public static ProfileEditFragment newInstance() {
        return new ProfileEditFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.profile_edit, container, false);

        firstName = (EditText) profileView.findViewById(R.id.first_name);
        lastName = (EditText) profileView.findViewById(R.id.last_name);
        dateOfBirth = (EditText) profileView.findViewById(R.id.dob);
        address = (EditText) profileView.findViewById(R.id.address);
        city = (EditText) profileView.findViewById(R.id.city);
        state = (EditText) profileView.findViewById(R.id.state);
        zip = (EditText) profileView.findViewById(R.id.zip);
        phone = (EditText) profileView.findViewById(R.id.phone);
        email = (TextView) profileView.findViewById(R.id.email);

        insuranceProvider = (EditText) profileView.findViewById(R.id.provider);
        memberId = (EditText) profileView.findViewById(R.id.id);
        group = (EditText) profileView.findViewById(R.id.group);

        if (ProfileManager.getProfile() == null) {
            //Get profile since we don't have it
            getProfileInfo("Bearer " + AuthManager.getBearerToken());
        } else {
            //We have a profile singleton; just update info.
            updateProfileViews(ProfileManager.getProfile());
        }

        setHasOptionsMenu(true);
        return profileView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.profile_edit_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.save_profile:
                Profile currentProfile = ProfileManager.getProfile();
                sendUpdatedProfile("Bearer " + AuthManager.getBearerToken(), getProfileValues(currentProfile));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfileInfo(String bearer) {
        NetworkManager.getInstance().getProfile(bearer).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(response.body());
                    updateProfileViews(response.body());
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Timber.e("Something failed! :/");
            }
        });
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
                    Toast.makeText(getActivity(), "Information saved", Toast.LENGTH_SHORT).show();
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                    Toast.makeText(getActivity(), "Unable to update Profile", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("Something failed! :/");
                Toast.makeText(getActivity(), "Unable to update Profile", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Updates the view on screen to have the proper profile values autopopulated
     *
     * @param profile the profile that we're using to autopopulate the EditTexts
     */
    private void updateProfileViews(Profile profile) {
        if (profile.firstName != null) {
            firstName.setText(profile.firstName);
        } else {
            firstName.setText(placeholderText);
        }

        if (profile.lastName != null) {
            lastName.setText(profile.lastName);
        } else {
            lastName.setText(placeholderText);
        }

        if (profile.dateOfBirth != null) {
            dateOfBirth.setText(profile.dateOfBirth);
        } else {
            dateOfBirth.setText(placeholderText);
        }

        if (profile.address != null && profile.address.line2 != null && profile.address.line1 != null) {
            address.setText(profile.address.line1 + "\n" + profile.address.line2);
        } else if (profile.address != null && profile.address.line1 != null) {
            address.setText(profile.address.line1);
        } else {
            address.setText(placeholderText);
        }

        if (profile.address != null && profile.address.city != null) {
            city.setText(profile.address.city);
        } else {
            city.setText(placeholderText);
        }

        if (profile.address != null && profile.address.stateOrProvince != null) {
            state.setText(profile.address.stateOrProvince);
        } else {
            state.setText(placeholderText);
        }

        if (profile.address != null && profile.address.zipCode != null) {
            zip.setText(profile.address.zipCode);
        } else {
            zip.setText(placeholderText);
        }

        if (profile.phoneNumber != null) {
            phone.setText(profile.phoneNumber);
        } else {
            phone.setText(placeholderText);
        }

        if (profile.email != null) {
            email.setText(profile.email);
        } else {
            email.setText(placeholderText);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.providerName != null) {
            insuranceProvider.setText(profile.insuranceProvider.providerName);
        } else {
            insuranceProvider.setText(placeholderText);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.memberNumber != null) {
            memberId.setText(profile.insuranceProvider.memberNumber);
        } else {
            memberId.setText(placeholderText);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.groupNumber != null) {
            group.setText(profile.insuranceProvider.groupNumber);
        } else {
            group.setText(placeholderText);
        }
    }

    /**
     * Grabs values from EditTexts and updates the current profile with said values
     *
     * @param currentProfile the current profile so far (before looking at the EditTexts)
     * @return the profile with updated information according to the EditTexts
     */
    private Profile getProfileValues(Profile currentProfile) {
        Profile profile = currentProfile;

        if(profile.address == null){
            profile.address = new Address();
        }

        if(profile.insuranceProvider == null){
            profile.insuranceProvider = new InsuranceProvider();
        }

        if (firstName.getText() != null && !firstName.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.firstName = firstName.getText().toString().trim();
        }

        if (lastName.getText() != null && !lastName.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.lastName = lastName.getText().toString().trim();
        }

        if (dateOfBirth.getText() != null && !dateOfBirth.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.dateOfBirth = dateOfBirth.getText().toString().trim();
        }

        if (address.getText() != null && !address.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.line1 = address.getText().toString().trim();
        }

        if (city.getText() != null && !city.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.city = city.getText().toString().trim();
        }

        if (state.getText() != null && !state.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.stateOrProvince = state.getText().toString().trim();
        }

        if (zip.getText() != null && !zip.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.zipCode = zip.getText().toString().trim();
        }

        if (phone.getText() != null && !phone.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.phoneNumber = phone.getText().toString().trim();
        }

        if (insuranceProvider.getText() != null && !insuranceProvider.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.insuranceProvider.providerName = insuranceProvider.getText().toString().trim();
        }

        if (memberId.getText() != null && !memberId.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.insuranceProvider.memberNumber = memberId.getText().toString().trim();
        }

        if (group.getText() != null && !group.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.insuranceProvider.groupNumber = group.getText().toString().trim();
        }

        return profile;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE_EDIT;
    }
}
