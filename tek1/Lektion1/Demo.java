import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Demo {
    public static void main(String[] args) throws Exception {
        System.out.println("demo running");
        findByName("Ed");
    }

    private static void findByName(String name) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("spotify_5.txt"));
        while(scanner.hasNextLine()){ // 9.55
            String line = scanner.nextLine().split("-")[1];
            if(line.contains(name)){
                System.out.println(line);
                return;
            }
        }
    }

    private static void readSongs() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("spotify_5.txt"));
        while(scanner.hasNextLine()){
            System.out.println(scanner.nextLine());
        }
    }

}
