package converter;

import model.Element;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class Converter {

    private static final String xmlWithAttributes = "<.*\\s.*=.+>";


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

    public static String parseJSON(String jsonContents) {

        List<Element> elements = new ArrayList<>();

        // Read the tags/elements
        StringBuilder sb = new StringBuilder();
        Deque<String> json = new ArrayDeque<>(List.of(jsonContents.split("")));
        List<String> path = new ArrayList<>();

        while (!json.isEmpty()) {

            if (json.peek().equals("{") && sb.isEmpty()) {
                json.poll();
            } else if (json.peek().equals(":")) {
                path.add(sb.toString().trim());
                sb = new StringBuilder();
                json.poll();
            } else if (json.peek().equals("}") && sb.toString().trim().isBlank()) {
                path.remove(path.size() - 1);
                json.poll();
            } else if (json.peek().equals("}") && !sb.toString().trim().isBlank()) {
                String value = sb.toString().trim();
                elements.add(new Element(
                        String.join(", ", path),
                        value
                ));
                sb = new StringBuilder();
                path.remove(path.size() - 1);
                json.poll();
            } else if (json.peek().equals(",")) {
                String value = sb.toString().trim();
                elements.add(new Element(
                        String.join(", ", path),
                        value
                ));
                sb = new StringBuilder();
                path.remove(path.size() - 1);
                json.poll();
            } else if (json.peek().equals("{") && !sb.isEmpty()) {
                elements.add(new Element(String.join(", ", path)));
                sb = new StringBuilder();
                json.poll();
            } else {
                sb.append(json.poll());
            }
        }

        return elements.stream()
                .map(Element::toString)
                .collect(Collectors.joining("\n"));
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

}
