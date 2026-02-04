import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
    

        Scanner scanner = new Scanner(System.in); // Start scanner

        ArrayList<String> arrguments = new ArrayList<>(List.of("echo", "exit", "type"));
        String[] description = {" is a shell builtin", ": not found"};


        while(true) {

            System.out.print("$ ");
        
            String command = scanner.nextLine(); // Read next line 

            String[] tokens = command.split(" ");

            if(command.equals("exit")) break;     

            switch(tokens[0]) {
                
                case "echo" -> {  
                    
                    for (int i = 1; i < tokens.length; i++) {
                        System.out.print(tokens[i] );
                        if (i < tokens.length - 1) {System.out.print(" ");} // Print space until last element
                    }
                    System.out.println();
                }

                case "type" -> {
                    
                    if(arrguments.contains(tokens[1])){
                        System.out.println(tokens[1] + description[0]);
                    } else {
                        System.out.println(tokens[1] + description[1]);
                    };
                }

                default -> System.out.println(command + ": command not found");
            };        

       } 

        scanner.close();

    }
}
