package com.prokarma.myhome.features.dev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseFragment;
import com.prokarma.myhome.utils.Constants;

/**
 * Created by kwelsh on 5/17/17.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class DeveloperFragment extends BaseFragment implements DevInterface.View {
    DevInterface.Presenter presenter = new DevPresenter(this);

    public static final String DEVELOPER_TAG = "dev_tag";
    public static final String DEVELOPER = "developer";

    View developerView;
    TextView bearer;
    TextView amwell;
    TextView profile;
    TextView location;
    TextView hockey;
    TextView maps;
    TextView type;

    public static DeveloperFragment newInstance() {
        return new DeveloperFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        developerView = inflater.inflate(R.layout.developer, container, false);
        getActivity().setTitle(getString(R.string.developer_settings));

        bearer = (TextView) developerView.findViewById(R.id.bearer);
        amwell = (TextView) developerView.findViewById(R.id.amwell);
        profile = (TextView) developerView.findViewById(R.id.profile);
        location = (TextView) developerView.findViewById(R.id.location);
        hockey = (TextView) developerView.findViewById(R.id.hockey);
        maps = (TextView) developerView.findViewById(R.id.maps);
        type = (TextView) developerView.findViewById(R.id.type);

        presenter.requestingBearerToken();
        presenter.requestingAmWellToken();
        presenter.requestingProfile();
        presenter.requestingLocation();
        presenter.requestingHockeyId();
        presenter.requestingMapsKey(getContext());
        presenter.requestingBuildType();

        Button apiButton = (Button) developerView.findViewById(R.id.api_overview);
        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onApiButtonPressed();
            }
        });

        return developerView;
    }

    @Override
    public Constants.ActivityTag setDrawerTag() {
        return Constants.ActivityTag.DEVELOPER;
    }

    @Override
    public void showBearerToken(String bearerToken) {
        bearer.setText(bearerToken);
    }

    @Override
    public void showAmWellToken(String amWellToken) {
        amwell.setText(amWellToken);
    }

    @Override
    public void showProfile(String profile) {
        this.profile.setText(profile);
    }

    @Override
    public void showLocation(String location) {
        this.location.setText(location);
    }

    @Override
    public void showHockeyId(String hockeyId) {
        hockey.setText(hockeyId);
    }

    @Override
    public void showMapsKey(String mapsKey) {
        maps.setText(mapsKey);
    }

    @Override
    public void showBuildType(String buildType) {
        type.setText(buildType);
    }
}
