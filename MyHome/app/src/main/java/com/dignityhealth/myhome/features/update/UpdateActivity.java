package com.dignityhealth.myhome.features.update;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dignityhealth.myhome.R;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    /*
 * Get an intent for update activity.
 */
    public static Intent getLoginIntent(Context context) {

        return new Intent(context, UpdateActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        boolean isForceUpdate = getIntent().getBooleanExtra("IS_FORCE_UPDATE", false);

        Button update = (Button) findViewById(R.id.ok_update);
        Button cancel = (Button) findViewById(R.id.cancel_update);

        if (isForceUpdate)
            cancel.setVisibility(View.GONE);

        update.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ok_update:
                updateApplication();
                finishAffinity();
                break;
            case R.id.cancel_update:
                finish();
                break;
        }
    }

    private void updateApplication() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +
                "com.attendify.dignityhealthconvention")));

//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
    }
}