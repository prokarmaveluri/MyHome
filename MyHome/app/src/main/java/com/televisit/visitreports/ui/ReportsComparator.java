package com.televisit.visitreports.ui;

import com.americanwell.sdk.entity.visit.VisitReport;

import java.util.Comparator;

import timber.log.Timber;

/**
 * Created by veluri on 2/8/18.
 */

public class ReportsComparator implements Comparator<VisitReport> {
    @Override
    public int compare(VisitReport o1, VisitReport o2) {
        try {
            //0 comes when two date are same,
            //1 comes when date1 is lower then date2
            //-1 comes when date1 is higher then date2

            if (o1.getSchedule() != null && o1.getSchedule().getActualStartTime() != null
                    && o2.getSchedule() != null && o2.getSchedule().getActualStartTime() != null) {

                if (o1.getSchedule().getActualStartTime() > o2.getSchedule().getActualStartTime()) {
                    return -1;
                } else if (o1.getSchedule().getActualStartTime() < o2.getSchedule().getActualStartTime()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
        return 0;
    }
}