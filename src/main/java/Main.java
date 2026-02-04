import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
    

        Scanner scanner = new Scanner(System.in); // Start scanner

        HashMap<String, String> arrguments = new HashMap<>();

        arrguments.put("echo",  " is a shell builtin");
        arrguments.put("exit", " is a shell builtin" );
        arrguments.put("type", " is a shell builtin" ); 

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
                    if(arrguments.containsKey(tokens[1])) {
                        System.out.println(tokens[1] + arrguments.get(tokens[1]));
                    } else {

                        String path = System.getenv("PATH");

                        boolean found = false;

                        if (path != null) {
                            
                            String[] directories = path.split(File.pathSeparator); 


                            for (String dir: directories) {
                                File file = new File(dir, tokens[1]);

                                if (file.exists() && file.canExecute()) {
                                    System.out.println(tokens[1] + " is " + file.getAbsolutePath());
                                    found = true;
                                    break;
                                } 
                            }

                            if (!found) {
                                System.out.println(tokens[1] + ": not found");
                            }
                        }
                    }
                        
                }

                default -> System.out.println(command + ": command not found");
            };        

       } 

        scanner.close();

    }
}
