package com.televisit.previousvisit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prokarma.myhome.R;
import com.prokarma.myhome.app.RecyclerViewListener;
import com.prokarma.myhome.features.appointments.Appointment;
import com.prokarma.myhome.utils.DateUtil;

import java.util.ArrayList;

/**
 * Created by veluri on 11/29/17.
 */

public class PreviousVisitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_EMPTY = 0;
    private final int VIEW_TYPE_NORMAL = 1;

    public final Context context;
    public ArrayList<Appointment> appointments;
    private final RecyclerViewListener onItemClickListener;
    private final boolean isPastAppointments;

    public PreviousVisitsAdapter(Context context, @Nullable ArrayList<Appointment> appointments, boolean isPastAppointments, RecyclerViewListener onItemClickListener) {
        this.context = context;
        this.appointments = appointments;
        this.onItemClickListener = onItemClickListener;
        this.isPastAppointments = isPastAppointments;
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
                Appointment appointment = appointments.get(position);

                if (appointment.doctorName != null && !appointment.doctorName.isEmpty()) {
                    holder.doctorName.setText(appointment.doctorName);
                }

                if (appointment.facilityName != null && !appointment.facilityName.isEmpty()) {
                    holder.facility.setText(appointment.facilityName);
                }

                if (appointment.appointmentStart != null && !appointment.appointmentStart.isEmpty()) {
                    holder.date.setText(DateUtil.getDateWordsFromUTC(appointment.appointmentStart));
                    holder.time.setText(DateUtil.getTime(appointment.appointmentStart) + " " + DateUtil.getReadableTimeZone(appointment));
                }

                holder.setOnItemClickListener(appointment, onItemClickListener);
                break;

            case VIEW_TYPE_EMPTY:
                ViewHolderEmpty holderEmpty = (ViewHolderEmpty) genericHolder;
                holderEmpty.text.setText(context.getString(R.string.no_previous_visits));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (appointments == null || appointments.isEmpty()) {
            return 1;   //return 1 to show the empty view
        }

        return appointments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (appointments == null || appointments.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public View layout;
        public TextView date;
        public TextView time;
        public TextView facility;
        public TextView doctorName;

        public ViewHolder(final Context context, final View view) {
            super(view);
            this.view = view;
            layout = view.findViewById(R.id.appointment_item_layout);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            facility = (TextView) view.findViewById(R.id.facility);
            doctorName = (TextView) view.findViewById(R.id.doctor_name);
        }

        public void setOnItemClickListener(final Appointment appointment, final RecyclerViewListener onItemClickListener) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(appointment, getAdapterPosition());
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
