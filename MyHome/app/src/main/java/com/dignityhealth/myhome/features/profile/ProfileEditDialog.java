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

public class ProfileEditDialog extends BaseFragment {
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

    public static ProfileEditDialog newInstance() {
        return new ProfileEditDialog();
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

        //Update the profile_view information
        getProfileInfo("Bearer " + AuthManager.getBearerToken());

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
                Toast.makeText(getActivity(), "saving profile...", Toast.LENGTH_SHORT).show();
                //Update Profile....
                //finish fragment
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfileInfo(String bearer) {
        NetworkManager.getInstance().profile(bearer).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
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

    private void updateProfileViews(ProfileResponse profileResponse) {
        String na = "Not Available";

        if (profileResponse.firstName != null) {
            firstName.setText(profileResponse.firstName);
        } else {
            firstName.setText(na);
        }

        if (profileResponse.lastName != null) {
            lastName.setText(profileResponse.lastName);
        } else {
            lastName.setText(na);
        }

        if (profileResponse.dateOfBirth != null) {
            dateOfBirth.setText(profileResponse.dateOfBirth);
        } else {
            dateOfBirth.setText(na);
        }

        if (profileResponse.address != null && profileResponse.address.line2 != null && profileResponse.address.line1 != null) {
            address.setText(profileResponse.address.line1 + "\n" + profileResponse.address.line2);
        } else if (profileResponse.address != null && profileResponse.address.line1 != null) {
            address.setText(profileResponse.address.line1);
        } else {
            address.setText(na);
        }

        if (profileResponse.address != null && profileResponse.address.city != null) {
            city.setText(profileResponse.address.city);
        } else {
            city.setText(na);
        }

        if (profileResponse.address != null && profileResponse.address.stateOrProvince != null) {
            state.setText(profileResponse.address.stateOrProvince);
        } else {
            state.setText(na);
        }

        if (profileResponse.address != null && profileResponse.address.zipCode != null) {
            zip.setText(profileResponse.address.zipCode);
        } else {
            zip.setText(na);
        }

        if (profileResponse.phoneNumber != null) {
            phone.setText(profileResponse.phoneNumber);
        } else {
            phone.setText(na);
        }

        if (profileResponse.email != null) {
            email.setText(profileResponse.email);
        } else {
            email.setText(na);
        }

        if (profileResponse.insuranceProvider != null && profileResponse.insuranceProvider.providerName != null) {
            insuranceProvider.setText(profileResponse.insuranceProvider.providerName);
        } else {
            insuranceProvider.setText(na);
        }

        if (profileResponse.insuranceProvider != null && profileResponse.insuranceProvider.memberNumber != null) {
            memberId.setText(profileResponse.insuranceProvider.memberNumber);
        } else {
            memberId.setText(na);
        }

        if (profileResponse.insuranceProvider != null && profileResponse.insuranceProvider.groupNumber != null) {
            group.setText(profileResponse.insuranceProvider.groupNumber);
        } else {
            group.setText(na);
        }
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE_EDIT;
    }
}
