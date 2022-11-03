package converter;

public class Main {
    public static void main(String[] args) {
        System.out.println(Converter.XMLToJSON("<host>127.0.0.1</host>"));
        System.out.println(Converter.XMLToJSON("<jdk>1.8.9</jdk>"));
        System.out.println(Converter.XMLToJSON("<success/>"));
        System.out.println(Converter.XMLToJSON("<storage/>"));
    }
}
