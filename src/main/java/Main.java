import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
    

        Scanner scanner = new Scanner(System.in); // Start scanner

        boolean run = true;

        while(run) {

            System.out.print("$ ");
        
            String command = scanner.nextLine(); // Read next line 

            if(command.equals("exit")) {
                run = false;
            } else {
                System.out.println(command + ": command not found");
            }

       } 

        scanner.close();

    }
}
