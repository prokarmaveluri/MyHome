package com.televisit;

import com.americanwell.sdk.entity.Authentication;
import com.americanwell.sdk.entity.consumer.Consumer;
import com.americanwell.sdk.entity.health.Allergy;
import com.americanwell.sdk.entity.health.Condition;
import com.americanwell.sdk.entity.health.Medication;
import com.americanwell.sdk.entity.pharmacy.Pharmacy;
import com.americanwell.sdk.entity.practice.Practice;
import com.americanwell.sdk.entity.visit.Visit;
import com.americanwell.sdk.entity.visit.VisitContext;

import java.util.List;

/**
 * Created by cmajji on 10/31/17.
 */

public class SDKUtils {
    private static final SDKUtils ourInstance = new SDKUtils();

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

    public static SDKUtils getInstance() {
        return ourInstance;
    }

    private SDKUtils() {
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

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;
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
}
