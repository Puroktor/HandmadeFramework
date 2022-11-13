package ru.vsu.csf.skofenko.server.persistance;

public class CaseFormatter {
    public static String camelCaseToUnderscores(String camelCaseString) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCaseString
                .replaceAll(regex, replacement)
                .toLowerCase();
    }

    public static String underscoresToCamelCase(String underscoresString){
        StringBuilder builder = new StringBuilder(underscoresString);
        for (int i = 0; i < builder.length(); i++) {
            if (builder.charAt(i) == '_') {
                builder.deleteCharAt(i);
                builder.replace(i, i + 1, String.valueOf(Character.toUpperCase(builder.charAt(i))));
            }
        }
        return builder.toString();
    }
}
