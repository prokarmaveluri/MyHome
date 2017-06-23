package com.dignityhealth.myhome.features.enrollment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dignityhealth.myhome.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Fragment dialog to display the password criteria.
 *
 * Created by cmajji on 1/03/17.
 */
public class PasswordCriteriaDialog extends DialogFragment {

    public PasswordCriteriaDialog() {
        // Required empty public constructor
    }

    public static PasswordCriteriaDialog newInstance() {
        PasswordCriteriaDialog fragment = new PasswordCriteriaDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_password_criteria, container, false);
        TextView textView = (TextView) view.findViewById(R.id.password_criteria_text);
        Button button = (Button) view.findViewById(R.id.password_criteria_cancel);
        textView.setText(getBulletPoints(getCriteria()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private String getBulletPoints(List<String> list) {
        String points = "";
        for (int index = 0; index < list.size(); index++) {
            if (index != list.size() - 1) {
                points = points.concat("\u2022 " + list.get(index) + "\n");
            } else {
                points = points.concat("\u2022 " + list.get(index));
            }
        }
        return points;
    }

    private List<String> getCriteria() {
        List<String> criteria = new ArrayList<>();
        criteria.add(getString(R.string.password_must_be_eight_chars));
        criteria.add(getString(R.string.password_cannot_contain_name));
        criteria.add(getString(R.string.password_needs_one_uppercase));
        criteria.add(getString(R.string.password_needs_one_lowercase));
        criteria.add(getString(R.string.password_needs_one_number));
        criteria.add(getString(R.string.password_needs_one_special));
        return criteria;
    }
}
