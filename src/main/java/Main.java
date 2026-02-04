import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
    

        Scanner scanner = new Scanner(System.in); // Start scanner


        while(true) {

            System.out.print("$ ");
        
            String command = scanner.nextLine(); // Read next line 

            String[] tokens = command.split(" ");

            if(command.equals("exit")) break;     

            switch(tokens[0]) {
                
                case "echo" -> System.out.println(command.split(" ", 2)[1]); // Seperate input into two parts and output second element
                    
                 /*{  for (int i = 1; i < tokens.length; i++) {
                        System.out.print(tokens[i] );
                        if (i < tokens.length - 1) {System.out.print(" ");} 
                    }
                    System.out.println();
                }*/
                default -> System.out.println(command + ": command not found");
            };        

       } 

        scanner.close();

    }
}
