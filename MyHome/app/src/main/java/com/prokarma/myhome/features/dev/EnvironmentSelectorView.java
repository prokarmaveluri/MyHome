package com.prokarma.myhome.features.dev;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.prokarma.myhome.R;

/**
 * Created by kwelsh on 2/7/18.
 */

public class EnvironmentSelectorView implements EnvironmentSelectorContract.View {
    private RadioGroup envMyHomeGroup;
    private RadioGroup envAmWellGroup;
    private AppCompatCheckBox checkBox;
    private TextInputLayout usernameLayout;
    private TextInputEditText user;
    private TextInputLayout passwordLayout;
    private TextInputEditText pass;

    public EnvironmentSelectorView(final View masterView, final EnvironmentSelectorPresenter presenter) {
        envMyHomeGroup = (RadioGroup) masterView.findViewById(R.id.env_myhome_radio_group);
        envAmWellGroup = (RadioGroup) masterView.findViewById(R.id.env_amwell_radio_group);
        checkBox = (AppCompatCheckBox) masterView.findViewById(R.id.checkbox_mutual_auth);
        usernameLayout = (TextInputLayout) masterView.findViewById(R.id.user_layout);
        user = (TextInputEditText) masterView.findViewById(R.id.user);
        passwordLayout = (TextInputLayout) masterView.findViewById(R.id.password_layout);
        pass = (TextInputEditText) masterView.findViewById(R.id.password);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    usernameLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    passwordLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                }
            }
        });

        Toolbar toolbar = (Toolbar) masterView.findViewById(R.id.toolbar);
        toolbar.setTitle("Please Select Environment");
        toolbar.inflateMenu(R.menu.environment_selector_dialog_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.finish_dialog:
                        String username = "";
                        String password = "";

                        if (user.getText() != null) {
                            username = user.getText().toString();
                        }

                        if (pass.getText() != null) {
                            password = pass.getText().toString();
                        }

                        presenter.onFinishedSelecting(envMyHomeGroup.getCheckedRadioButtonId(), envAmWellGroup.getCheckedRadioButtonId(), checkBox.isChecked(), username, password);
                        break;
                }
                return true;
            }
        });

    }
}
