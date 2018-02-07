package com.prokarma.myhome.features.dev;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prokarma.myhome.R;

/**
 * Created by kwelsh on 2/7/18.
 */

public class DevView implements DevContract.View {
    TextView bearer;
    TextView amwell;
    TextView profile;
    TextView location;
    TextView hockey;
    TextView maps;
    TextView type;

    public DevView(final View masterView, final DevPresenter presenter) {
        bearer = (TextView) masterView.findViewById(R.id.bearer);
        amwell = (TextView) masterView.findViewById(R.id.amwell);
        profile = (TextView) masterView.findViewById(R.id.profile);
        location = (TextView) masterView.findViewById(R.id.location);
        hockey = (TextView) masterView.findViewById(R.id.hockey);
        maps = (TextView) masterView.findViewById(R.id.maps);
        type = (TextView) masterView.findViewById(R.id.type);

        Button apiButton = (Button) masterView.findViewById(R.id.api_overview);
        apiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onApiButtonPressed();
            }
        });
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
