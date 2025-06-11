package com.iyuba.conceptEnglish.manager.sharedpreferences;

public interface Infos {
    // These strange keys' values are kept for compatibility and must not be modified!
    interface Keys {


        String DEVICE_ID = "device_id";

        String IS_AUTIO_READ = "IS_AUTIO_READ";

        String PRIVACY = "Privacy";
        String FIRST = "first_join";
        String PERSONAL = "setSupportPersonalized";
    }

    interface DefaultValue {

        String DEVICE_ID = "";

        boolean IS_AUTIO_READ = false;

        boolean PRIVACY = false;

        boolean PERSONAL = true;

    }
}
