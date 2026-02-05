import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static public String findPath(String fileName) {

        String path = System.getenv("PATH");

        if (path == null) return null;
        
        String[] directories = path.split(File.pathSeparator);

        for (String dir: directories) {
            // Create file objext for each directory
            File file = new File(dir, fileName); // Look for the file name in directories 

            if (file.exists() && file.canExecute()) {
                return file.getAbsolutePath(); // Return path of the first found file 
            }
        }
        return null;
    }

    static public ArrayList<String> textParser(String text) {

        ArrayList<String> arr = new ArrayList<>();
        StringBuilder newText = new StringBuilder();
        boolean isQuote = false; 

        for (int x = 0; x < text.length(); x++) {

            if (text.charAt(x) == ' ' && !isQuote) {
                if (newText.length() > 0) {
                    arr.add(newText.toString());
                    newText.setLength(0);;
                }
            } else if (text.charAt(x) == '\'') {
                isQuote = !isQuote; 
            } else {
                newText.append(text.charAt(x));
            }
        }

        arr.add(newText.toString());

        return arr;
    }


    public static void main(String[] args) throws Exception {
    

        Scanner scanner = new Scanner(System.in); // Start scanner

        String currentDirectory = System.getProperty("user.dir"); // Get current working directory 

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

            ArrayList<String> token = textParser(command);

            if(command.equals("exit")) break;     

            switch(tokens[0]) {
                
                case "echo" -> {  
                    
                    for (int i = 1; i < token.size(); i++) {
                        System.out.print(token.get(i) );
                        if (i < token.size()- 1) {System.out.print(" ");} // Print space until last element
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

                    if (tokens[1].contentEquals("~")) {
                        String path = System.getenv("HOME");
                        currentDirectory = path;
                        break;
                    }

                    File newPath = new File(tokens[1]); // Create file object 

                    // If the path doesn't start from the root, add the passed value to current directory 
                    if (!newPath.isAbsolute()) {

                        newPath = new File(currentDirectory, tokens[1]);
                    } 
                    //Check if the path exist
                    if (newPath.exists() && newPath.isDirectory()) {
                        currentDirectory = newPath.getCanonicalPath(); // Set canonical path
                    } else {
                        System.out.println("cd: " + tokens[1] + ": No such file or directory");
                    }
                }

                default -> {
               
                    String path = findPath(tokens[0]);

                    if (path == null) {
                        System.out.println(path + ": not found");
                        break;
                    }

                    //tokens[0] = path;         
                    ProcessBuilder pb = new ProcessBuilder(tokens);
                    pb.directory(new File(currentDirectory)); // Update directory
                    pb.inheritIO();
                    pb.start().waitFor();               
                
                }
            };       

       } 

        scanner.close();

    }
}
