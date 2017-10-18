/**
 * Copyright 2016 American Well Systems
 * All rights reserved.
 * <p/>
 * It is illegal to use, reproduce or distribute
 * any part of this Intellectual Property without
 * prior written authorization from American Well.
 */
package com.americanwell.sdksample.intake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.Topic;
import com.americanwell.sdk.entity.visit.TriageQuestion;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.manager.ValidationConstants;
import com.prokarma.myhome.R;
import com.americanwell.sdksample.SampleApplication;
import com.americanwell.sdksample.cost.VisitCostActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;
import nucleus.factory.RequiresPresenter;

/**
 * Activity for collection of intake triage questions
 */
@RequiresPresenter(TriageQuestionsPresenter.class)
public class TriageQuestionsActivity extends BaseIntakeActivity<TriageQuestionsPresenter> {

    @BindView(R.id.topic_answer_edit_text)
    EditText topicAnswerEditText;
    @BindView(R.id.privacy_policy_layout)
    View privacyPolicyLayout;
    @BindView(R.id.privacy_policy_check_box)
    CheckBox privacyPolicyCheckBox;
    @BindView(R.id.privacy_policy_link)
    View privacyPolicyLink;
    @BindView(R.id.triage_topic_question)
    TextView triageTopicQuestion;
    @BindView(R.id.triage_question_list)
    ListView triageQuestionList;
    @BindView(R.id.topics_list)
    ListView topicsList;
    @BindView(R.id.topics_list_footer_divider)
    View topicsDivider;
    @BindView(R.id.dependent_info_text)
    TextView dependentInfoText;
    @BindView(R.id.share_health_summary_checkbox)
    CheckBox switchHealthSummary;
    @BindView(R.id.triage_phone_layout)
    TextInputLayout phoneLayout;
    @BindView(R.id.triage_phone_edit_text)
    EditText phoneEditText;
    @BindView(R.id.invite_guests_text)
    TextView inviteGuestsText;
    @BindView(R.id.invites_list_view)
    ListView invitesList;
    @BindView(R.id.invites_list_footer_divider)
    View invitesDivider;
    @BindView(R.id.add_email_layout)
    LinearLayout addEmailLayout;
    @BindView(R.id.invite_email)
    EditText inviteEmail;
    @BindView(R.id.button_add_invite)
    Button addInviteButton;
    @BindView(R.id.max_emails_message)
    TextView maxEmailsMessage;

    @BindString(R.string.app_warning)
    String appWarning;
    @BindString(R.string.patient_selection_privacy_policy_warning)
    String privacyPolicyWarning;
    @BindString(R.string.invite_guests_add_validation)
    String addValidation;
    @BindString(R.string.login_email_address_warning)
    String emailAddressWarning;
    @BindString(R.string.triage_guest_email_exists)
    String emailAddressExists;

    private TriageQuestionAdapter triageQuestionAdapter;
    private TopicsAdapter topicsAdapter;
    private ArrayAdapter<String> invitesAdapter;

    private AlertDialog privacyPolicyDialog;

    public static Intent makeIntent(@NonNull final Context context,
                                    @NonNull final VisitContext visitContext) {
        final Intent intent = new Intent(context, TriageQuestionsActivity.class);
        intent.putExtra(EXTRA_VISIT_CONTEXT, visitContext);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SampleApplication.getActivityComponent().inject(this);
        setContentView(R.layout.activity_triage_questions);
    }

    @Override
    protected void dismissDialogs() {
        super.dismissDialogs();
        dismissDialog(privacyPolicyDialog);
    }

    public void setTriageQuestions(final boolean showTriageQuestions, final List<TriageQuestion>
            triageQuestions) {
        if (showTriageQuestions) {
            triageQuestionAdapter = new TriageQuestionAdapter(this, triageQuestions);
            triageQuestionList.setAdapter(triageQuestionAdapter);
            triageQuestionList.setVisibility(View.VISIBLE);
        }
        else {
            triageQuestionList.setVisibility(View.GONE);
        }
    }

    public void setTopics(final List<Topic> topics) {
        topicsAdapter = new TopicsAdapter(this, topics);
        topicsList.setAdapter(topicsAdapter);
        if (!topics.isEmpty()) {
            topicAnswerEditText.setHint(R.string.triage_other_topic_prompt);
            topicsDivider.setVisibility(View.VISIBLE);
        }
        else {
            topicsList.setVisibility(View.GONE);
            topicsDivider.setVisibility(View.GONE);
        }
    }

    @OnItemClick(R.id.topics_list)
    public void setTopicSelected(int position) {
        final Topic item = topicsAdapter.getItem(position);
        getPresenter().toggleTopic(item);
        topicsAdapter.notifyDataSetChanged();
    }

