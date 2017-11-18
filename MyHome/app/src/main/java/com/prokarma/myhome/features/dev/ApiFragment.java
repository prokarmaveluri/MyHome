package com.prokarma.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.ApiRecyclerViewListener;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.AppPreferences;
import com.prokarma.myhome.utils.CommonUtil;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.SessionUtil;

import java.util.ArrayList;

/**
 * Created by kwelsh on 5/17/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class ApiFragment extends BaseFragment {
    public static final String API_TAG = "api_tag";

    ArrayList<ApiOption> apiOptions;

    View apiView;
    RecyclerView apiRecyclerView;
    ApiRecyclerViewAdapter apiRecyclerViewAdapter;

    public static ApiFragment newInstance() {
        return new ApiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        apiView = inflater.inflate(R.layout.api, container, false);
        getActivity().setTitle(getString(R.string.api_settings));

        apiOptions = new ArrayList<>();
        apiOptions.add(new ApiOption("Hide API Error Info", "If this is toggled off, a button will show with the API Error message to allow you to view more details. This is defaulted to on in production and is disregaurded for developer builds; developers always see the info.", null, Constants.API_HIDE_API_ERROR_INFO));
        apiOptions.add(new ApiOption("Sign In API", "The Sign In API is used to log into the app. It also gives us profile information so our dashboard can be populated faster.", "WARNING: You'll have to uninstall the app to log back in if you turn this API off!", Constants.API_SIGN_IN_FORCE_ERROR));
        apiOptions.add(new ApiOption("Sign In Refresh API", "The Sign In Refresh API is used to log users back into the app automatically (if the sign in token is not expired). A failure here will just silently send the user to the Login Screen.", "WARNING: You may experience issues and have to uninstall if you want to sign in again!", Constants.API_SIGN_IN_REFRESH_FORCE_ERROR));
        apiOptions.add(new ApiOption("Sign Out API", "The Sign Out API is used to log the user out of the app. This should just fail silently, as the user is always taken back to the log in screen and doesn't care if the call actually failed.", null, Constants.API_SIGN_OUT_FORCE_ERROR));
        apiOptions.add(new ApiOption("Profile Get API", "The Profile Get API is called to get the user's profile information. This is normally an irrelevant call; we get the profile info directly from the Sign In API.", null, Constants.API_PROFILE_GET_FORCE_ERROR));
        apiOptions.add(new ApiOption("Profile Update API", "The Profile Update API is called to update the user's profile information. This occurs mainly in the Profile Edit Screen when you attempt to save, though it can also be called in the Booking Flow.", null, Constants.API_PROFILE_UPDATE_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get My Appointments API", "The Get My Appointments API is called when fetching a user's appointments. This is called mainly on the Appointment's screen.", null, Constants.API_GET_MY_APPOINTMENTS_FORCE_ERROR));
        apiOptions.add(new ApiOption("Create Appointment API", "The Create Appointment API is used when booking an appointment in the booking flow. It's called right after confirming the booking appointment.", null, Constants.API_CREATE_APPOINTMENT_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Validation Rules API", "The Get Validation Rules API is used to get the rules for the Booking Registration Forms. It also gets the list of Insurance Providers. This call occurs only in the Booking Flow, right after a time is selected.", null, Constants.API_GET_VALIDATION_RULES_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Provider Details API", "The Get Provider Details API is used to get the provider's full profile. It's called when we land on a the Provider's Page (often, some basic details are already known).", null, Constants.API_GET_PROVIDER_DETAILS_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Appointment Times API", "The Get Appointment Times API is used to get the provider's times he's available for an appointment. It's called when the user clicks on 'Book Appointment' button on the Provider's Page. In the near future, it will also be called when a user selects a date in a different month.", null, Constants.API_GET_APPOINTMENT_TIMES_FORCE_ERROR));
        apiOptions.add(new ApiOption("Register API", "The Register API is used to enroll the user for the app. This call is made right after accepting the Terms &amp; Conditions.", null, Constants.API_REGISTER_FORCE_ERROR));
        apiOptions.add(new ApiOption("Change Password API", "The Change Password API is used to change the user's password. It can be found in the top right in Settings.", null, Constants.API_CHANGE_PASSWORD_FORCE_ERROR));
        apiOptions.add(new ApiOption("Change Security Question API", "The Change Security Question API is used to change the user's security question. It can be found in the top right in Settings.", null, Constants.API_CHANGE_SECURITY_QUESTION_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Saved Doctors API", "The Get Saved Doctors API is used to fetch the list of favorite providers of the users. This is called on the FAD screen (and possibly the dashboard).", null, Constants.API_GET_SAVED_DOCTORS_FORCE_ERROR));
        apiOptions.add(new ApiOption("Save Doctor API", "The Save Doctor API is used to favorite a provider. The heart may still change due to the call taking a few seconds.", null, Constants.API_SAVE_DOCTOR_FORCE_ERROR));
        apiOptions.add(new ApiOption("Delete Saved Doctor API", "The Delete Saved Doctor API is used to delete a favorite provider. The heart may still change due to the call taking a few seconds.", null, Constants.API_DELETE_SAVED_DOCTOR_FORCE_ERROR));
        apiOptions.add(new ApiOption("Forgot Password API", "The Forgot Password API is used when a user forgets their credentials. It should send an email with instructions on how to recover their account.", null, Constants.API_FORGOT_PASSWORD_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Providers API", "The Get Providers API is used to fetch a list of doctors in FAD.", null, Constants.API_GET_PROVIDERS_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Location Suggestions API", "The Get Location Suggestions API is used to suggest locations to the user in FAD filters.", null, Constants.API_GET_LOCATION_SUGGESTIONS_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Search Suggestions API", "The Get Search Suggestions API is used to suggest search queries to the user in FAD. This is called when the user attempts to type anything into the search bar.", null, Constants.API_GET_SEARCH_SUGGESTIONS_FORCE_ERROR));
        apiOptions.add(new ApiOption("Get Location API", "The Get Location API is used to get a user's location. This is usually called in the background, silently, and should fail silently.", null, Constants.API_GET_LOCATION_FORCE_ERROR));
        apiOptions.add(new ApiOption("Find Email API", "The Find Email API is used to check if an email is already in use during the enrollment flow.", null, Constants.API_FIND_EMAIL_FORCE_ERROR));
        apiOptions.add(new ApiOption("Version Check API", "The Version Check API is used to check what version of the app the user has installed. This is helpful in forcing the user to upgrade the app if the APIs have changed.", null, Constants.API_VERSION_CHECK_FORCE_ERROR));
        apiOptions.add(new ApiOption("Resend Email API", "The Resend Email API is used to resend the email if a user has not verified their account yet.", null, Constants.API_RESEND_EMAIL_FORCE_ERROR));

        apiRecyclerView = (RecyclerView) apiView.findViewById(R.id.list_apis);
        apiRecyclerViewAdapter = new ApiRecyclerViewAdapter(getContext(), apiOptions, new ApiRecyclerViewListener() {
            @Override
            public void onPreferenceChanged(Object model, int position, boolean isChecked) {
                final ApiOption apiOption = (ApiOption) model;
                AppPreferences.getInstance().setBooleanPreference(apiOption.getPreference(), !isChecked);
            }
        });

        apiRecyclerView.setHasFixedSize(true);
        apiRecyclerView.setAdapter(apiRecyclerViewAdapter);
        apiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        apiRecyclerView.addItemDecoration(itemDecoration);

        Button save = (Button) apiView.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionUtil.logout(getActivity(), null);

                if (getActivity() != null) {
                    CommonUtil.exitApp(getContext(), getActivity());
                }
            }
        });

        return apiView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.API;
    }
}
