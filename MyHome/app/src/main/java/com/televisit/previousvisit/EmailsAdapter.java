package com.televisit.previousvisit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.prokarma.myhome.R;

import java.util.List;

import timber.log.Timber;

/**
 * Created by veluri on 12/18/17.
 */

public class EmailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<EmailsAdapter.EmailSelection> emailsList;

    public EmailsAdapter(List<EmailsAdapter.EmailSelection> emailsList) {
        this.emailsList = emailsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.email_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;

        myHolder.emailId.setText(emailsList.get(position).getEmailId().toLowerCase());
        myHolder.emailId.setChecked(emailsList.get(position).isSelected());

        myHolder.emailId.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioButton rb = (RadioButton) view;
                Timber.d("" + rb.getText() + ". state = " + rb.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (emailsList == null || emailsList.isEmpty()) {
            return 0;
        }
        return emailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public RadioButton emailId;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            emailId = (RadioButton) view.findViewById(R.id.email_id);
        }
    }

    public static class EmailSelection {
        private String emailId;
        private boolean selected;

        public EmailSelection() {
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
