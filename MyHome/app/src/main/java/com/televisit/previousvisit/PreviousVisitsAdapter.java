package com.televisit.previousvisit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.R;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.utils.DateUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by veluri on 11/29/17.
 */

public class PreviousVisitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_EMPTY = 0;
    private final int VIEW_TYPE_NORMAL = 1;

    public final Context context;
    public List<VisitReport> visitReports;
    private final RecyclerViewListener onItemClickListener;
    private final DecimalFormat amountFormat;
    private final SimpleDateFormat dateFormat;


    public PreviousVisitsAdapter(Context context, @Nullable List<VisitReport> visitReports, RecyclerViewListener onItemClickListener) {
        this.context = context;
        this.visitReports = visitReports;
        this.onItemClickListener = onItemClickListener;
        amountFormat = new DecimalFormat("##.##");
        dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.previousvisit_item_empty, parent, false);
            return new ViewHolderEmpty(context, view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.previousvisit_item, parent, false);
            return new ViewHolder(context, view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, int position) {
        switch (genericHolder.getItemViewType()) {
            case VIEW_TYPE_NORMAL:
                ViewHolder holder = (ViewHolder) genericHolder;
                VisitReport visitReport = visitReports.get(position);

                if (visitReport.getProviderName() != null && !visitReport.getProviderName().isEmpty()) {
                    holder.doctorName.setText(visitReport.getProviderName());
                }

                try {
                    if (visitReport.getDate() != null && visitReport.getDate().isValidDate()) {
                        // DateUtil.convertDateToReadableShort(DateUtil.getDateFromHyphens(stringDate)));
                        // holder.date.setText(dateFormat.format(visitReport.getDate().toDate()));

                        holder.date.setText(DateUtil.convertDateToReadableShort(visitReport.getDate().toDate()));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                    e.printStackTrace();
                }

                //hardcoding for now, as per directions got
                holder.amount.setText("$ 0.00");
                holder.text.setText("I cut my hand on a peice of glass");

                holder.viewLink.setTag(position);
                holder.viewLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (int) v.getTag();
                        onItemClickListener.onItemClick(visitReports.get(pos), pos);
                    }
                });

                holder.setOnItemClickListener(visitReport, onItemClickListener);
                break;

            case VIEW_TYPE_EMPTY:
                ViewHolderEmpty holderEmpty = (ViewHolderEmpty) genericHolder;
                holderEmpty.text.setText(context.getString(R.string.no_previous_visits));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (visitReports == null || visitReports.isEmpty()) {
            return 1;   //return 1 to show the empty view
        }

        return visitReports.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (visitReports == null || visitReports.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public View layout;
        public TextView date;
        public TextView viewLink;
        public TextView doctorName;
        public TextView text;
        public TextView amount;

        public ViewHolder(final Context context, final View view) {
            super(view);
            this.view = view;
            layout = view.findViewById(R.id.previousvisit_item_layout);
            date = (TextView) view.findViewById(R.id.date);
            viewLink = (TextView) view.findViewById(R.id.view);
            doctorName = (TextView) view.findViewById(R.id.doctor_name);
            amount = (TextView) view.findViewById(R.id.amount);
            text = (TextView) view.findViewById(R.id.text);
        }

        public void setOnItemClickListener(final VisitReport visitReport, final RecyclerViewListener onItemClickListener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(visitReport, getAdapterPosition());
                }
            });
        }
    }

    public static class ViewHolderEmpty extends RecyclerView.ViewHolder {
        public View view;
        public TextView text;

        public ViewHolderEmpty(final Context context, final View view) {
            super(view);
            this.view = view;
            text = (TextView) view.findViewById(R.id.empty_text);
        }
    }
}
