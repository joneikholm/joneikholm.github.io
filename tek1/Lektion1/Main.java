import java.io.*;
import java.util.*;

class ReadNames {
    private static String fileName="myshop.txt";
    public static void main(String[] args) throws Exception {
        //updateProduct("Wireless_Mouse", "29.99");
        //readFile();
        //System.out.println(findByName("Bluetooth_Headphones"));
        //System.out.println(findCheapest());
        convertToCsv("myshop.csv");
    }

    static void convertToCsv(String outputFile) throws Exception {
        Scanner sc = new Scanner(new File(fileName));
        PrintWriter pw = new PrintWriter(outputFile);
        pw.println("Name,Price,Quantity");
        while (sc.hasNextLine()) {
            String[] parts = sc.nextLine().split(" ");
            pw.println(parts[0] + "," + parts[1] + "," + parts[2]);
        }
        pw.close();
    }

    static void updateProduct(String name, String price) throws Exception {
        Scanner sc = new Scanner(new File(fileName));
        StringBuilder sb = new StringBuilder();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith(name)) {
                String[] parts = line.split(" ");
                sb.append(parts[0] + " " + price + " " + parts[2] + "\n");
            } else {
                sb.append(line + "\n");
            }
        }
        PrintWriter pw = new PrintWriter(fileName);
        pw.print(sb);
        pw.close();
    }

    static String findCheapest() throws Exception {
        Scanner sc = new Scanner(new File(fileName));
        String cheapest = null;
        double minPrice = Double.MAX_VALUE;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            double price = Double.parseDouble(line.split(" ")[1]);
            if (price < minPrice) {
                minPrice = price;
                cheapest = line;
            }
        }
        return cheapest;
    }

    static String findByName(String name) throws Exception {
        Scanner sc = new Scanner(new File(fileName));
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith(name)) {
                return line;
            }
        }
        return null;
    }

    private static void readFile() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        while (sc.hasNextLine()) {
            System.out.println(sc.nextLine());
        }
    }

    private static void readNames() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fileName));
        while (sc.hasNextLine()) {
            System.out.println(sc.nextLine().split(" ")[0]);
        }
    }
}