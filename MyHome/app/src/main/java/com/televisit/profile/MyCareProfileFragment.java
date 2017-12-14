package com.televisit.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.consumer.ConsumerUpdate;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.app.NavigationActivity;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.EnviHandler;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.televisit.AwsManager;
import com.televisit.AwsNetworkManager;
import com.televisit.interfaces.AwsUpdateConsumer;

import timber.log.Timber;

/**
 * Created by kwelsh on 4/26/17.
 */

public class MyCareProfileFragment extends BaseFragment implements AwsUpdateConsumer {
    public static final String MY_PROFILE_TAG = "my_profile_tag";

    View profileView;
    TextView welcomeText;
    TextInputLayout firstNameLayout;
    TextInputEditText firstName;
    TextInputLayout lastNameLayout;
    TextInputEditText lastName;
    Spinner gender;
    TextInputLayout dateOfBirthLayout;
    TextInputEditText dateOfBirth;
    TextInputEditText address;
    TextInputEditText address2;
    TextInputEditText city;
    Spinner state;
    TextInputLayout zipLayout;
    TextInputEditText zip;
    TextInputLayout phoneLayout;
    TextInputEditText phone;
    TextInputLayout emailLayout;
    TextView email;
    TextView genderLabel;

    ProgressBar progress;

    public static MyCareProfileFragment newInstance() {
        return new MyCareProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.my_profile, container, false);
        ((NavigationActivity) getActivity()).setActionBarTitle(getString(R.string.my_personal_information));

        welcomeText = (TextView) profileView.findViewById(R.id.welcome_text);
        firstNameLayout = (TextInputLayout) profileView.findViewById(R.id.first_name_layout);
        firstName = (TextInputEditText) profileView.findViewById(R.id.first_name);
        lastNameLayout = (TextInputLayout) profileView.findViewById(R.id.last_name_layout);
        lastName = (TextInputEditText) profileView.findViewById(R.id.last_name);
        genderLabel = (TextView) profileView.findViewById(R.id.gender_label);
        gender = (Spinner) profileView.findViewById(R.id.gender);
        dateOfBirthLayout = (TextInputLayout) profileView.findViewById(R.id.dob_layout);
        dateOfBirth = (TextInputEditText) profileView.findViewById(R.id.dob);
        address = (TextInputEditText) profileView.findViewById(R.id.address);
        address2 = (TextInputEditText) profileView.findViewById(R.id.address2);
        city = (TextInputEditText) profileView.findViewById(R.id.city);
        state = (Spinner) profileView.findViewById(R.id.state);
        zipLayout = (TextInputLayout) profileView.findViewById(R.id.zip_layout);
        zip = (TextInputEditText) profileView.findViewById(R.id.zip);
        phoneLayout = (TextInputLayout) profileView.findViewById(R.id.phone_layout);
        phone = (TextInputEditText) profileView.findViewById(R.id.phone);
        email = (TextView) profileView.findViewById(R.id.email);
        progress = (ProgressBar) profileView.findViewById(R.id.profile_edit_progress);

        dateOfBirth.addTextChangedListener(new PhoneAndDOBFormatter(dateOfBirth, PhoneAndDOBFormatter.FormatterType.DOB));

        phone.addTextChangedListener(new PhoneAndDOBFormatter(phone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER_DOTS));

