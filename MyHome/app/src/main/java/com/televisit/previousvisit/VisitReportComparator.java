package com.televisit.previousvisit;

import com.americanwell.sdk.entity.visit.VisitReport;
import com.prokarma.myhome.utils.DateUtil;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by veluri on 12/3/17.
 */

public class VisitReportComparator implements Comparator<VisitReport> {
    @Override
    public int compare(VisitReport o1, VisitReport o2) {
        try {
            //0 comes when two date are same,
            //1 comes when date1 is lower then date2
            //-1 comes when date1 is higher then date2

            if (o1.getDate() != null && o2.getDate() != null) {

                if (o1.getDate().toDate().getTime() > o2.getDate().toDate().getTime()) {
                    return -1;
                }
                else if (o1.getDate().toDate().getTime() < o2.getDate().toDate().getTime()) {
                    return 1;
                }
                else {
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
