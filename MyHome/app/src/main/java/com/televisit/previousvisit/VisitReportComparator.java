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
            if (o1.getDate() != null && o2.getDate() != null) {

                Date visitDate1 = DateUtil.getDateFromHyphens(o1.getDate().getYear() + "-" + o1.getDate().getMonth() + "-" + o1.getDate().getDay());
                Date visitDate2 = DateUtil.getDateFromHyphens(o2.getDate().getYear() + "-" + o2.getDate().getMonth() + "-" + o2.getDate().getDay());

                return visitDate2.compareTo(visitDate1);
            }
        } catch (ParseException e) {
            Timber.e(e);
            e.printStackTrace();
        }
        return 1;
    }
}
