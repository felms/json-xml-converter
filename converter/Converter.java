package converter;

import java.util.Arrays;

public class Converter {

    private static final String elementRegex = "<.*>.*<\\/.*>";

    private static final String emptyElementRegex = "<.*\\/>";
    private static final String withAttributes = "<.*\\s.*=.+>";

    private static final String emptyValueRegex = "\\{.*(null)\\}";

    private static final String keyValueRegex = "\\{.*\\}";


    public static String XMLToJSON(String xmlContents) {

        if (xmlContents.matches(emptyElementRegex)) {

            if (xmlContents.matches(withAttributes)) {

                String tag = xmlContents.replaceAll("[<\\/>]", "").split("\s+")[0];
                String attributes = getXMLAttributes(xmlContents);

                return "{" +
                        String.format("\"%s\" : {", tag) +
                        attributes +
                        String.format("\"#%s\" : null}}", tag);

            } else {
                String s = xmlContents.substring(0, xmlContents.length() - 2).substring(1);
                return String.format("{\"%s\": null}", s);
            }

        } else if (xmlContents.matches(elementRegex)) {

            if (xmlContents.matches(withAttributes)) {

                String tag = xmlContents.replaceAll("[<\\/>]", "").split("\s+")[0];
                String contents = xmlContents.replaceAll("<\\/.*>", "").replaceAll("<.*>", "");
                String attributes = getXMLAttributes(xmlContents);

                return "{" +
                        String.format("\"%s\" : {", tag) +
                        attributes +
                        String.format("\"#%s\" : \"%s\"}}", tag, contents);

            } else {
                String tag = xmlContents.substring(1).replaceAll(">.*", "");
                String contents = xmlContents.replaceAll("<\\/.*>", "").replaceAll("<.*>", "");

                return String.format("{\"%s\": \"%s\"}", tag, contents);
            }
        } else {
            return "{}";
        }
    }

    public static String JSONToXML(String jsonContents) {

        if (jsonContents.matches(emptyValueRegex)) {
            String key = jsonContents.replaceAll(":.*", "")
                    .substring(1)
                    .replaceAll("\"", "")
                    .trim();
            return String.format("<%s/>", key);
        } else if (jsonContents.matches(keyValueRegex)) {
            String[] keyValue = jsonContents.replaceAll("[{}]", "")
                    .split(":");

            String key = keyValue[0].trim().replaceAll("\"", "");
            String value = keyValue[1].trim().replaceAll("\"", "");

            return String.format("<%s>%s</%s>", key, value, key);
        } else {
            return "<>";
        }
    }

    private static String getXMLAttributes(String xmlContents) {
        String attributes = xmlContents.replaceAll(">.+", "").replaceAll("<[\\w]+\\s+|\\/>", "");
        String[] attributesArray = attributes.replaceAll("\"", "")
                .replaceAll("\\s+=\\s+", "*=*")
                .split("\\s+");

        StringBuilder sb = new StringBuilder();
        for (String attribute :attributesArray) {
            String[] keyValue = attribute.split("\\*=\\*");
            sb.append(String.format("\"@%s\" : \"%s\",", keyValue[0], keyValue[1]));
        }

        return sb.toString();
    }

}
