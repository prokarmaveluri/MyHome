package com.prokarma.myhome.features.profile;

import java.util.ArrayList;

/**
 * Created by kwelsh on 4/28/17.
 */

public class ProfileResponse {

    public Profile result;
    public boolean isValid;
    public ArrayList<Errors> errors;
    public ArrayList<Warnings> warnings;

}