package model;

public class Attribute {

    private final String attributeKey;
    private final String attributeValue;


    public Attribute(String attributeKey, String attributeValue) {
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", this.attributeKey, this.attributeValue);
    }
}
