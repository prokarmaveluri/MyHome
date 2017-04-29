package com.dignityhealth.myhome.features.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.app.BaseFragment;
import com.dignityhealth.myhome.networking.NetworkManager;
import com.dignityhealth.myhome.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwelsh on 4/26/17.
 */

public class ProfileFragment extends BaseFragment {
    public static final String PROFILE_TAG = "profile_tag";
    View profileView;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.profile, container, false);

        //get the profile information
        //getProfile("bearer token here...");

        return profileView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.PROFILE;
    }

    private void getProfile(String bearer){
        NetworkManager.getInstance().profile(bearer).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "Successful Response\n" + response, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Response, but not successful?\n" + response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "something failed! :/\n", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
