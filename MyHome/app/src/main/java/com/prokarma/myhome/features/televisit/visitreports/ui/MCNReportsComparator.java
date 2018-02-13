package com.prokarma.myhome.features.televisit.visitreports.ui;

import android.support.annotation.IntDef;

import com.americanwell.sdk.entity.visit.VisitReport;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Comparator;

import timber.log.Timber;

/**
 * Created by veluri on 2/8/18.
 */

public class MCNReportsComparator implements Comparator<VisitReport> {

    @IntDef({DateComparision.SAME, DateComparision.BEFORE, DateComparision.AFTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DateComparision {
        int AFTER = -1;     //-1 comes when date1 is higher then date2
        int BEFORE = 1;     //1 comes when date1 is lower then date2
        int SAME = 0;       //0 comes when two dates are same
    }

    @Override
    public int compare(VisitReport o1, VisitReport o2) {
        try {
            if (o1.getSchedule() != null && o1.getSchedule().getActualStartTime() != null
                    && o2.getSchedule() != null && o2.getSchedule().getActualStartTime() != null) {

                if (o1.getSchedule().getActualStartTime() > o2.getSchedule().getActualStartTime()) {
                    return DateComparision.AFTER;
                } else if (o1.getSchedule().getActualStartTime() < o2.getSchedule().getActualStartTime()) {
                    return DateComparision.BEFORE;
                } else {
                    return DateComparision.SAME;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
        return 0;
    }
}