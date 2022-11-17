package model;

import java.util.ArrayList;
import java.util.List;

public class Element {

    private final String elementPath;
    private final String elementValue;
    private final List<Attribute> attributes;

    public Element(String elementPath) {
        this(elementPath, "");
    }

    public Element(String elementPath, String elementValue) {
        this.elementPath = elementPath;
        this.elementValue = elementValue;

        this.attributes = new ArrayList<>();
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    @Override
    public String toString() {
        return "Element:\n" +
                "path = " + elementPath + "\n" +
                (this.elementValue.isBlank() ? "" : ("value = " + elementValue + "\n")) +
                (this.attributes.isEmpty() ? "" : "attributes:\n" + attributes) +
                "\n";
    }
}
