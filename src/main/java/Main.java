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
                case "echo" : {

                    for (int i = 1; i < tokens.length; i++) {
                        System.out.print(tokens[i] + " ");
                    }
                    System.out.println();
                    break;
                }
                default: System.out.println(command + ": command not found");
            };

                  

       } 

        scanner.close();

    }
}