        gender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        updateConsumerViews(AwsManager.getInstance().getPatient());

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
                CommonUtil.hideSoftKeyboard(getActivity());
                if (isValidConsumer()) {
                    sendUpdatedConsumer();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUpdatedConsumer() {
        progress.setVisibility(View.VISIBLE);

        AwsManager.getInstance().getAWSDK().getNewAddress();

        ConsumerUpdate update = AwsManager.getInstance().getAWSDK().getConsumerManager().getNewConsumerUpdate(AwsManager.getInstance().getPatient());

        //TODO change this once login actually works
        if (EnviHandler.AWSDK_URL.equals(EnviHandler.AmWellEnvType.DEV)) {
            update.setPassword("Pass123*");
        } else {
            update.setPassword("Password1");
        }

        //Comment this back in once login works
        update.setEmail(email.getText().toString().trim());
        update.setFirstName(firstName.getText().toString().trim());
        update.setLastName(lastName.getText().toString().trim());
        update.setGender(gender.getSelectedItem().toString().trim());
        update.setPhone(CommonUtil.stripPhoneNumber(phone.getText().toString().trim()));
        update.setDob(DateUtil.convertReadabletoDob(dateOfBirth.getText().toString().trim()));

        com.americanwell.sdk.entity.Address userAddress = AwsManager.getInstance().getAWSDK().getNewAddress();

        if (address.getText() != null) {
            userAddress.setAddress1(address.getText().toString());
        }

        if (address2.getText() != null) {
            userAddress.setAddress2(address2.getText().toString());
        }

        if (city.getText() != null) {
            userAddress.setCity(city.getText().toString());
        }

        if (state.getSelectedItemPosition() != 0) {
            userAddress.setState(AwsManager.getInstance().getState(state.getSelectedItem().toString()));
        }

        if (zip.getText() != null) {
            userAddress.setZipCode(zip.getText().toString());
        }

        update.setAddress(userAddress);

        AwsNetworkManager.getInstance().updateConsumer(update, this);
    }

    /**
     * Updates the view on screen to have the proper consumer values autopopulated
     *
     * @param consumer the consumer that we're using to autopopulate the EditTexts
     */
    private void updateConsumerViews(Consumer consumer) {
        if (consumer.getFirstName() != null) {
            welcomeText.setText(getString(R.string.mcn_welcome_text_variable, consumer.getFirstName()));
            firstName.setText(consumer.getFirstName());
        } else {
            welcomeText.setText(getString(R.string.mcn_welcome_text));
        }

        if (consumer.getLastName() != null) {
            lastName.setText(consumer.getLastName());
        }

        if (consumer.getGender() != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (consumer.getGender().equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
        }

        if (consumer.getDob() != null) {
            dateOfBirth.setText(DateUtil.convertDobtoReadable(consumer.getDob()));
        }

        if (consumer.getAddress() != null && consumer.getAddress().getAddress1() != null) {
            address.setText(consumer.getAddress().getAddress1());
        }

        if (consumer.getAddress() != null && consumer.getAddress().getAddress2() != null) {
            address2.setText(consumer.getAddress().getAddress2());
        }

        if (consumer.getAddress() != null && consumer.getAddress().getCity() != null) {
            city.setText(consumer.getAddress().getCity());
        }

        if (consumer.getAddress() != null && consumer.getAddress().getState() != null) {

            //Loop through states until we find a match, then set state spinner selection
            for (int i = 0; i < state.getAdapter().getCount(); i++) {
                if (consumer.getAddress().getState().getCode().equalsIgnoreCase(state.getAdapter().getItem(i).toString())) {
                    state.setSelection(i);
                    break;
                }
            }
        } else {
            state.setSelection(0);  //Placeholder is the first item in the array
        }

        if (consumer.getAddress() != null && consumer.getAddress().getZipCode() != null) {
            zip.setText(consumer.getAddress().getZipCode().trim());
        }

        if (consumer.getPhone() != null) {
            //phone.setText(profile.phoneNumber.replaceAll("\\.", "-"));
            phone.setText(CommonUtil.constructPhoneNumberDots(consumer.getPhone()));
        }

        if (consumer.getEmail() != null) {
            email.setText(consumer.getEmail());
        }
    }

    private boolean isValidConsumer() {
        boolean isValid = true;

        if (firstNameLayout.getVisibility() == View.VISIBLE && firstName.getText().toString().trim().isEmpty()) {
            isValid = false;
            firstNameLayout.setError(getString(R.string.first_name_required));
        } else {
            firstNameLayout.setError(null);
        }

        if (lastNameLayout.getVisibility() == View.VISIBLE && lastName.getText().toString().trim().isEmpty()) {
            isValid = false;
            lastNameLayout.setError(getString(R.string.last_name_required));
        } else {
            lastNameLayout.setError(null);
        }

        if (dateOfBirth.getVisibility() == View.VISIBLE && (!DateUtil.isValidDateOfBirth(dateOfBirth.getText().toString().trim())) &&
                !dateOfBirth.getText().toString().trim().isEmpty()) {
            isValid = false;
            dateOfBirthLayout.setError(getString(R.string.date_of_birth_invalid));
        } else {
            dateOfBirthLayout.setError(null);
        }

        if (zipLayout.getVisibility() == View.VISIBLE && (zip.getText().toString().trim().length() != 0 &&
                zip.getText().toString().trim().length() != 5)) {
            isValid = false;
            zipLayout.setError(getString(R.string.zip_invalid));
        } else {
            zipLayout.setError(null);
        }

        if (phoneLayout.getVisibility() == View.VISIBLE && phone.getText().toString().trim().length() != 0 &&
                !CommonUtil.isValidMobile(phone.getText().toString())) {
            isValid = false;
            phoneLayout.setError(getString(R.string.phone_number_invalid));
        } else {
            phoneLayout.setError(null);
        }

        return isValid;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.MY_CARE_PROFILE;
    }

    @Override
    public void updateConsumerComplete(Consumer consumer) {
        AwsManager.getInstance().setPatient(consumer);
        AwsManager.getInstance().setConsumer(consumer);

        Toast.makeText(getActivity(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void updateConsumerFailed(String errorMessage) {
        Timber.e(errorMessage);
        Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }
}
