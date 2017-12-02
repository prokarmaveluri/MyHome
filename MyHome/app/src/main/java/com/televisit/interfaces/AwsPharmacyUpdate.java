package com.televisit.interfaces;

import com.americanwell.sdk.entity.pharmacy.Pharmacy;

/**
 * Created by kwelsh on 12/1/17.
 */

public interface AwsPharmacyUpdate {
    void pharmacyUpdateComplete(Pharmacy pharmacy);
    void pharmacyUpdateFailed(String errorMessage);
}
