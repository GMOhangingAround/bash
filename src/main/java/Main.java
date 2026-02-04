import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print("$ ");

        Scanner scanner = new Scanner(System.in); // Start scanner

        String command = scanner.nextLine(); // Read next line 

        System.out.println(command + ": command not found");

        scanner.close();
    }
}
