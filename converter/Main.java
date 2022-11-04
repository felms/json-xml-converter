package converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File("test.txt"));
        String input  = "";
        while (scanner.hasNext()) {
            input += scanner.nextLine();
        }

        if (input.matches("<.*")) {
            System.out.println(Converter.XMLToJSON(input));
        } else {
            System.out.println(Converter.JSONToXML(input));
        }

    }
}
