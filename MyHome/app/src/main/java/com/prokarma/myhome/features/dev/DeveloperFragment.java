package com.prokarma.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prokarma.myhome.BuildConfig;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.features.fad.FadManager;
import com.prokarma.myhome.features.profile.ProfileManager;
import com.prokarma.myhome.networking.auth.AuthManager;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/17/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class DeveloperFragment extends BaseFragment {
    public static final String DEVELOPER_TAG = "dev_tag";
    public static final String DEVELOPER = "developer";

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
        bearer.setText(AuthManager.getInstance().getBearerToken() != null ? AuthManager.getInstance().getBearerToken() : "Couldn't find Bearer Token");
        TextView session = (TextView) developerView.findViewById(R.id.session);
        session.setText(AuthManager.getInstance().getSessionToken() != null ? AuthManager.getInstance().getSessionToken() : "Session Token couldn't be found");
        TextView profile = (TextView) developerView.findViewById(R.id.profile);
        profile.setText(ProfileManager.getProfile() != null ? ProfileManager.getProfile().toString() : "Profile Not Retrieved Yet");
        TextView location = (TextView) developerView.findViewById(R.id.location);
        location.setText(FadManager.getInstance().getCurrentLocation() != null ? FadManager.getInstance().getCurrentLocation().toString() : "Location Not Retrieved Yet");
        TextView hockey = (TextView) developerView.findViewById(R.id.hockey);
        hockey.setText(BuildConfig.HOCKEY_ID);
        TextView maps = (TextView) developerView.findViewById(R.id.maps);
        maps.setText(getString(R.string.google_maps_api_key));
        TextView type = (TextView) developerView.findViewById(R.id.type);
        type.setText(BuildConfig.BUILD_TYPE);

        return developerView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.DEVELOPER;
    }
}
