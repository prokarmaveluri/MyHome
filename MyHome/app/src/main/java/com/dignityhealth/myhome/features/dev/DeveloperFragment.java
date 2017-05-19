package com.dignityhealth.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dignityhealth.myhome.BuildConfig;
import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.features.appointments.Appointment;
import com.dignityhealth.myhome.features.fad.FadManager;
import com.dignityhealth.myhome.features.profile.ProfileManager;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.networking.auth.AuthManager;
import com.dignityhealth.myhome.utils.Constants;
import com.dignityhealth.myhome.utils.DevUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by kwelsh on 5/17/17.
 */

public class DeveloperFragment extends BaseFragment {
    public static final String DEVELOPER_TAG = "dev_tag";
    View developerView;

    public static DeveloperFragment newInstance() {
        return new DeveloperFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        developerView = inflater.inflate(R.layout.developer, container, false);
        getActivity().setTitle(getString(R.string.developer_settings));

        TextView bearer = (TextView) developerView.findViewById(R.id.bearer);
        bearer.setText(AuthManager.getInstance().getBearerToken() != null ? "Bearer " + AuthManager.getInstance().getBearerToken() : "Couldn't find Bearer Token");
        TextView session = (TextView) developerView.findViewById(R.id.session);
        session.setText(AuthManager.getInstance().getSessionToken() != null ? AuthManager.getInstance().getSessionToken() : "Session Token couldn't be found");
        TextView profile = (TextView) developerView.findViewById(R.id.profile);
        profile.setText(ProfileManager.getProfile() != null ? ProfileManager.getProfile().toString() : "Profile Not Retrieved Yet");
        TextView location = (TextView) developerView.findViewById(R.id.location);
        location.setText(FadManager.getInstance().getCurrentLocation() != null ? FadManager.getInstance().getCurrentLocation().toString() : "Location Not Retrieved Yet");
        TextView hockey = (TextView) developerView.findViewById(R.id.hockey);
        hockey.setText(BuildConfig.HOCKEY_ID != null ? BuildConfig.HOCKEY_ID : "Unknown");
        TextView type = (TextView) developerView.findViewById(R.id.type);
        type.setText(BuildConfig.BUILD_TYPE != null ? BuildConfig.BUILD_TYPE : "Unknown");

        Button addAppointment = (Button) developerView.findViewById(R.id.add_appointment);
        addAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRandomAppointment("Bearer " + AuthManager.getInstance().getBearerToken());
            }
        });

        return developerView;
    }

    private void addRandomAppointment(String bearer) {
        Timber.i("Session bearer " + bearer);

        final Appointment dummyAppointment =
                new Appointment(
                        DevUtil.getRandomID(),
                        true,
                        DevUtil.getEmail(),
                        DevUtil.getRandomAppointmentDate(),
                        "Dermatology",
                        DevUtil.getRandomBoolean(),
                        "care giver name here",
                        "This is a dummy appointment for testing",
                        DevUtil.getRandomAppointmentReason(),
                        DevUtil.getRandomDoctor(),
                        DevUtil.getRandomFacilityName(),
                        DevUtil.getRandomPhoneNumber(),
                        DevUtil.getRandomAddress());

        NetworkManager.getInstance().createAppointment(bearer, dummyAppointment).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful Response\n" + response);
                    Toast.makeText(getActivity(), "Appointment Created!", Toast.LENGTH_SHORT).show();
                } else {
                    Timber.e("Response, but not successful?\n" + response);
                    Toast.makeText(getActivity(), "Appointment Creation Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Timber.e("Something failed! :/");
                Timber.e("Throwable = " + t);
                Toast.makeText(getActivity(), "Appointment Creation Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.DEVELOPER;
    }
}
