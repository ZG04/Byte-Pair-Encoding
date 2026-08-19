import java.util.Scanner;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter token file path:");
        String tokenFile = sc.nextLine();

        TokenMap map = FileHelper.loadTokens(tokenFile);

        EncodedList list = new EncodedList(map);

        System.out.println("Enter input text file path:");
        String inputFile = sc.nextLine();

        FileHelper.appendFile(list, inputFile);

        System.out.println("Enter output file path to save:");

        String outFile = sc.nextLine();
        FileHelper.storeEncodedList(list, outFile);

        saveFile(list, outFile + ".sep", map);

        System.out.println("Compressed");

        sc.close();
    }

    public static void saveFile(EncodedList list, String outFile, TokenMap map) {
        try (PrintWriter writer = new PrintWriter(outFile)) {
            writer.println("Length: " + list.size());
            for (int i = 0; i < list.size(); i++) {

                EncodingValue value = list.get(i);
                String expand = value.expandAsString(map);

               if (i > 0) {

                   writer.print("|");

                }
                writer.print(expand);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}





