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
                ProfileResponse currentProfile = ProfileManager.getProfile();
                sendUpdatedProfile("Bearer " + AuthManager.getBearerToken(), getProfileValues(currentProfile));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfileInfo(String bearer) {
        NetworkManager.getInstance().getProfile(bearer).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    ProfileManager.setProfile(response.body());
                    updateProfileViews(response.body());
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
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
    private void sendUpdatedProfile(String bearer, ProfileResponse updatedProfile) {
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
     * @param profileResponse the profile that we're using to autopopulate the EditTexts
     */
    private void updateProfileViews(ProfileResponse profileResponse) {
        if (profileResponse.firstName != null) {
            firstName.setText(profileResponse.firstName);
        } else {
            firstName.setText(placeholderText);
        }

        if (profileResponse.lastName != null) {
            lastName.setText(profileResponse.lastName);
        } else {
            lastName.setText(placeholderText);
        }

        if (profileResponse.dateOfBirth != null) {
            dateOfBirth.setText(profileResponse.dateOfBirth);
        } else {
            dateOfBirth.setText(placeholderText);
        }

        if (profileResponse.address != null && profileResponse.address.line2 != null && profileResponse.address.line1 != null) {
            address.setText(profileResponse.address.line1 + "\n" + profileResponse.address.line2);
        } else if (profileResponse.address != null && profileResponse.address.line1 != null) {
            address.setText(profileResponse.address.line1);
        } else {
            address.setText(placeholderText);
        }

        if (profileResponse.address != null && profileResponse.address.city != null) {
            city.setText(profileResponse.address.city);
        } else {
            city.setText(placeholderText);
        }

        if (profileResponse.address != null && profileResponse.address.stateOrProvince != null) {
            state.setText(profileResponse.address.stateOrProvince);
        } else {
            state.setText(placeholderText);
        }

        if (profileResponse.address != null && profileResponse.address.zipCode != null) {
            zip.setText(profileResponse.address.zipCode);
        } else {
            zip.setText(placeholderText);
        }

        if (profileResponse.phoneNumber != null) {
            phone.setText(profileResponse.phoneNumber);
        } else {
            phone.setText(placeholderText);
        }

        if (profileResponse.email != null) {
            email.setText(profileResponse.email);
        } else {
            email.setText(placeholderText);
        }

        if (profileResponse.insuranceProvider != null && profileResponse.insuranceProvider.providerName != null) {
            insuranceProvider.setText(profileResponse.insuranceProvider.providerName);
        } else {
            insuranceProvider.setText(placeholderText);
        }

        if (profileResponse.insuranceProvider != null && profileResponse.insuranceProvider.memberNumber != null) {
            memberId.setText(profileResponse.insuranceProvider.memberNumber);
        } else {
            memberId.setText(placeholderText);
        }

        if (profileResponse.insuranceProvider != null && profileResponse.insuranceProvider.groupNumber != null) {
            group.setText(profileResponse.insuranceProvider.groupNumber);
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
    private ProfileResponse getProfileValues(ProfileResponse currentProfile) {
        ProfileResponse profile = currentProfile;

        if(profile.address == null){
            profile.address = new Address();
        }

        if(profile.insuranceProvider == null){
            profile.insuranceProvider = new InsuranceProvider();
        }

        if (firstName.getText() != null && firstName.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.firstName = firstName.getText().toString();
        }

        if (lastName.getText() != null && lastName.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.lastName = lastName.getText().toString();
        }

        if (dateOfBirth.getText() != null && dateOfBirth.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.dateOfBirth = dateOfBirth.getText().toString();
        }

        if (address.getText() != null && address.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.line1 = address.getText().toString();
        }

        if (city.getText() != null && city.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.city = city.getText().toString();
        }

        if (state.getText() != null && state.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.stateOrProvince = state.getText().toString();
        }

        if (zip.getText() != null && zip.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.address.zipCode = zip.getText().toString();
        }

        if (phone.getText() != null && phone.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.phoneNumber = phone.getText().toString();
        }

        if (insuranceProvider.getText() != null && insuranceProvider.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.insuranceProvider.providerName = insuranceProvider.getText().toString();
        }

        if (memberId.getText() != null && memberId.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.insuranceProvider.memberNumber = memberId.getText().toString();
        }

        if (group.getText() != null && group.getText().toString().equalsIgnoreCase(placeholderText)) {
            profile.insuranceProvider.groupNumber = group.getText().toString();
        }

        return profile;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE_EDIT;
    }
}
