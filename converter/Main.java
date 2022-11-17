package converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        //Scanner scanner = new Scanner(new File("test.txt"));
        //StringBuilder input  = new StringBuilder();
        //while (scanner.hasNext()) {
        //    input.append(scanner.nextLine());
        //}

        //System.out.println(Converter.parseXML(input.toString()));

        String input = """
                {
                    "elem1": {
                        "@attr1": "val1",
                        "@attr2": "val2",
                        "#elem1": {
                            "elem2": {
                                "@attr3": "val3",
                                "@attr4": "val4",
                                "#elem2": "Value1"
                            },
                            "elem3": {
                                "@attr5": "val5",
                                "@attr6": "val6",
                                "#elem3": "Value2"
                            }
                        }
                    }
                }""";

        System.out.println(Converter.parseJSON(input));
    }
}
