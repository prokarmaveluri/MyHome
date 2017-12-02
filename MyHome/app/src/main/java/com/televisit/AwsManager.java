package com.televisit;

import android.content.Context;

import com.americanwell.sdk.AWSDK;
import com.americanwell.sdk.AWSDKFactory;
import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;
import com.americanwell.sdk.entity.visit.VisitReport;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.americanwell.sdk.logging.AWSDKLogger;
import com.prokarma.myhome.BuildConfig;

import java.util.List;

import timber.log.Timber;

import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_AUDIO;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_DEFAULT;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_MEDIA;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_NETWORKING;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_PERMISSIONS;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_VIDEO;
import static com.americanwell.sdk.logging.AWSDKLogger.LOG_CATEGORY_VISIT;

/**
 * Created by cmajji on 10/31/17.
 */

public class AwsManager {
    private static final AwsManager ourInstance = new AwsManager();
    private static AWSDK awsdk = null;

    //private HashMap<VisitReport, VisitReportDetail> visitReportDetailHashMap;
    private List<VisitReport> visitReports;
    private List<Allergy> allergies;
    private List<Condition> conditions;
    private List<Practice> practices;
    private List<Pharmacy> pharmacies;
    private List<Medication> medications;
    private Pharmacy consumerPharmacy;
    private Authentication authentication;
    private VisitContext visitContext;
    private Visit visit;
    private Consumer consumer;
    private Consumer dependent;
    private boolean hasMedicationsFilledOut;
    private State hasAllergiesFilledOut = State.NOT_FILLED_OUT;
    private State hasConditionsFilledOut = State.NOT_FILLED_OUT;
    private boolean hasInitializedAwsdk;
    private boolean hasAuthenticated;
    private boolean hasConsumer;

    public enum State {
        NOT_FILLED_OUT,
        FILLED_OUT_HAVE_NONE,
        FILLED_OUT_HAVE_FEW
    }

    public static AwsManager getInstance() {
        return ourInstance;
    }

    private AwsManager() {
    }

    public void init(Context context) {
        try {
            this.awsdk = AWSDKFactory.getAWSDK(context);

            if (BuildConfig.REPORT_LOGS) {
                awsdk.getDefaultLogger().setPriority(3); // set log level to debug - Log.DEBUG
            } else {
                awsdk.getDefaultLogger().setPriority(6); // set log level to error - Log.ERROR
            }

            // Set the categories for the logs you want displayed. Setting this to null will allow all categories to be displayed.
            @AWSDKLogger.AWSDKLogCategory
            String[] categories = {LOG_CATEGORY_DEFAULT, LOG_CATEGORY_VIDEO, LOG_CATEGORY_AUDIO,
                    LOG_CATEGORY_PERMISSIONS, LOG_CATEGORY_MEDIA, LOG_CATEGORY_VISIT, LOG_CATEGORY_NETWORKING};
            awsdk.getDefaultLogger().setLogCategories(categories);
        } catch (AWSDKInstantiationException e) {
            Timber.e(e);
        }
    }

    /**
     * This should be the only place you get the {@link AWSDK} instance object.
     *
     * @return
     */
    public AWSDK getAWSDK() {
        return awsdk;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public List<Practice> getPractices() {
        return practices;
    }

    public void setPractices(List<Practice> practices) {
        this.practices = practices;
    }

    public List<VisitReport> getVisitReports() {
        return visitReports;
    }

    public void setVisitReports(List<VisitReport> visitReports) {
        this.visitReports = visitReports;
    }

    /*public HashMap<VisitReport, VisitReportDetail> getVisitReportDetailHashMap() {
        if (visitReportDetailHashMap == null) {
            visitReportDetailHashMap = new HashMap<>();
        }
        return visitReportDetailHashMap;
    }

    public void setVisitReportDetailHashMap(HashMap<VisitReport, VisitReportDetail> visitReportDetailHashMap) {
        this.visitReportDetailHashMap = visitReportDetailHashMap;
    }*/

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;

        if (isHasConditionsFilledOut() == State.FILLED_OUT_HAVE_FEW) {
            setHasConditionsFilledOut(State.FILLED_OUT_HAVE_NONE);
        }
        for (Condition condition : conditions) {
            if (condition.isCurrent()) {
                setHasConditionsFilledOut(State.FILLED_OUT_HAVE_FEW);
                break;
            }
        }
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;

        if (isHasAllergiesFilledOut() == State.FILLED_OUT_HAVE_FEW) {
            setHasAllergiesFilledOut(State.FILLED_OUT_HAVE_NONE);
        }
        for (Allergy allergy : allergies) {
            if (allergy.isCurrent()) {
                setHasAllergiesFilledOut(State.FILLED_OUT_HAVE_FEW);
                break;
            }
        }
    }

    public List<Pharmacy> getPharmacies() {
        return pharmacies;
    }

    public void setPharmacies(List<Pharmacy> pharmacies) {
        this.pharmacies = pharmacies;
    }

    public Pharmacy getConsumerPharmacy() {
        return consumerPharmacy;
    }

    public void setConsumerPharmacy(Pharmacy consumerPharmacy) {
        this.consumerPharmacy = consumerPharmacy;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;

        if (medications != null && !medications.isEmpty()) {
            setHasMedicationsFilledOut(true);
        } else {
            setHasMedicationsFilledOut(false);
        }
    }

    public VisitContext getVisitContext() {
        return visitContext;
    }

    public void setVisitContext(VisitContext visitContext) {
        this.visitContext = visitContext;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public boolean isHasMedicationsFilledOut() {
        return hasMedicationsFilledOut;
    }

    public void setHasMedicationsFilledOut(boolean hasMedicationsFilledOut) {
        this.hasMedicationsFilledOut = hasMedicationsFilledOut;
    }

    public State isHasAllergiesFilledOut() {
        return hasAllergiesFilledOut;
    }

    public void setHasAllergiesFilledOut(State hasAllergiesFilledOut) {
        this.hasAllergiesFilledOut = hasAllergiesFilledOut;
    }

    public State isHasConditionsFilledOut() {
        return hasConditionsFilledOut;
    }

    public void setHasConditionsFilledOut(State hasConditionsFilledOut) {
        this.hasConditionsFilledOut = hasConditionsFilledOut;
    }

    public boolean isHasInitializedAwsdk() {
        return hasInitializedAwsdk;
    }

    public void setHasInitializedAwsdk(boolean hasInitializedAwsdk) {
        this.hasInitializedAwsdk = hasInitializedAwsdk;
    }

    public boolean isHasAuthenticated() {
        return hasAuthenticated;
    }

    public void setHasAuthenticated(boolean hasAuthenticated) {
        this.hasAuthenticated = hasAuthenticated;
    }

    public boolean isHasConsumer() {
        return hasConsumer;
    }

    public void setHasConsumer(boolean hasConsumer) {
        this.hasConsumer = hasConsumer;
    }

    public Consumer getDependent() {
        return dependent;
    }

    public void setDependent(Consumer dependent) {
        this.dependent = dependent;
    }
}
