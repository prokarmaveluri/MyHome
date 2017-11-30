package com.prokarma.myhome.features.fad.details.booking;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegAttributes;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegIncluded;
import com.prokarma.myhome.features.fad.details.booking.req.validation.RegValidationResponse;
import com.prokarma.myhome.features.profile.Address;
import com.prokarma.myhome.features.profile.InsuranceProvider;
import com.prokarma.myhome.features.profile.Profile;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.DateUtil;
import com.prokarma.myhome.utils.PhoneAndDOBFormatter;
import com.prokarma.myhome.utils.ValidationUtil;

import java.util.List;

import timber.log.Timber;

/**
 * Created by kwelsh on 5/18/17.
 */

public class BookingDialogAdapter extends PagerAdapter {
    private Context context;
    private BookingDialogToolbarInterface bookingDialogToolbarInterface;
    private boolean autoPopulateFromProfile;
    private boolean autoPopulateInsurnacePlan;
    private static int NUM_ITEMS = 2;

    private ViewGroup insuranceLayout;
    private ViewGroup personalLayout;
    private ProgressBar progressBarInsurance;

    ScrollView scrollView;
    TextInputLayout caregiverLayout;
    TextInputEditText caregiverName;
    TextInputLayout firstNameLayout;
    TextInputEditText firstName;
    TextInputLayout lastNameLayout;
    TextInputEditText lastName;
    TextInputLayout preferredNameLayout;
    TextInputEditText preferredName;
    TextView genderLabel;
    Spinner gender;
    TextView areYouPregnantLabel;
    RadioGroup areYouPregnantGroup;
    TextInputLayout weeksPregnantLayout;
    TextInputEditText weeksPregnant;
    TextInputLayout dateOfBirthLayout;
    TextInputEditText dateOfBirth;
    TextInputLayout addressLayout;
    TextInputEditText address;
    TextInputLayout address2Layout;
    TextInputEditText address2;
    TextInputLayout cityLayout;
    TextInputEditText city;
    Spinner state;
    TextInputLayout zipLayout;
    TextInputEditText zip;
    LinearLayout zipPhoneLayout;
    TextInputLayout phoneLayout;
    TextInputEditText phone;
    TextInputLayout emailLayout;
    TextInputEditText email;
    TextView translatorLabel;
    RadioGroup translatorGroup;
    TextInputLayout translatorLanguageLayout;
    TextInputEditText translatorLanguage;
    TextView assistanceLabel;
    RadioGroup assistanceGroup;
    TextInputLayout reasonForVisitLayout;
    TextInputEditText reasonForVisit;

    TextView planLabel;
    Spinner plan;
    TextInputLayout memberIdLayout;
    TextInputEditText memberId;
    TextInputLayout groupLayout;
    TextInputEditText group;
    TextInputLayout insurancePhoneLayout;
    TextInputEditText insurancePhone;

