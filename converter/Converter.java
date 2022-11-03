package converter;

public class Converter {

    private static final String elementRegex = "<.*>.*<\\/.*>";
    private static final String emptyElementRegex = "<.*\\/>";
    private static final String emptyValueRegex = "\\{.*(null)\\}";
    private static final String keyValueRegex = "\\{.*\\}";


    public static String XMLToJSON(String xmlContents) {

        if (xmlContents.matches(emptyElementRegex)) {
            String s = xmlContents.substring(0, xmlContents.length() - 2).substring(1);
            return String.format("{\"%s\": null}", s);
        } else if (xmlContents.matches(elementRegex)) {
            String tag = xmlContents.substring(1).replaceAll(">.*", "");
            String contents = xmlContents.replaceAll("<\\/.*>", "").replaceAll("<.*>", "");

            return String.format("{\"%s\": \"%s\"}", tag, contents);
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

}
