package converter;

public class Converter {

    private static final String elementRegex = "<.*>.*<\\/.*>";
    private static final String emptyElementRegex = "<.*\\/>";

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

}
