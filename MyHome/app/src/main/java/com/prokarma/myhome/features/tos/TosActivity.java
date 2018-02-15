package com.prokarma.myhome.features.tos;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.BaseActivity;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.utils.Constants;
import com.prokarma.myhome.utils.TealiumUtil;

/**
 * Created by kwelsh on 2/14/18.
 */
public class TosActivity extends BaseActivity {
    public static final String FILE_ANDROID_ASSET_TOS_HTML = "file:///android_asset/tos.html"; //Changes Fragment's ToS too

    TosPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.toolbarWhite);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            //noinspection deprecation
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }

        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EnrollmentRequest enrollmentRequest = getIntent().getParcelableExtra(Constants.ENROLLMENT_REQUEST);

        presenter = new TosPresenter(this, this, getWindow().getDecorView(), enrollmentRequest);
        presenter.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TealiumUtil.trackView(Constants.TOS_SCREEN, null);
    }
}
