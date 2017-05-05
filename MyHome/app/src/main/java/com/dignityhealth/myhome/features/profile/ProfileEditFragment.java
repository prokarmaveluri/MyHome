package com.dignityhealth.myhome.features.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.Constants;

import java.text.ParseException;
import java.util.Calendar;

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
    TextInputEditText firstName;
    TextInputEditText lastName;
    TextInputEditText preferredName;
    Spinner gender;
    TextInputEditText dateOfBirth;
    TextInputEditText address;
    TextInputEditText address2;
    TextInputEditText city;
    Spinner state;
    TextInputEditText zip;
    EditText phone1;
    EditText phone2;
    EditText phone3;
    TextView email;

    TextInputEditText insuranceProvider;
    TextInputEditText memberId;
    TextInputEditText group;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            dateOfBirth.setText(Constants.SIMPLE_DATE_FORMAT.format(myCalendar.getTime()));
        }
    };

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

        firstName = (TextInputEditText) profileView.findViewById(R.id.first_name);
        lastName = (TextInputEditText) profileView.findViewById(R.id.last_name);
        preferredName = (TextInputEditText) profileView.findViewById(R.id.preferred_name);
        gender = (Spinner) profileView.findViewById(R.id.gender);
        dateOfBirth = (TextInputEditText) profileView.findViewById(R.id.dob);
        address = (TextInputEditText) profileView.findViewById(R.id.address);
        address2 = (TextInputEditText) profileView.findViewById(R.id.address2);
        city = (TextInputEditText) profileView.findViewById(R.id.city);
        state = (Spinner) profileView.findViewById(R.id.state);
        zip = (TextInputEditText) profileView.findViewById(R.id.zip);
        phone1 = (EditText) profileView.findViewById(R.id.phone1);
        phone2 = (EditText) profileView.findViewById(R.id.phone2);
        phone3 = (EditText) profileView.findViewById(R.id.phone3);
        email = (TextView) profileView.findViewById(R.id.email);

        insuranceProvider = (TextInputEditText) profileView.findViewById(R.id.provider);
        memberId = (TextInputEditText) profileView.findViewById(R.id.id);
        group = (TextInputEditText) profileView.findViewById(R.id.group);

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateSetListener,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
                if (!CommonUtil.validPhoneNumber(phone1.getText().toString(), phone2.getText().toString(), phone3.getText().toString())) {
                    Toast.makeText(getActivity(), "Phone Number not valid.", Toast.LENGTH_LONG).show();
                } else {
                    Profile currentProfile = ProfileManager.getProfile();
                    sendUpdatedProfile("Bearer " + AuthManager.getBearerToken(), getProfileValues(currentProfile));
                }
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
        }

        if (profile.lastName != null) {
            lastName.setText(profile.lastName);
        }

        if (profile.preferredName != null) {
            preferredName.setText(profile.preferredName);
        }

        if (profile.gender != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (profile.gender.equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
        }

        if (profile.dateOfBirth != null) {
            try {
                myCalendar.setTime(Constants.SIMPLE_DATE_FORMAT_UTC.parse(profile.dateOfBirth));
                dateOfBirth.setText(Constants.SIMPLE_DATE_FORMAT.format(myCalendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (profile.address != null && profile.address.line1 != null) {
            address.setText(profile.address.line1);
        }

        if (profile.address != null && profile.address.line2 != null) {
            address2.setText(profile.address.line2);
        }

        if (profile.address != null && profile.address.city != null) {
            city.setText(profile.address.city);
        }

        if (profile.address != null && profile.address.stateOrProvince != null) {

            //Loop through states until we find a match, then set state spinner selection
            for (int i = 0; i < state.getAdapter().getCount(); i++) {
                if (profile.address.stateOrProvince.equalsIgnoreCase(state.getAdapter().getItem(i).toString())) {
                    state.setSelection(i);
                    break;
                }
            }
        } else {
            state.setSelection(0);  //Placeholder is the first item in the array
        }

        if (profile.address != null && profile.address.zipCode != null) {
            zip.setText(profile.address.zipCode);
        }

        if (profile.phoneNumber != null) {
            int length = profile.phoneNumber.length();

            if (length == 10) {
                phone3.setText(profile.phoneNumber.substring(length - 4, length));
                phone2.setText(profile.phoneNumber.substring(length - 7, length - 4));
                phone1.setText(profile.phoneNumber.substring(length - 10, length - 7));
            } else if (length == 7) {
                phone3.setText(profile.phoneNumber.substring(length - 4, length));
                phone2.setText(profile.phoneNumber.substring(length - 7, length - 4));
                phone1.setText("");
            }
        }

        if (profile.email != null) {
            email.setText(profile.email);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.providerName != null) {
            insuranceProvider.setText(profile.insuranceProvider.providerName);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.memberNumber != null) {
            memberId.setText(profile.insuranceProvider.memberNumber);
        }

        if (profile.insuranceProvider != null && profile.insuranceProvider.groupNumber != null) {
            group.setText(profile.insuranceProvider.groupNumber);
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

        if (profile.address == null) {
            profile.address = new Address();
        }

        if (profile.insuranceProvider == null) {
            profile.insuranceProvider = new InsuranceProvider();
        }

        if (firstName.getText() != null && !firstName.getText().toString().isEmpty()) {
            profile.firstName = firstName.getText().toString().trim();
        }

        if (lastName.getText() != null && !lastName.getText().toString().isEmpty()) {
            profile.lastName = lastName.getText().toString().trim();
        }

        if (preferredName.getText() != null && !preferredName.getText().toString().isEmpty()) {
            profile.preferredName = preferredName.getText().toString().trim();
        }

        if (!gender.getSelectedItem().toString().isEmpty()) {
            profile.gender = gender.getSelectedItem().toString().trim();
        }

        if (dateOfBirth.getText() != null && !dateOfBirth.getText().toString().isEmpty()) {
            profile.dateOfBirth = dateOfBirth.getText().toString().trim();
        }

        if (address.getText() != null && !address.getText().toString().isEmpty()) {
            profile.address.line1 = address.getText().toString().trim();
        }

        if (address2.getText() != null && !address2.getText().toString().isEmpty()) {
            profile.address.line2 = address2.getText().toString().trim();
        }

        if (city.getText() != null && !city.getText().toString().isEmpty()) {
            profile.address.city = city.getText().toString().trim();
        }

        if (!state.getSelectedItem().toString().isEmpty()) {
            profile.address.stateOrProvince = state.getSelectedItem().toString().trim();
        }

        if (zip.getText() != null && !zip.getText().toString().isEmpty()) {
            profile.address.zipCode = zip.getText().toString().trim();
        }

        //TODO figure out hyphen for phone...
//        if (CommonUtil.validPhoneNumber(phone1.getText().toString(), phone2.getText().toString(), phone3.getText().toString())
//                && !phone1.getText().toString().equalsIgnoreCase(placeholderText)) {
//            profile.phoneNumber = phone1.getText().toString().trim() + phone2.getText().toString().trim() + phone3.getText().toString().trim();
//        }

        if (insuranceProvider.getText() != null && !insuranceProvider.getText().toString().isEmpty()) {
            profile.insuranceProvider.providerName = insuranceProvider.getText().toString().trim();
        }

        if (memberId.getText() != null && !memberId.getText().toString().isEmpty()) {
            profile.insuranceProvider.memberNumber = memberId.getText().toString().trim();
        }

        if (group.getText() != null && !group.getText().toString().isEmpty()) {
            profile.insuranceProvider.groupNumber = group.getText().toString().trim();
        }

        return profile;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE_EDIT;
    }
}