    private Profile formsProfile;

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = false;
    }

    public BookingDialogAdapter(Context context, BookingDialogToolbarInterface bookingDialogToolbarInterface, boolean autoPopulateFromProfile, boolean autoPopulateInsurnacePlan, Profile profile) {
        this.context = context;
        this.bookingDialogToolbarInterface = bookingDialogToolbarInterface;
        this.autoPopulateFromProfile = autoPopulateFromProfile;
        this.autoPopulateInsurnacePlan = autoPopulateInsurnacePlan;
        formsProfile = Profile.copy(profile);
    }

    @Nullable
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

                if (!BookingManager.isBookingForMe() && ProfileManager.getProfile().firstName != null && ProfileManager.getProfile().lastName != null) {
                    caregiverName.setText(ProfileManager.getProfile().firstName + " " + ProfileManager.getProfile().lastName);
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
        planLabel = (TextView) insuranceLayout.findViewById(R.id.provider_plan_label);
        plan = (Spinner) insuranceLayout.findViewById(R.id.provider_plan);
        memberIdLayout = (TextInputLayout) insuranceLayout.findViewById(R.id.id_layout);
        memberId = (TextInputEditText) insuranceLayout.findViewById(R.id.id);
        groupLayout = (TextInputLayout) insuranceLayout.findViewById(R.id.group_layout);
        group = (TextInputEditText) insuranceLayout.findViewById(R.id.group);
        insurancePhoneLayout = (TextInputLayout) insuranceLayout.findViewById(R.id.insurance_phone_layout);
        insurancePhone = (TextInputEditText) insuranceLayout.findViewById(R.id.insurance_phone);
        progressBarInsurance = (ProgressBar) insuranceLayout.findViewById(R.id.progress_bar_insurance);

        updateVisibility(true);

        insurancePhone.addTextChangedListener(new PhoneAndDOBFormatter(insurancePhone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER_DOTS));
    }

    private void setupPersonal() {
        scrollView = (ScrollView) personalLayout.findViewById(R.id.dialog_personal_scoll_view);
        caregiverLayout = (TextInputLayout) personalLayout.findViewById(R.id.caregiver_name_layout);
        caregiverName = (TextInputEditText) personalLayout.findViewById(R.id.caregiver_name);
        firstNameLayout = (TextInputLayout) personalLayout.findViewById(R.id.first_name_layout);
        firstName = (TextInputEditText) personalLayout.findViewById(R.id.first_name);
        lastNameLayout = (TextInputLayout) personalLayout.findViewById(R.id.last_name_layout);
        lastName = (TextInputEditText) personalLayout.findViewById(R.id.last_name);
        preferredNameLayout = (TextInputLayout) personalLayout.findViewById(R.id.preferred_name_layout);
        preferredName = (TextInputEditText) personalLayout.findViewById(R.id.preferred_name);
        genderLabel = (TextView) personalLayout.findViewById(R.id.gender_label);
        gender = (Spinner) personalLayout.findViewById(R.id.gender);
        areYouPregnantLabel = (TextView) personalLayout.findViewById(R.id.pregnant_label);
        areYouPregnantGroup = (RadioGroup) personalLayout.findViewById(R.id.group_pregnant);
        weeksPregnantLayout = (TextInputLayout) personalLayout.findViewById(R.id.weeks_layout);
        weeksPregnant = (TextInputEditText) personalLayout.findViewById(R.id.weeks);
        dateOfBirthLayout = (TextInputLayout) personalLayout.findViewById(R.id.dob_layout);
        dateOfBirth = (TextInputEditText) personalLayout.findViewById(R.id.dob);
        addressLayout = (TextInputLayout) personalLayout.findViewById(R.id.address_layout);
        address = (TextInputEditText) personalLayout.findViewById(R.id.address);
        address2Layout = (TextInputLayout) personalLayout.findViewById(R.id.address2_layout);
        address2 = (TextInputEditText) personalLayout.findViewById(R.id.address2);
        cityLayout = (TextInputLayout) personalLayout.findViewById(R.id.city_layout);
        city = (TextInputEditText) personalLayout.findViewById(R.id.city);
        state = (Spinner) personalLayout.findViewById(R.id.state);
        zipLayout = (TextInputLayout) personalLayout.findViewById(R.id.zip_layout);
        zip = (TextInputEditText) personalLayout.findViewById(R.id.zip);
        zipPhoneLayout = (LinearLayout) personalLayout.findViewById(R.id.zip_phone_layout);
        phoneLayout = (TextInputLayout) personalLayout.findViewById(R.id.phone_layout);
        phone = (TextInputEditText) personalLayout.findViewById(R.id.phone);
        emailLayout = (TextInputLayout) personalLayout.findViewById(R.id.email_layout);
        email = (TextInputEditText) personalLayout.findViewById(R.id.email);
        translatorLabel = (TextView) personalLayout.findViewById(R.id.translator_label);
        translatorGroup = (RadioGroup) personalLayout.findViewById(R.id.group_translator);
        translatorLanguageLayout = (TextInputLayout) personalLayout.findViewById(R.id.translator_language_layout);
        translatorLanguage = (TextInputEditText) personalLayout.findViewById(R.id.translator_language);
        assistanceLabel = (TextView) personalLayout.findViewById(R.id.assistance_label);
        assistanceGroup = (RadioGroup) personalLayout.findViewById(R.id.group_assistance);
        reasonForVisitLayout = (TextInputLayout) personalLayout.findViewById(R.id.booking_reason_layout);
        reasonForVisit = (TextInputEditText) personalLayout.findViewById(R.id.booking_reason);
        reasonForVisit.setHint(context.getString(R.string.booking_reason_hint));

        dateOfBirth.addTextChangedListener(new PhoneAndDOBFormatter(dateOfBirth, PhoneAndDOBFormatter.FormatterType.DOB));

        phone.addTextChangedListener(new PhoneAndDOBFormatter(phone, PhoneAndDOBFormatter.FormatterType.PHONE_NUMBER_DOTS));

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

        translatorGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.translator_not_needed:
                        translatorLanguageLayout.setVisibility(View.GONE);
                        translatorLanguage.setText("");
                        break;

                    case R.id.translator_needed:
                        translatorLanguageLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        //Allows reasonForVisit to scroll up & down so you can see all of the text
        reasonForVisit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.booking_reason) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Auto-populates Insurance page with values from Profile Singleton
     */
    private void populateInsuranceLayout() {
        if (autoPopulateInsurnacePlan && formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.insurancePlan != null && plan.getAdapter() != null) {
            for (int i = 0; i < plan.getAdapter().getCount(); i++) {
                if (formsProfile.insuranceProvider.insurancePlan.equalsIgnoreCase(((RegIncluded) plan.getAdapter().getItem(i)).getAttributes().getName())) {
                    plan.setSelection(i);
                    break;
                }
            }
        }

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.memberNumber != null) {
            memberId.setText(formsProfile.insuranceProvider.memberNumber);
        }

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.groupNumber != null) {
            group.setText(formsProfile.insuranceProvider.groupNumber);
        }

        if (formsProfile.insuranceProvider != null && formsProfile.insuranceProvider.insurancePhoneNumber != null) {
            insurancePhone.setText(formsProfile.insuranceProvider.insurancePhoneNumber);
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

        if (formsProfile.gender != null) {
            //Loop through genders until we find a match, then set gender spinner selection
            for (int i = 0; i < gender.getAdapter().getCount(); i++) {
                if (formsProfile.gender.equalsIgnoreCase(gender.getAdapter().getItem(i).toString())) {
                    gender.setSelection(i);
                    break;
                }
            }
        } else {
            gender.setSelection(0);
        }

        if (formsProfile.gender.equalsIgnoreCase(context.getString(R.string.female))) {
            areYouPregnantLabel.setVisibility(View.VISIBLE);
            areYouPregnantGroup.setVisibility(View.VISIBLE);
        }

        if (formsProfile.isPregnant) {
            areYouPregnantGroup.check(R.id.radio_pregnant);
        }

        if (formsProfile.weeksPregnant != null && Integer.parseInt(formsProfile.weeksPregnant) != 0) {
            weeksPregnantLayout.setVisibility(View.VISIBLE);
            weeksPregnant.setVisibility(View.VISIBLE);
            weeksPregnant.setText(formsProfile.weeksPregnant);
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

        if (formsProfile.address != null && formsProfile.address.zipCode != null && formsProfile.address.zipCode.trim().length() > 0) {
            zip.setText(formsProfile.address.zipCode.trim());
        }

        if (formsProfile.phoneNumber != null) {
            phone.setText(formsProfile.phoneNumber.replaceAll("\\.", "-"));
        }

        if (formsProfile.email != null) {
            email.setText(formsProfile.email);
        }

        if (formsProfile.translationNeeded) {
            translatorGroup.check(R.id.translator_needed);
            translatorLanguage.setVisibility(View.VISIBLE);

            if (formsProfile.translatorLanguage != null) {
                translatorLanguage.setText(formsProfile.translatorLanguage);
            }
        }

        if (formsProfile.assistanceNeeded) {
            assistanceGroup.check(R.id.assistance_needed);
        }

        if (formsProfile.reasonForVisit != null) {
            reasonForVisit.setText(formsProfile.reasonForVisit);
        }
    }

    public void setupInsurancePlanSpinner(@Nullable RegValidationResponse regValidationResponse) {
        if (regValidationResponse != null) {
            List<RegIncluded> insurances = ValidationUtil.getInsurances(regValidationResponse);
            insurances.add(0, new RegIncluded(ValidationUtil.TYPE_INSURANCES, null, new RegAttributes(null, context.getString(R.string.please_pick_a_plan))));
            ProviderPlanSpinnerAdapter spinnerAdapter = new ProviderPlanSpinnerAdapter(context, R.layout.provider_plan_spinner_item, insurances);
            //spinnerAdapter.setDropDownViewResource(R.layout.provider_plan_spinner_item);

            if (plan != null) {
                plan.setAdapter(spinnerAdapter);
            }

            //Call again now with proper adapter setup
            if (autoPopulateFromProfile) {
                populateInsuranceLayout();
            }
        } else {
            Timber.e("Wasn't able to setup insurances!");
        }
    }

    public void setupValidationRules(@Nullable RegValidationResponse regValidationResponse) {
        updateVisibility(false);

        if (regValidationResponse != null) {
            final List<String> fields = ValidationUtil.getEnabledFields(regValidationResponse);

            if (fields != null) {
                if (fields.contains(ValidationUtil.FIELD_CAREGIVER_NAME) && !BookingManager.isBookingForMe()) {
                    caregiverLayout.setVisibility(View.VISIBLE);
                } else {
                    caregiverLayout.setVisibility(View.GONE);
                }

                firstNameLayout.setVisibility(fields.contains(ValidationUtil.FIELD_FIRST_NAME) ? View.VISIBLE : View.GONE);
                lastNameLayout.setVisibility(fields.contains(ValidationUtil.FIELD_LAST_NAME) ? View.VISIBLE : View.GONE);
                preferredNameLayout.setVisibility(View.VISIBLE);

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
                    gender.setOnItemSelectedListener(null);
                    gender.setVisibility(View.GONE);
                    genderLabel.setVisibility(View.GONE);
                }

                dateOfBirthLayout.setVisibility(fields.contains(ValidationUtil.FIELD_BIRTHDATE) ? View.VISIBLE : View.GONE);
                addressLayout.setVisibility(View.VISIBLE);
                address2Layout.setVisibility(View.VISIBLE);
                cityLayout.setVisibility(View.VISIBLE);
                state.setVisibility(View.VISIBLE);
                zipLayout.setVisibility(fields.contains(ValidationUtil.FIELD_ZIP) ? View.VISIBLE : View.GONE);
                phoneLayout.setVisibility(fields.contains(ValidationUtil.FIELD_PHONE_NUMBER) ? View.VISIBLE : View.GONE);
                emailLayout.setVisibility(fields.contains(ValidationUtil.FIELD_EMAIL) ? View.VISIBLE : View.GONE);

                translatorLabel.setVisibility(View.VISIBLE);
                translatorGroup.setVisibility(View.VISIBLE);
                assistanceLabel.setVisibility(View.VISIBLE);
                assistanceGroup.setVisibility(View.VISIBLE);
                reasonForVisitLayout.setVisibility(fields.contains(ValidationUtil.FIELD_REASON_FOR_VISIT) ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * Updates the visibility of the views depending on the status of loading the validation rules
     *
     * @param isLoading should we show the loading view or not
     */
    private void updateVisibility(boolean isLoading) {
        try {
            planLabel.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            plan.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            memberIdLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            memberId.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            groupLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            group.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            insurancePhoneLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            insurancePhone.setVisibility(isLoading ? View.GONE : View.VISIBLE);

            progressBarInsurance.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        } catch (NullPointerException ex) {
            Timber.e(ex.toString());
            ex.printStackTrace();
        }
    }

    /**
     * Gets the Profile constructed from the values in the forms
     *
     * @return a Profile that accurately represents the forms filled out
     */
    public Profile getProfile() {
        if (formsProfile.address == null) {
            formsProfile.address = new Address();
        }

        if (formsProfile.insuranceProvider == null) {
            formsProfile.insuranceProvider = new InsuranceProvider();
        }

        if (caregiverName.getVisibility() == View.VISIBLE && caregiverName.getText() != null) {
            formsProfile.primaryCaregiverName = caregiverName.getText().toString().trim();
        }

        if (firstName.getVisibility() == View.VISIBLE && firstName.getText() != null) {
            formsProfile.firstName = firstName.getText().toString().trim();
        }

        if (lastName.getVisibility() == View.VISIBLE && lastName.getText() != null) {
            formsProfile.lastName = lastName.getText().toString().trim();
        }

        if (preferredName.getVisibility() == View.VISIBLE && preferredName.getText() != null) {
            formsProfile.preferredName = preferredName.getText().toString().trim();
        }

        if (gender.getVisibility() == View.VISIBLE && !gender.getSelectedItem().toString().isEmpty()) {
            formsProfile.gender = gender.getSelectedItem().toString().trim();
        }

        if (areYouPregnantGroup.getVisibility() == View.VISIBLE) {
            formsProfile.isPregnant = areYouPregnantGroup.getCheckedRadioButtonId() == R.id.radio_pregnant;
        }

        //Need to make sure weeks pregnant is sent as null (empty causes issues with API)
        if (weeksPregnant.getVisibility() == View.VISIBLE && weeksPregnant.getText() != null &&
                !weeksPregnant.getText().toString().isEmpty()) {
            formsProfile.weeksPregnant = weeksPregnant.getText().toString().trim();
        } else {
            formsProfile.weeksPregnant = null;
        }

        if (dateOfBirth.getVisibility() == View.VISIBLE && dateOfBirth.getText() != null) {
            formsProfile.dateOfBirth = DateUtil.convertReadableToUTC(dateOfBirth.getText().toString().trim());
        }

        if (address.getVisibility() == View.VISIBLE && address.getText() != null) {
            formsProfile.address.line1 = address.getText().toString().trim();
        }

        if (address2.getVisibility() == View.VISIBLE && address2.getText() != null) {
            formsProfile.address.line2 = address2.getText().toString().trim();
        }

        if (city.getVisibility() == View.VISIBLE && city.getText() != null) {
            formsProfile.address.city = city.getText().toString().trim();
        }

        if (state.getVisibility() == View.VISIBLE && !state.getSelectedItem().toString().isEmpty() &&
                state.getSelectedItemPosition() != 0) {
            formsProfile.address.stateOrProvince = state.getSelectedItem().toString().trim();
        }

        if (zip.getVisibility() == View.VISIBLE && zip.getText() != null) {
            formsProfile.address.zipCode = zip.getText().toString().trim();
        }

        if (email.getVisibility() == View.VISIBLE && email.getText() != null) {
            formsProfile.email = email.getText().toString().trim();
        }

        //Make sure to strip phone number of any non-digits
        if (phone.getVisibility() == View.VISIBLE && phone.getText() != null) {
            formsProfile.phoneNumber = CommonUtil.stripPhoneNumber(phone.getText().toString().trim());
        }

        if (plan.getVisibility() == View.VISIBLE && plan.getSelectedItemPosition() != 0) {
            formsProfile.insuranceProvider.insurancePlan = ((RegIncluded) plan.getSelectedItem()).getAttributes().getName();
            formsProfile.insuranceProvider.insurancePlanPermaLink = ((RegIncluded) plan.getSelectedItem()).getId();
            //formsProfile.insuranceProvider.providerName = ((RegIncluded) plan.getSelectedItem()).getAttributes().getCompany();
        }

        if (memberId.getVisibility() == View.VISIBLE && memberId.getText() != null) {
            formsProfile.insuranceProvider.memberNumber = memberId.getText().toString().trim();
        }

        if (group.getVisibility() == View.VISIBLE && group.getText() != null) {
            formsProfile.insuranceProvider.groupNumber = group.getText().toString().trim();
        }

        if (insurancePhone.getVisibility() == View.VISIBLE && insurancePhone.getText() != null) {
            formsProfile.insuranceProvider.insurancePhoneNumber = insurancePhone.getText().toString().trim();
        }

        if (translatorGroup.getVisibility() == View.VISIBLE) {
            formsProfile.translationNeeded = translatorGroup.getCheckedRadioButtonId() == R.id.translator_needed;
        }

        if (translatorLanguage.getVisibility() == View.VISIBLE && translatorLanguage.getText() != null) {
            formsProfile.translatorLanguage = translatorLanguage.getText().toString().trim();
        }

        if (assistanceGroup.getVisibility() == View.VISIBLE) {
            formsProfile.assistanceNeeded = assistanceGroup.getCheckedRadioButtonId() == R.id.assistance_needed;
        }

        if (reasonForVisit.getVisibility() == View.VISIBLE && reasonForVisit.getText() != null) {
            formsProfile.reasonForVisit = reasonForVisit.getText().toString().trim();
        }

        return formsProfile;
    }


    public boolean validateForm(int page) {
        boolean isValid = true;

        if (page == 0) {
            //Insurance Page

            if (plan.getSelectedItem() == null || plan.getSelectedItemPosition() == 0) {
                isValid = false;
                planLabel.setText(R.string.plan_required);
                planLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                planLabel.setText(context.getString(R.string.insurance_plan));
                planLabel.setTextColor(ContextCompat.getColor(context, R.color.text_darker));
            }

            if (memberId.getText().toString().isEmpty()) {
                isValid = false;
                memberIdLayout.setError(context.getString(R.string.member_id_required));
            } else {
                memberIdLayout.setError(null);
            }

            if (group.getText().toString().isEmpty()) {
                isValid = false;
                groupLayout.setError(context.getString(R.string.group_id_required));
            } else {
                groupLayout.setError(null);
            }

            if (insurancePhone.getVisibility() == View.VISIBLE && !insurancePhone.getText().toString().isEmpty() &&
                    !CommonUtil.isValidMobile(insurancePhone.getText().toString())) {
                isValid = false;
                insurancePhoneLayout.setError(context.getString(R.string.insurance_phone_number_invalid));
            } else {
                insurancePhoneLayout.setError(null);
            }

        } else if (page == 1) {
            //Personal Page
            int scrollPosition = -1;

            if (caregiverLayout.getVisibility() == View.VISIBLE && caregiverName.getText().toString().trim().isEmpty()) {
                caregiverLayout.setError(context.getString(R.string.caregiver_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) caregiverLayout.getY();
                caregiverLayout.setFocusable(true);
            } else {
                caregiverLayout.setError(null);
            }

            if (firstNameLayout.getVisibility() == View.VISIBLE && firstName.getText().toString().trim().isEmpty()) {
                firstNameLayout.setError(context.getString(R.string.first_name_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) firstNameLayout.getY();
                firstNameLayout.setFocusable(true);
            } else {
                firstNameLayout.setError(null);
            }

            if (lastNameLayout.getVisibility() == View.VISIBLE && lastName.getText().toString().trim().isEmpty()) {
                lastNameLayout.setError(context.getString(R.string.last_name_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) lastNameLayout.getY();
                lastNameLayout.setFocusable(true);
            } else {
                lastNameLayout.setError(null);
            }

            if (gender.getVisibility() == View.VISIBLE && gender.getSelectedItemPosition() == 0) {
                genderLabel.setText(context.getString(R.string.gender_required));
                genderLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                if (scrollPosition == -1)
                    scrollPosition = (int) genderLabel.getY();
                genderLabel.setFocusable(true);
            } else {
                genderLabel.setText(context.getString(R.string.gender));
                genderLabel.setTextColor(ContextCompat.getColor(context, R.color.text_darker));
            }

            if (weeksPregnantLayout.getVisibility() == View.VISIBLE && weeksPregnant.getText().toString().isEmpty()) {
                weeksPregnantLayout.setError(context.getString(R.string.weeks_pregnant_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) weeksPregnantLayout.getY();
                weeksPregnantLayout.setFocusable(true);
            } else if (weeksPregnantLayout.getVisibility() == View.VISIBLE && !weeksPregnant.getText().toString().isEmpty() && Integer.parseInt(weeksPregnant.getText().toString()) > 45) {
                weeksPregnantLayout.setError(context.getString(R.string.weeks_pregnant_too_large));
                if (scrollPosition == -1)
                    scrollPosition = (int) weeksPregnantLayout.getY();
                weeksPregnantLayout.setFocusable(true);
            } else {
                weeksPregnantLayout.setError(null);
            }

            if (dateOfBirthLayout.getVisibility() == View.VISIBLE && !DateUtil.isValidDateOfBirth(dateOfBirth.getText().toString())) {
                dateOfBirthLayout.setError(context.getString(R.string.date_of_birth_invalid));
                if (scrollPosition == -1)
                    scrollPosition = (int) dateOfBirthLayout.getY();
                dateOfBirthLayout.setFocusable(true);
            } else if (dateOfBirthLayout.getVisibility() == View.VISIBLE && BookingManager.isBookingForMe() && !DateUtil.isOlderThan18(DateUtil.convertReadableToUTC(dateOfBirth.getText().toString()))) {
                dateOfBirthLayout.setError(context.getString(R.string.date_of_birth_too_young));
                if (scrollPosition == -1)
                    scrollPosition = (int) dateOfBirthLayout.getY();
                dateOfBirthLayout.setFocusable(true);
            } else {
                dateOfBirthLayout.setError(null);
            }

            if (addressLayout.getVisibility() == View.VISIBLE && address.getText().toString().trim().isEmpty()) {
                addressLayout.setError(context.getString(R.string.address_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) addressLayout.getY();
                addressLayout.setFocusable(true);
            } else {
                addressLayout.setError(null);
            }

            if (cityLayout.getVisibility() == View.VISIBLE && city.getText().toString().trim().isEmpty() && state.getVisibility() == View.VISIBLE && state.getSelectedItemPosition() == 0) {
                cityLayout.setError(context.getString(R.string.city_and_state_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) cityLayout.getY();
                cityLayout.setFocusable(true);
            } else if (cityLayout.getVisibility() == View.VISIBLE && city.getText().toString().trim().isEmpty()) {
                cityLayout.setError(context.getString(R.string.city_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) cityLayout.getY();
                cityLayout.setFocusable(true);
            } else if (state.getVisibility() == View.VISIBLE && state.getSelectedItemPosition() == 0) {
                cityLayout.setError(context.getString(R.string.state_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) cityLayout.getY();
                cityLayout.setFocusable(true);
            } else {
                cityLayout.setError(null);
            }

            if (zipLayout.getVisibility() == View.VISIBLE && zip.getText().toString().isEmpty()) {
                zipLayout.setError(context.getString(R.string.zip_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) zipPhoneLayout.getY();
                zipLayout.setFocusable(true);
            } else if (zipLayout.getVisibility() == View.VISIBLE && (zip.getText().toString().trim().length() != 0 && zip.getText().toString().trim().length() != 5)) {
                zipLayout.setError(context.getString(R.string.zip_invalid));
                if (scrollPosition == -1)
                    scrollPosition = (int) zipPhoneLayout.getY();
                zipLayout.setFocusable(true);
            } else {
                zipLayout.setError(null);
            }

            if (phoneLayout.getVisibility() == View.VISIBLE && phone.getText().toString().isEmpty()) {
                phoneLayout.setError(context.getString(R.string.phone_number_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) zipPhoneLayout.getY();
                phoneLayout.setFocusable(true);
            } else if (phoneLayout.getVisibility() == View.VISIBLE && !CommonUtil.isValidMobile(phone.getText().toString())) {
                phoneLayout.setError(context.getString(R.string.phone_number_invalid));
                if (scrollPosition == -1)
                    scrollPosition = (int) zipPhoneLayout.getY();
                phoneLayout.setFocusable(true);
            } else {
                phoneLayout.setError(null);
            }

            if (emailLayout.getVisibility() == View.VISIBLE && email.getText().toString().isEmpty()) {
                emailLayout.setError(context.getString(R.string.email_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) emailLayout.getY();
                emailLayout.setFocusable(true);
            } else if (emailLayout.getVisibility() == View.VISIBLE && !CommonUtil.isValidEmail(email.getText().toString())) {
                emailLayout.setError(context.getString(R.string.email_invalid));
                if (scrollPosition == -1)
                    scrollPosition = (int) emailLayout.getY();
                emailLayout.setFocusable(true);
            } else {
                emailLayout.setError(null);
            }

            if (translatorLanguageLayout.getVisibility() == View.VISIBLE &&
                    translatorLanguage.getText().toString().trim().isEmpty()) {
                translatorLanguageLayout.setError(context.getString(R.string.translator_language_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) translatorLanguageLayout.getY();
                translatorLanguageLayout.setFocusable(true);
            } else {
                translatorLanguageLayout.setError(null);
            }

            if (reasonForVisitLayout.getVisibility() == View.VISIBLE &&
                    reasonForVisit.getText().toString().trim().isEmpty()) {
                reasonForVisitLayout.setError(context.getString(R.string.reason_for_visit_required));
                if (scrollPosition == -1)
                    scrollPosition = (int) reasonForVisitLayout.getY();
                reasonForVisitLayout.setFocusable(true);
            } else {
                reasonForVisitLayout.setError(null);
            }
            if (scrollPosition != -1) {
                scrollView.smoothScrollTo(0, scrollPosition);
                return false;
            }
        }
        return isValid;
    }
}