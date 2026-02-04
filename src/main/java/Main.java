import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static public String findPath(String fileName) {

        String path = System.getenv("PATH");

        if (path == null) return null;
        
        String[] directories = path.split(File.pathSeparator);

        for (String dir: directories) {
            File file = new File(dir, fileName); // Look for the file name in directories 

            if (file.exists() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
    

        Scanner scanner = new Scanner(System.in); // Start scanner

        String currentDirectory = System.getProperty("user.dir");

        HashMap<String, String> arrguments = new HashMap<>();

        arrguments.put("echo",  " is a shell builtin");
        arrguments.put("exit", " is a shell builtin" );
        arrguments.put("type", " is a shell builtin" ); 
        arrguments.put("pwd", " is a shell builtin");
        arrguments.put("cd", " is a shell builtin");

        
        
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

                    if (tokens.length == 1) {break;}

                    if(arrguments.containsKey(tokens[1])) {
                        System.out.println(tokens[1] + arrguments.get(tokens[1]));
                    } else {

                        String fileName = tokens[1];
                        String path = findPath(fileName);
                        boolean found = false;

                        if (path != null) {
                            System.out.println(fileName + " is " + path);
                            found = true;
                            break;
                        }

                        if (!found) {
                            System.out.println(fileName + ": not found");
                        }
                    }
                }

                case "pwd" -> {
                    System.out.println(currentDirectory); 
                }

                case "cd" -> {
                    
                    if (tokens.length < 2) {
                        break; 
                    }

                    File newPath = new File(tokens[1]);


                    if (!newPath.isAbsolute()) {

                        newPath = new File(currentDirectory, tokens[1]);
                    } 
                    
                    if (newPath.exists() && newPath.isDirectory()) {
                        currentDirectory = newPath.getCanonicalPath(); 
                    } else {
                        System.out.println("cd: " + tokens[1] + ": No such file or directory");
                    }
                }

                default -> {
               
                    String path = findPath(tokens[0]);

                    if (path == null) {
                        System.out.println(tokens[0] + ": not found");
                        break;
                    }

                    //tokens[0] = path;         
                    ProcessBuilder pb = new ProcessBuilder(tokens);
                    pb.directory(new File(currentDirectory));
                    pb.inheritIO();
                    pb.start().waitFor();               
                
                }
            };       

       } 

        scanner.close();

    }
}
