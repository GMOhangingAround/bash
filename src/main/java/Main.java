import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
    

        Scanner scanner = new Scanner(System.in); // Start scanner

        while(true) {

            System.out.print("$ ");
        
            String command = scanner.nextLine(); // Read next line 

            if(command.equals("exit")) break; 
         
            System.out.println(command + ": command not found");
            

       } 

        scanner.close();

    }
}
