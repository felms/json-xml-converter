package converter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Converter {

    private static final String elementRegex = "<.*>.*<\\/.*>";
    private static final String emptyElementRegex = "<.*\\/>";
    private static final String xmlWithAttributes = "<.*\\s.*=.+>";

    private static final String emptyValueRegex = "\\{.*(null)\\}";
    private static final String keyValueRegex = "\\{.*\\}";


    public static String XMLToJSON(String xmlContents) {

        if (xmlContents.matches(emptyElementRegex)) {

            if (xmlContents.matches(xmlWithAttributes)) {

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

            if (xmlContents.matches(xmlWithAttributes)) {

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

        if (jsonContents.contains("#")){
            String key = jsonContents.split(": \\{")[0].replaceAll("[\\{\"]", "").trim();
            String attributes = getJSONAttributes(jsonContents);
            String value = jsonContents.split("#")[1]
                    .replaceAll("[\\}\"]", "")
                    .split(":")[1]
                    .trim();

            if (value.equals("null")) {
                return String.format("<%s %s />", key, attributes);
            } else {
                return String.format("<%s %s>%s</%s>", key, attributes, value, key);
            }

        } else if (jsonContents.matches(emptyValueRegex)) {
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

    public static String parseXML(String xmlContents) {


        // Read the tags/elements
        StringBuilder sb = new StringBuilder();

        Deque<String> xml = new ArrayDeque<>(List.of(xmlContents.split("")));
        Deque<String> xmlTagsAndElements = new ArrayDeque<>();

        while (!xml.isEmpty()) {
            if (xml.peek().equals("<") && !sb.isEmpty()) {
                xmlTagsAndElements.offer(sb.toString().trim());
                sb = new StringBuilder();
                sb.append(xml.poll());
            } else if (xml.peek().equals(">")) {
                sb.append(xml.poll());
                xmlTagsAndElements.offer(sb.toString().trim());
                sb = new StringBuilder();
            } else {
                sb.append(xml.poll());
            }
        }

        // Parse the tags/elements
        sb = new StringBuilder();
        List<String> path = new ArrayList<>();

        while (!xmlTagsAndElements.isEmpty()) {
            String node = xmlTagsAndElements.poll().trim();
            //System.out.println("Nodes: ");
            //System.out.println(node);

            if (node.matches("<\\/.+>") && !path.isEmpty()) {
                path.remove(path.size() - 1);
            } else if (node.matches("<.+\\/>")) {
                String tag = node.replaceAll("<|(\\/>)", "").split("\\s+")[0];
                path.add(tag);
                String stringPath = String.join(", ", path);

                String attributes = "";
                if (node.matches(xmlWithAttributes)) {
                    attributes = getXMLAttributes(node);
                }

                sb.append(
                        String.format("""
                                Element:
                                path = %s
                                value = null""", stringPath)
                );

                if (!attributes.isBlank()) {
                    sb.append(
                            String.format("""
                                    \nattributes:
                                    %s"
                                    """, attributes)
                    );
                } else {
                    sb.append("\n");
                }

                sb.append("\n");

                path.remove(path.size() - 1);

            } else if (node.matches("<.+>")) {
                String tag = node.replaceAll("[<>]", "").split("\\s+")[0];
                path.add(tag);
                String stringPath = String.join(", ", path);

                String attributes = "";
                if (node.matches(xmlWithAttributes)) {
                    attributes = getXMLAttributes(node);
                }

                sb.append(
                        String.format("""
                                Element:
                                path = %s""", stringPath)
                );

                String next = xmlTagsAndElements.peek().trim();
                String closingThisTag = "</" + tag + ">";
                if (next.matches(closingThisTag)) {
                    sb.append("\nvalue = \"\"");
                } else if (!next.isBlank() && !next.contains("<")) {
                    sb.append(String.format("\nvalue = \"%s\"", next));
                    xmlTagsAndElements.poll();
                }

                if (!attributes.isBlank()) {
                    sb.append(
                            String.format("""
                                    \nattributes:
                                    %s
                                    """, attributes)
                    );
                } else {
                    sb.append("\n");
                }

                sb.append("\n");
            }
        }

        return sb.toString().trim();
    }

    private static String getXMLAttributes(String xmlContents) {
        String attributes = xmlContents.replaceAll("<[\\w]+\\s+|\\/>|>", "");
        String[] attributesArray = attributes.replaceAll("\"", "")
                .replaceAll("\\s+=\\s+", "*=*")
                .split("\\s+");

        StringBuilder sb = new StringBuilder();
        for (String attribute : attributesArray) {
            String[] keyValue = attribute.split("(\\*=\\*)|(=)");
            sb.append(String.format("%s = \"%s\"\n", keyValue[0], keyValue[1]));
        }

        return sb.toString().trim();
    }

    private static String getJSONAttributes(String jsonContents) {

        String attributes = jsonContents.split(": \\{")[1]
                .split("#")[0]
                .replaceAll("\\{", "")
                .trim();
        String[] attributesArray = attributes.split(",");
        StringBuilder sb = new StringBuilder();

        for (String s : attributesArray) {
            String att = s.replaceAll("\"", "").trim();
            if (att.isEmpty()) {
                continue;
            }
            String[] keyValue = att.split("\s*:\s*");
            String key = keyValue[0].replaceAll("[\"@,]", "").trim();
            String value = keyValue[1].replaceAll("\"", "").trim();
            sb.append(String.format("%s = \"%s\" ", key, value));
        }

        return sb.deleteCharAt(sb.length() - 1)
                .toString();
    }
}
