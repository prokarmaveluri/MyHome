package com.televisit.previousvisit;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.televisit.summary.VisitSummaryFragment;

import java.util.List;

/**
 * Created by veluri on 12/18/17.
 */

public class EmailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<EmailsAdapter.EmailSelection> emailsList;
    private VisitSummaryFragment fragment;

    public EmailsAdapter(List<EmailsAdapter.EmailSelection> emailsList, VisitSummaryFragment fragment) {
        this.emailsList = emailsList;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.email_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;

        myHolder.emailId.setText(emailsList.get(position).getEmailId().toLowerCase());
        myHolder.deleteEmail.setTag(emailsList.get(position).getEmailId().toLowerCase());

        myHolder.emailId.setContentDescription(myHolder.emailId.getText());
        myHolder.deleteEmail.setContentDescription(fragment.getContext().getResources().getString(R.string.delete) + ", Email address, " + myHolder.emailId.getText());

        myHolder.deleteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment != null && v.getTag() != null) {
                    fragment.deleteEmailAddress(v.getTag().toString());
                }
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
        public TextView emailId;
        public TextView deleteEmail;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            emailId = (TextView) view.findViewById(R.id.email_id);
            deleteEmail = (TextView) view.findViewById(R.id.delete_email);
        }
    }

    public static class EmailSelection {
        private String emailId;

        public EmailSelection() {
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }
    }
}
