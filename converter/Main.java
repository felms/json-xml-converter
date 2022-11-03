package converter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        if (input.matches("<.*")) {
            System.out.println(Converter.XMLToJSON(input));
        } else if (input.matches("\\{.*")) {
            System.out.println(Converter.JSONToXML(input));
        }

    }
}
