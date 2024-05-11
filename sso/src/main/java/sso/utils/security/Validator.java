package sso.utils.security;

import org.json.JSONException;
import org.json.JSONObject;
import sso.utils.exceptions.ValidationException;

import java.util.regex.Pattern;

public class Validator {

    public static void validatePattern(String pattern, String valString) {
        if (valString == null || !Pattern.matches(pattern, valString)) {
            throw new ValidationException(valString + " violates " + pattern + " pattern");
        }
    }

    public static void validateAttributesString(String attributes) {
        try {
            new JSONObject(attributes);
        } catch (JSONException e) {
            throw new ValidationException("attributes have invalid json format");
        }
    }
}
