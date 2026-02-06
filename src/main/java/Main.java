import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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

       //for (char c : text.toCharArray()) {}

        for (int x = 0; x < text.length(); x++) {
            
            char c = text.charAt(x);
            

            if (singleQuote) {
                if (c == '\'') singleQuote = !singleQuote;
                else newText.append(c);
            } else if (doubleQuote) {
                if (c == '"') {doubleQuote = !doubleQuote;}
                else if (c == '\\' && x+1 < text.length())  {
                    char nextChar = text.charAt(x + 1);
                    if (nextChar == '\\' || nextChar == '"' || nextChar == '\n' || nextChar == '$') {
                    newText.append(nextChar); x++;} 
                    else newText.append(c);
                } else {newText.append(c);}
            } else if (c == ' ' && !singleQuote && !doubleQuote) {
                if (newText.length() > 0) {
                    arr.add(newText.toString());
                    newText.setLength(0);}
            } else if (c == '\"' && !singleQuote) {
                doubleQuote = !doubleQuote;
            } else if (c == '\'' && !doubleQuote) {
                singleQuote = !singleQuote; 
            }  else if (c == '\\' ) {
                if (x+1 < text.length()) {
                    newText.append(text.charAt(x+1));
                    x++;
                }
            } else {
                newText.append(c);
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

        // Write into history file
        FileWriter history = new FileWriter("history.txt");
        int num = 1;

        
  
        while(true) {

            System.out.print("$ ");
        
            String command = scanner.nextLine(); // Read next line 

            history.write(" " + num + " ");
            history.write(command); // Write each command into the file
            history.write("\n");
            num++;

            //String[] tokens = command.split(" ");

            ArrayList<String> token = textParser(command);

            if(command.equals("exit")) {
                break;
            }     

            switch(token.get(0)) {
                
                case "echo" -> {  
                    
                    for (int i = 1; i < token.size(); i++) {
                        System.out.print(token.get(i) );
                        if (i < token.size()- 1) {System.out.print(" ");} // Print space until last element
                    }
                    System.out.println();
                }

                case "history" -> {

                    history.flush();

                    File text = new File("history.txt"); // Create file object

                    Scanner read = new Scanner(text);
                    
                    if (token.size() > 1) {
                        
                        Integer val = Integer.parseInt(token.get(1));
                        long lineCount = Files.lines(Paths.get("history.txt")).count();
                        int skip = (int) lineCount - val; 

                        for(int i = skip; i > 0; i--) read.nextLine();
                    }
                    
                    
                    while(read.hasNextLine()) {
                        System.out.println(read.nextLine());
                    }

                    read.close();
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


        history.close();
        scanner.close();

    }
}