    public void showTopicQuestion(boolean show) {
        triageTopicQuestion.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showOtherTopic(boolean show) {
        topicAnswerEditText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @OnTextChanged(value = R.id.topic_answer_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onOtherTopicChanged(final CharSequence value) {
        getPresenter().setOtherTopic(value.toString().trim());
    }

    public void setPhone(String phoneNumber) {
        phoneEditText.setText(phoneNumber);
    }

    @OnTextChanged(value = R.id.triage_phone_edit_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onPhoneChanged(final CharSequence value) {
        getPresenter().setCallbackPhone(value.toString().trim());
    }

    public void setHasPrivacyPolicy(final boolean hasPrivacyPolicy) {
        privacyPolicyLayout.setVisibility(hasPrivacyPolicy ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.privacy_policy_link)
    public void showPrivacyPolicy() {
        @SuppressLint("InflateParams")
        final WebView disclaimerWebView = (WebView) getLayoutInflater().inflate(R.layout.dialog_disclaimer, null);
        disclaimerWebView.loadData(getPresenter().getPrivacyPolicy(), "text/html;charset=UTF-8", "UTF-8");
        privacyPolicyDialog = new AlertDialog.Builder(this)
                .setView(disclaimerWebView)
                .show();
    }

    @OnCheckedChanged(R.id.privacy_policy_check_box)
    public void setPrivacyPolicyAccepted(boolean checked) {
        getPresenter().setLegalTextsAccepted(checked);
    }

    public void setPrivacyPolicyError() {
        setError(appWarning, privacyPolicyWarning);
    }

    @OnCheckedChanged(R.id.share_health_summary_checkbox)
    public void setShareHealthSummary(boolean checked) {
        getPresenter().setShareHealthSummary(checked);
    }

    public void setDependentInfoText(final String dependentName) {
        if (TextUtils.isEmpty(dependentName)) {
            dependentInfoText.setVisibility(View.GONE);
        }
        else {
            dependentInfoText.setText(getString(R.string.visit_dependent_info, dependentName));
            dependentInfoText.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        getPresenter().validate();
    }

    public void setFieldsValid(boolean isFirstAvailable) {
        if (isFirstAvailable) {
            startActivity(FirstAvailableActivity.makeIntent(this, getPresenter().getVisitContext()));
        }
        else {
            startActivity(VisitCostActivity.makeIntent(this, getPresenter().getVisitContext()));
        }
    }

    @Override
    protected Map<String, View> getValidationViews() {
        final Map<String, View> views = new HashMap<>();
        views.put(ValidationConstants.VALIDATION_PHONE, phoneLayout);
        return views;
    }

    public void setInviteEmails(final boolean showInviteEmails, final List<String> inviteEmails) {
        if (showInviteEmails) {
            invitesAdapter = new ArrayAdapter<>(this, R.layout.item_guest_invite, R.id.text1);
            invitesList.setAdapter(invitesAdapter);
            invitesAdapter.addAll(inviteEmails);
            checkToggleInviteDividerVisible();
        }
        else {
            inviteGuestsText.setVisibility(View.GONE);
            invitesList.setVisibility(View.GONE);
            invitesDivider.setVisibility(View.GONE);
            addEmailLayout.setVisibility(View.GONE);
        }
    }

    public void addInviteEmail(final String email) {
        invitesAdapter.add(email);
        inviteEmail.setText(null);
        checkToggleInviteDividerVisible();
    }

    public void removeInviteEmail(final String email) {
        invitesAdapter.remove(email);
        checkToggleInviteDividerVisible();
    }

    public void showInviteEmailError() {
        inviteEmail.setError(addValidation);
    }

    public String getInviteEmail() {
        return inviteEmail.getText().toString().trim();
    }

    @OnItemClick(R.id.invites_list_view)
    public void onItemClick(int position) {
        getPresenter().removeInviteEmail(invitesAdapter.getItem(position));
    }

    @OnClick(R.id.button_add_invite)
    public void onAddClick() {
        getPresenter().addInviteEmail(inviteEmail.getText().toString().trim());
    }

    protected void setInviteEmailError(final String error) {
        inviteEmail.setError(error);
    }

    public void setEmailWarning() {
        inviteEmail.setError(emailAddressWarning);
    }

    public void setEmailExists() {
        inviteEmail.setError(emailAddressExists);
    }

    protected void checkToggleInviteDividerVisible() {
        invitesDivider.setVisibility((invitesAdapter.getCount() > 0) ? View.VISIBLE : View.GONE);
    }

    public void setMaxInvitesReached(boolean maxReached, int max) {
        addEmailLayout.setVisibility(maxReached ? View.GONE : View.VISIBLE);
        maxEmailsMessage.setVisibility(maxReached ? View.VISIBLE : View.GONE);
        maxEmailsMessage.setText(getString(R.string.invite_guests_max_reached, max));
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (maxReached && view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public class TriageQuestionAdapter extends ArrayAdapter<TriageQuestion> {
        public TriageQuestionAdapter(final Context context, final List<TriageQuestion> list) {
            super(context, 0, list);
        }

        public class ViewHolder {
            @BindView(R.id.text_view)
            TextView textView;
            @BindView(R.id.edit_text)
            EditText editText;
            private TextWatcher textWatcher;

            public ViewHolder(final View view) {
                ButterKnife.bind(this, view);
            }
        }

        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder;

            if (view == null) {
                view = View.inflate(getContext(), R.layout.item_triage_question, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final TriageQuestion triageQuestion = getItem(position);
            viewHolder.textView.setText(triageQuestion.getQuestion());
            viewHolder.editText.setText(triageQuestion.getAnswer());

            viewHolder.editText.removeTextChangedListener(viewHolder.textWatcher);
            viewHolder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    triageQuestion.setAnswer(s.toString());
                }
            };
            viewHolder.editText.addTextChangedListener(viewHolder.textWatcher);

            return view;
        }
    }

    public class TopicsAdapter extends ArrayAdapter<Topic> {
        public TopicsAdapter(final Context context, final List<Topic> list) {
            super(context, 0, list);
        }

        private class ViewHolder {
            CheckedTextView checkedTextView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_triage_topic, null);
                viewHolder = new ViewHolder();
                viewHolder.checkedTextView = (CheckedTextView) view;
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final Topic topic = getItem(position);
            viewHolder.checkedTextView.setText(topic.getTitle());
            viewHolder.checkedTextView.setChecked(topic.isSelected());
            return view;
        }

    }

}
