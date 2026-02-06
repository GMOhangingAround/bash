import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
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
        boolean singleQuote = false;
        boolean doubleQuote = false;
        boolean isBacklash = false;   

       //for (char c : text.toCharArray()) {}

        for (int x = 0; x < text.length(); x++) {

            if (isBacklash) {
                newText.append(text.charAt(x));
                isBacklash = !isBacklash;

            } else if (text.charAt(x) == ' ' && !singleQuote && !doubleQuote) {
                if (newText.length() > 0) {
                    arr.add(newText.toString());
                    newText.setLength(0);;
                }
            } else if (text.charAt(x) == '\"' && !singleQuote) {
                doubleQuote = !doubleQuote;
            } else if (text.charAt(x) == '\'' && !doubleQuote) {
                singleQuote = !singleQuote; 
            }  else if (text.charAt(x) == '\\' ) {
                isBacklash = !isBacklash;
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

        // Get builtins values and description
        Properties builtins = new Properties();
        builtins.load(new FileInputStream("builtins.conf"));

        
  
        while(true) {

            System.out.print("$ ");
        
            String command = scanner.nextLine(); // Read next line 

            //String[] tokens = command.split(" ");

            ArrayList<String> token = textParser(command);

            if(command.equals("exit")) break;     

            switch(token.get(0)) {
                
                case "echo" -> {  
                    
                    for (int i = 1; i < token.size(); i++) {
                        System.out.print(token.get(i) );
                        if (i < token.size()- 1) {System.out.print(" ");} // Print space until last element
                    }
                    System.out.println();
                }

                case "type" -> {

                    if (token.size() == 1) {break;}

                    if(builtins.containsKey(token.get(1))) {
                        System.out.println(token.get(1) + " " + builtins.getProperty(token.get(1)));
                    } else {

                        String fileName = token.get(1);
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
                    
                    if (token.size() < 2) {
                        break; 
                    }

                    if (token.get(1).contentEquals("~")) {
                        String path = System.getenv("HOME");
                        currentDirectory = path;
                        break;
                    }

                    File newPath = new File(token.get(1)); // Create file object 

                    // If the path doesn't start from the root, add the passed value to current directory 
                    if (!newPath.isAbsolute()) {

                        newPath = new File(currentDirectory, token.get(1));
                    } 
                    //Check if the path exist
                    if (newPath.exists() && newPath.isDirectory()) {
                        currentDirectory = newPath.getCanonicalPath(); // Set canonical path
                    } else {
                        System.out.println("cd: " + token.get(1) + ": No such file or directory");
                    }
                }

                default -> {
               
                    String path = findPath(token.get(0));

                    if (path == null) {
                        System.out.println(token.get(0) + ": command not found");
                        break;
                    }
      
                    ProcessBuilder pb = new ProcessBuilder(token);
                    pb.directory(new File(currentDirectory)); // Update directory
                    pb.inheritIO();
                    pb.start().waitFor();               
                
                }
            };       

       } 

        scanner.close();

    }
}
