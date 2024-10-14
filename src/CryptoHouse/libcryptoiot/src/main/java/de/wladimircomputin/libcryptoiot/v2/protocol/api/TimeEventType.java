package de.wladimircomputin.libcryptoiot.v2.protocol.api;

import android.widget.Switch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TimeEventType {
    DISABLED("disabled"),
    SUNRISE("sunrise"),
    SUNSET("sunset"),
    TIME("time")
    ;

    private final String text;

    TimeEventType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TimeEventType fromString(String type){

        if(type.equalsIgnoreCase(SUNRISE.text)){
            return SUNRISE;
        } else if(type.equalsIgnoreCase(SUNSET.text)){
            return SUNSET;
        } else if(type.equalsIgnoreCase(DISABLED.text)) {
            return DISABLED;
        } else if(type.equalsIgnoreCase(TIME.text)) {
            return TIME;
        } else{
            Pattern pattern = Pattern.compile("\\d\\d:\\d\\d", Pattern.CASE_INSENSITIVE);//searching for matches with the pattern will be done case-insensitively.
            Matcher matcher = pattern.matcher(type);
            if(matcher.matches()){
                return TIME;
            }
        }
        return DISABLED;
    }
}
