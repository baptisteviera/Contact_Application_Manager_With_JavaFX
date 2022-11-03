package model;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Country {

    public static List<String> getCountryNames() {
        List<String> countryNames;
        String[] locales = Locale.getISOCountries();

        countryNames = Arrays.stream(locales)
                .map(countryCode -> (new Locale("", countryCode))
                        .getDisplayCountry()).sorted()
                .collect(Collectors.toList());

        return countryNames;
    }

}
