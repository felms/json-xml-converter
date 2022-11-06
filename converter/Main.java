package converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File("test.txt"));
        StringBuilder input  = new StringBuilder();
        while (scanner.hasNext()) {
            input.append(scanner.nextLine());
        }

        System.out.println(Converter.parseXML(input.toString()));
    }
}
