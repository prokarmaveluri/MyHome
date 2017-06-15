package com.dignityhealth.myhome.features.fad.details.booking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.dignityhealth.myhome.features.profile.Profile;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.utils.CommonUtil;
import com.dignityhealth.myhome.utils.DateUtil;
import com.dignityhealth.myhome.utils.ValidationUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingDialogAdapter extends PagerAdapter {
    private Context context;
    private BookingDialogToolbarInterface bookingDialogToolbarInterface;
    private boolean autoPopulateFromProfile;
    private static int NUM_ITEMS = 2;

    private ViewGroup insuranceLayout;
    private ViewGroup personalLayout;
    private ProgressBar progressBar;

    TextInputLayout caregiverLayout;
    TextInputEditText caregiverName;
    TextInputEditText firstName;
    TextInputEditText lastName;
    TextInputEditText preferredName;
    TextView genderLabel;
    Spinner gender;
    TextView areYouPregnantLabel;
    RadioGroup areYouPregnantGroup;
    TextInputLayout weeksPregnantLayout;
    TextInputEditText weeksPregnant;
    TextInputEditText dateOfBirth;
    TextInputEditText address;
    TextInputEditText address2;
    TextInputEditText city;
    Spinner state;
    TextInputEditText zip;
    TextInputEditText phone;
    TextInputEditText email;
    TextView translatorLabel;
    RadioGroup translatorGroup;
    TextInputLayout translatorLanguageLayout;
    TextInputEditText translatorLanguage;
    TextView assistanceLabel;
    RadioGroup assistanceGroup;
    TextInputEditText reasonForVisit;

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

            dateOfBirth.setText(DateUtil.convertDateToReadable(myCalendar.getTime()));
        }
    };

    private Profile formsProfile;

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface, boolean autoPopulateFromProfile) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = autoPopulateFromProfile;
        if (autoPopulateFromProfile) {
            formsProfile = Profile.copy(ProfileManager.getProfile());
        }
    }

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface, boolean autoPopulateFromProfile, Profile profile) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = autoPopulateFromProfile;
        formsProfile = Profile.copy(profile);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (position) {
            case 0:
                insuranceLayout = (ViewGroup) inflater.inflate(R.layout.book_dialog_insurance, container, false);
                //setupInsuranceView
                container.addView(insuranceLayout, position);

                setupInsurance();

                if (autoPopulateFromProfile) {
                    populateInsuranceLayout();
                }

                return insuranceLayout;

            case 1:
                personalLayout = (ViewGroup) inflater.inflate(R.layout.book_dialog_personal, container, false);
                //setupPersonalView
                container.addView(personalLayout, position);

                setupPersonal();

                if (autoPopulateFromProfile) {
                    populatePersonalLayout();
                }

                return personalLayout;

            default:
                return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeViewAt(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (bookingDialogToolbarInterface != null) {
            bookingDialogToolbarInterface.setToolbarMenu(position);
        }
    }

    private void setupInsurance() {
        insuranceProvider = (TextInputEditText) insuranceLayout.findViewById(R.id.provider);
        memberId = (TextInputEditText) insuranceLayout.findViewById(R.id.id);
        group = (TextInputEditText) insuranceLayout.findViewById(R.id.group);
    }

    private void setupPersonal() {
        caregiverLayout = (TextInputLayout) personalLayout.findViewById(R.id.caregiver_name_layout);
        caregiverName = (TextInputEditText) personalLayout.findViewById(R.id.caregiver_name);
        firstName = (TextInputEditText) personalLayout.findViewById(R.id.first_name);
        lastName = (TextInputEditText) personalLayout.findViewById(R.id.last_name);
        preferredName = (TextInputEditText) personalLayout.findViewById(R.id.preferred_name);
        genderLabel = (TextView) personalLayout.findViewById(R.id.gender_label);
        gender = (Spinner) personalLayout.findViewById(R.id.gender);
        areYouPregnantLabel = (TextView) personalLayout.findViewById(R.id.pregnant_label);
        areYouPregnantGroup = (RadioGroup) personalLayout.findViewById(R.id.group_pregnant);
        weeksPregnantLayout = (TextInputLayout) personalLayout.findViewById(R.id.weeks_layout);
        weeksPregnant = (TextInputEditText) personalLayout.findViewById(R.id.weeks);
        dateOfBirth = (TextInputEditText) personalLayout.findViewById(R.id.dob);
        address = (TextInputEditText) personalLayout.findViewById(R.id.address);
        address2 = (TextInputEditText) personalLayout.findViewById(R.id.address2);
        city = (TextInputEditText) personalLayout.findViewById(R.id.city);
        state = (Spinner) personalLayout.findViewById(R.id.state);
        zip = (TextInputEditText) personalLayout.findViewById(R.id.zip);
        phone = (TextInputEditText) personalLayout.findViewById(R.id.phone);
        email = (TextInputEditText) personalLayout.findViewById(R.id.email);
        translatorLabel = (TextView) personalLayout.findViewById(R.id.translator_label);
        translatorGroup = (RadioGroup) personalLayout.findViewById(R.id.group_translator);
        translatorLanguageLayout = (TextInputLayout) personalLayout.findViewById(R.id.translator_language_layout);
        translatorLanguage = (TextInputEditText) personalLayout.findViewById(R.id.translator_language);
        assistanceLabel = (TextView) personalLayout.findViewById(R.id.assistance_label);
        assistanceGroup = (RadioGroup) personalLayout.findViewById(R.id.group_assistance);
        reasonForVisit = (TextInputEditText) personalLayout.findViewById(R.id.booking_reason);
        progressBar = (ProgressBar) personalLayout.findViewById(R.id.progress_bar);

        updateVisibility(true);

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.hideSoftKeyboard(context, dateOfBirth);
                new DatePickerDialog(context, dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        gender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(context, gender);
                return false;
            }
        });

        state.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CommonUtil.hideSoftKeyboard(context, state);
                return false;
            }
        });

        if (formsProfile.gender != null) {

            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (formsProfile.gender.equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);  //Placeholder is the first item in the array
        }

        if (formsProfile.address != null && formsProfile.address.stateOrProvince != null) {

            //Loop through states until we find a match, then set state spinner selection
            for (int i = 0; i < state.getAdapter().getCount(); i++) {
                if (formsProfile.address.stateOrProvince.equalsIgnoreCase(state.getAdapter().getItem(i).toString())) {
                    state.setSelection(i);
                    break;
                }
            }
        } else {
            state.setSelection(0);  //Placeholder is the first item in the array
        }
    }

    /**
     * Auto-populates Insurance page with values from Profile Singleton
     */
    private void populateInsuranceLayout() {
        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.providerName != null) {
            insuranceProvider.setText(formsProfile.insuranceProvider.providerName);
        }

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.memberNumber != null) {
            memberId.setText(formsProfile.insuranceProvider.memberNumber);
        }

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.groupNumber != null) {
            group.setText(formsProfile.insuranceProvider.groupNumber);
        }
    }

    /**
     * Auto-populates Personal page with values from Profile Singleton
     */
    private void populatePersonalLayout() {
        if (formsProfile.firstName != null) {
            firstName.setText(formsProfile.firstName);
        }

        if (formsProfile.lastName != null) {
            lastName.setText(formsProfile.lastName);
        }

        if (formsProfile.preferredName != null) {
            preferredName.setText(formsProfile.preferredName);
        }

        if (formsProfile.dateOfBirth != null) {
            dateOfBirth.setText(DateUtil.convertUTCtoReadable(formsProfile.dateOfBirth));
        }

        if (formsProfile.address != null && formsProfile.address.line1 != null) {
            address.setText(formsProfile.address.line1);
        }

        if (formsProfile.address != null && formsProfile.address.line2 != null) {
            address2.setText(formsProfile.address.line2);
        }

        if (formsProfile.address != null && formsProfile.address.city != null) {
            city.setText(formsProfile.address.city);
        }

        if (formsProfile.address != null && formsProfile.address.zipCode != null) {
            zip.setText(formsProfile.address.zipCode);
        }

        if (formsProfile.phoneNumber != null) {
            phone.setText(formsProfile.phoneNumber);
        }

        if (formsProfile.email != null) {
            email.setText(formsProfile.email);
        }
    }

    public void setupValidationRules(RegValidationResponse regValidationResponse) {
        updateVisibility(false);

        if (regValidationResponse != null) {
            final List<String> fields = ValidationUtil.getEnabledFields(regValidationResponse);

            if (fields != null) {
                if (fields.contains(ValidationUtil.FIELD_CAREGIVER_NAME) && !autoPopulateFromProfile) {
                    caregiverLayout.setVisibility(View.VISIBLE);
                    caregiverName.setVisibility(View.VISIBLE);
                } else {
                    caregiverLayout.setVisibility(View.GONE);
                    caregiverName.setVisibility(View.GONE);
                }

                firstName.setVisibility(fields.contains(ValidationUtil.FIELD_FIRST_NAME) ? View.VISIBLE : View.GONE);
                lastName.setVisibility(fields.contains(ValidationUtil.FIELD_LAST_NAME) ? View.VISIBLE : View.GONE);
                preferredName.setVisibility(View.VISIBLE);

                //Gender dynamic stuff based on gender AND enabled fields
                if (fields.contains(ValidationUtil.FIELD_GENDER)) {
                    gender.setVisibility(View.VISIBLE);
                    genderLabel.setVisibility(View.VISIBLE);

                    gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 2) {
                                //You're a female

                                if (fields.contains(ValidationUtil.FIELD_PREGNANT)) {
                                    areYouPregnantLabel.setVisibility(View.VISIBLE);
                                    areYouPregnantGroup.setVisibility(View.VISIBLE);
                                }

                            } else {
                                areYouPregnantLabel.setVisibility(View.GONE);
                                areYouPregnantGroup.setVisibility(View.GONE);
                                weeksPregnantLayout.setVisibility(View.GONE);
                                areYouPregnantGroup.check(R.id.radio_not_pregnant);
                                weeksPregnant.setText("");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    areYouPregnantGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                            switch (checkedId) {
                                case R.id.radio_not_pregnant:
                                    weeksPregnantLayout.setVisibility(View.GONE);
                                    break;

                                case R.id.radio_pregnant:
                                    if (fields.contains(ValidationUtil.FIELD_WEEKS_PREGNANT)) {
                                        weeksPregnantLayout.setVisibility(View.VISIBLE);
                                    }
                                    break;
                            }
                        }
                    });
                } else {
                    gender.setVisibility(View.GONE);
                    genderLabel.setVisibility(View.GONE);
                }

                dateOfBirth.setVisibility(fields.contains(ValidationUtil.FIELD_BIRTHDATE) ? View.VISIBLE : View.GONE);
                address.setVisibility(View.VISIBLE);
                address2.setVisibility(View.VISIBLE);
                city.setVisibility(View.VISIBLE);
                state.setVisibility(View.VISIBLE);
                zip.setVisibility(fields.contains(ValidationUtil.FIELD_ZIP) ? View.VISIBLE : View.GONE);
                phone.setVisibility(fields.contains(ValidationUtil.FIELD_PHONE_NUMBER) ? View.VISIBLE : View.GONE);
                email.setVisibility(fields.contains(ValidationUtil.FIELD_EMAIL) ? View.VISIBLE : View.GONE);
                translatorLabel.setVisibility(View.VISIBLE);
                translatorGroup.setVisibility(View.VISIBLE);

                translatorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        switch (checkedId) {
                            case R.id.translator_not_needed:
                                translatorLanguageLayout.setVisibility(View.GONE);
                                translatorLanguage.setVisibility(View.GONE);
                                translatorLanguage.setText("");
                                break;

                            case R.id.translator_needed:
                                translatorLanguageLayout.setVisibility(View.VISIBLE);
                                translatorLanguage.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });

                assistanceLabel.setVisibility(View.VISIBLE);
                assistanceGroup.setVisibility(View.VISIBLE);
                reasonForVisit.setVisibility(fields.contains(ValidationUtil.FIELD_REASON_FOR_VISIT) ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * Updates the visibility of the views depending on the status of loading the validation rules
     *
     * @param isLoading should we show the loading view or not
     */
    private void updateVisibility(boolean isLoading) {
        caregiverLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        caregiverName.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        firstName.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        lastName.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        preferredName.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        genderLabel.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        gender.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        areYouPregnantLabel.setVisibility(View.GONE);
        areYouPregnantGroup.setVisibility(View.GONE);
        weeksPregnantLayout.setVisibility(View.GONE);
        weeksPregnant.setVisibility(View.GONE);
        dateOfBirth.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        address.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        address2.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        city.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        state.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        zip.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        phone.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        email.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        translatorLabel.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        translatorGroup.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        translatorLanguageLayout.setVisibility(View.GONE);
        translatorLanguage.setVisibility(View.GONE);
        assistanceLabel.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        assistanceGroup.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        reasonForVisit.setVisibility(isLoading ? View.GONE : View.VISIBLE);

        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    /**
     * Gets the Profile constructed from the values in the forms
     *
     * @return a Profile that accurately represents the forms filled out
     */
    public Profile getProfile() {
//        if (formsProfile.address == null) {
//            formsProfile.address = new Address();
//        }
//
//        if (formsProfile.insuranceProvider == null) {
//            formsProfile.insuranceProvider = new InsuranceProvider();
//        }

        if (caregiverName.getVisibility() == View.VISIBLE && caregiverName.getText() != null && !caregiverName.getText().toString().isEmpty()) {
            formsProfile.primaryCaregiverName = caregiverName.getText().toString().trim();
        }

        if (firstName.getVisibility() == View.VISIBLE && firstName.getText() != null && !firstName.getText().toString().isEmpty()) {
            formsProfile.firstName = firstName.getText().toString().trim();
        }

        if (lastName.getVisibility() == View.VISIBLE && lastName.getText() != null && !lastName.getText().toString().isEmpty()) {
            formsProfile.lastName = lastName.getText().toString().trim();
        }

        if (preferredName.getVisibility() == View.VISIBLE && preferredName.getText() != null && !preferredName.getText().toString().isEmpty()) {
            formsProfile.preferredName = preferredName.getText().toString().trim();
        }

        if (gender.getVisibility() == View.VISIBLE && !gender.getSelectedItem().toString().isEmpty()) {
            formsProfile.gender = gender.getSelectedItem().toString().trim();
        }

        if (areYouPregnantGroup.getVisibility() == View.VISIBLE) {
            if (areYouPregnantGroup.getCheckedRadioButtonId() == R.id.radio_pregnant) {
                formsProfile.isPregnant = true;
            } else {
                formsProfile.isPregnant = false;
            }
        }

        if (weeksPregnant.getVisibility() == View.VISIBLE && weeksPregnant.getText() != null && !weeksPregnant.getText().toString().isEmpty()) {
            formsProfile.weeksPregnant = weeksPregnant.getText().toString().trim();
        }

        if (dateOfBirth.getVisibility() == View.VISIBLE && dateOfBirth.getText() != null && !dateOfBirth.getText().toString().isEmpty()) {
            formsProfile.dateOfBirth = DateUtil.convertReadableToUTC(dateOfBirth.getText().toString().trim());
        }

        if (address.getVisibility() == View.VISIBLE && address.getText() != null && !address.getText().toString().isEmpty()) {
            formsProfile.address.line1 = address.getText().toString().trim();
        }

        if (address2.getVisibility() == View.VISIBLE && address2.getText() != null && !address2.getText().toString().isEmpty()) {
            formsProfile.address.line2 = address2.getText().toString().trim();
        }

        if (city.getVisibility() == View.VISIBLE && city.getText() != null && !city.getText().toString().isEmpty()) {
            formsProfile.address.city = city.getText().toString().trim();
        }

        if (state.getVisibility() == View.VISIBLE && !state.getSelectedItem().toString().isEmpty() && state.getSelectedItemPosition() != 0) {
            formsProfile.address.stateOrProvince = state.getSelectedItem().toString().trim();
        }

        if (zip.getVisibility() == View.VISIBLE && zip.getText() != null && !zip.getText().toString().isEmpty()) {
            formsProfile.address.zipCode = zip.getText().toString().trim();
        }

        //Make sure to strip phone number of any non-digits
        if (phone.getVisibility() == View.VISIBLE && phone.getText() != null && !phone.getText().toString().isEmpty()) {
            formsProfile.phoneNumber = CommonUtil.stripPhoneNumber(phone.getText().toString().trim());
        }

        if (insuranceProvider.getVisibility() == View.VISIBLE && insuranceProvider.getText() != null && !insuranceProvider.getText().toString().isEmpty()) {
            formsProfile.insuranceProvider.providerName = insuranceProvider.getText().toString().trim();
        }

        if (memberId.getVisibility() == View.VISIBLE && memberId.getText() != null && !memberId.getText().toString().isEmpty()) {
            formsProfile.insuranceProvider.memberNumber = memberId.getText().toString().trim();
        }

        if (group.getVisibility() == View.VISIBLE && group.getText() != null && !group.getText().toString().isEmpty()) {
            formsProfile.insuranceProvider.groupNumber = group.getText().toString().trim();
        }

        if (translatorGroup.getVisibility() == View.VISIBLE) {
            if (translatorGroup.getCheckedRadioButtonId() == R.id.translator_needed) {
                formsProfile.translationNeeded = true;
            } else {
                formsProfile.translationNeeded = false;
            }
        }

        if (translatorLanguage.getVisibility() == View.VISIBLE && translatorLanguage.getText() != null && !translatorLanguage.getText().toString().isEmpty()) {
            formsProfile.translatorLanguage = translatorLanguage.getText().toString().trim();
        }

        if (assistanceGroup.getVisibility() == View.VISIBLE) {
            if (assistanceGroup.getCheckedRadioButtonId() == R.id.assistance_needed) {
                formsProfile.assistanceNeeded = true;
            } else {
                formsProfile.assistanceNeeded = false;
            }
        }

        if (reasonForVisit.getVisibility() == View.VISIBLE && reasonForVisit.getText() != null && !reasonForVisit.getText().toString().isEmpty()) {
            formsProfile.reasonForVisit = reasonForVisit.getText().toString().trim();
        }

        return formsProfile;
    }


    public boolean validateForm(int page){
        boolean isValid = true;

        if(page == 0){
            //Insurance Page

            if(insuranceProvider.getText().toString().isEmpty()){
                isValid = false;
                insuranceProvider.setError("Insurance Provider Name Required");
            } else {
                insuranceProvider.setError(null);
            }

            if(memberId.getText().toString().isEmpty()){
                isValid = false;
                memberId.setError("Member ID Required");
            } else {
                memberId.setError(null);
            }

            if(group.getText().toString().isEmpty()){
                isValid = false;
                group.setError("Group ID Required");
            } else {
                group.setError(null);
            }

        } else if (page == 1){
            //Personal Page

            if(caregiverName.getVisibility() == View.VISIBLE && caregiverName.getText().toString().isEmpty()){
                isValid = false;
                caregiverName.setError("Caregiver Name Required");
            }

            if(firstName.getVisibility() == View.VISIBLE && firstName.getText().toString().isEmpty()){
                isValid = false;
                firstName.setError("First Name Required");
            }

            if(lastName.getVisibility() == View.VISIBLE && lastName.getText().toString().isEmpty()){
                isValid = false;
                lastName.setError("Last Name Required");
            }

            if(dateOfBirth.getVisibility() == View.VISIBLE && dateOfBirth.getText().toString().isEmpty()){
                isValid = false;
                dateOfBirth.setError("Date of Birth Required");
            }

            if(phone.getVisibility() == View.VISIBLE && phone.getText().toString().isEmpty()){
                isValid = false;
                phone.setError("Phone Number Required");
            }

            if(email.getVisibility() == View.VISIBLE && email.getText().toString().isEmpty()){
                isValid = false;
                email.setError("Email Required");
            }

            if(email.getVisibility() == View.VISIBLE && !CommonUtil.isValidEmail(email.getText().toString())){
                isValid = false;
                email.setError("Email Invalid");
            }

            if(reasonForVisit.getVisibility() == View.VISIBLE && reasonForVisit.getText().toString().isEmpty()){
                isValid = false;
                reasonForVisit.setError("Reason For Visit Required");
            }
        }

        return isValid;
    }
}