import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
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


    public static List<String> textFileReader(String textName, int value) throws Exception {

        
        List<String> fileReader = new ArrayList<>();
        File text = new File(textName); // Create file object
        Scanner read = new Scanner(text);
        int count = 0;

        while(read.hasNextLine()) {
            fileReader.add(read.nextLine());
            count++;
        }
                    
        if (value == 0) {
            read.close(); 
            return fileReader;}
    
        int skip = 0;
        if (count - value > 0) skip = count - value;
        else skip = value; 
        

                   
        read.close();

        return fileReader.subList(skip, count);
    }






    public static void main(String[] args) throws Exception {
    

       // Scanner scanner = new Scanner(System.in); // Start scanner

        String currentDirectory = System.getProperty("user.dir"); // Get current working directory 

        // Get builtins values and description
        Properties builtins = new Properties();
        builtins.load(new FileInputStream("builtins.conf"));

        // Write into history file
        FileWriter history = new FileWriter("history.txt");
        int num = 1;

        /* Eneter raw mode to detect each caracter */ 
        new ProcessBuilder("stty", "raw", "-echo").inheritIO().start().waitFor(); 

        StringBuilder sb = new StringBuilder();
        List<String> historyInputs = new ArrayList<>();
        int pointer = historyInputs.size();
  
        while(true) {

            System.out.print("\r$ ");
            
            while(true) {
                
                int c = System.in.read();

                if (c == 27) {
                    int next1 = System.in.read();
                    int next2 = System.in.read();

                    if (next1 == 91 && next2 == 65) {
                        // Arrow up

                        if (pointer > 0) {
                            pointer--;
                            if (pointer == historyInputs.size()) {
                                sb.setLength(0);
                            } else {
                                sb.setLength(0);
                                sb.append(historyInputs.get(pointer));
                            }
                        }
                    }

                    if (next1 == 91 && next2 == 66) {
                        // Arrow down
                        if (pointer < historyInputs.size()) {
                            pointer++;
                            if (pointer == historyInputs.size()) {
                                sb.setLength(0);
                            } else {
                                sb.setLength(0);
                                sb.append(historyInputs.get(pointer)); 
                            }
                        }
                    }
                    System.out.print("\u001B[0G"); // Go to the lines front
                    System.out.print("\u001B[2K"); // Clear entire line
                    System.out.print("$ " + sb.toString()); // Print new buffer value
                    System.out.flush();

                } else if (c == 13 || c == 10) {
                    //System.out.println();
                    System.out.print("\r\n"); // Enter 
                    break;
                } else if (c == 127) {
                    // Delete last character
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() -1);
                        System.out.print("\b \b");
                        System.out.flush();
                    }
                } else {
                    sb.append((char) c); // Cast int to char value
                    System.out.print((char) c);
                    System.out.flush();
                }
        
            }

            //String command = scanner.nextLine(); // Read next line 

            String command = sb.toString();

            history.write(" " + num + " ");
            history.write(command); // Write each command into the file
            history.write("\n");
            num++;
            history.flush();

            historyInputs.add(command);
            sb.setLength(0);
            pointer = historyInputs.size();

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
                    
                    int size = 0;
                    
                    try {
                    if (token.size() > 1) {
                        if (token.get(1).equals("-r")) {
                            File read = new File(token.get(2));
                            Scanner getText = new Scanner(read);

                            while (getText.hasNextLine()) {
                                historyInputs.add(getText.nextLine());
                            }

                            getText.close();
                        } else {
                            String s = token.get(1);
                            size = Integer.parseInt(s);
                        }                  
                    }

                    List<String> lines = textFileReader("history.txt", size);

                    for (String line: lines) {
                        System.out.print(line + "\r\n");
                        }
                   } catch (Exception e) {
                        System.out.print("\n");
                   }
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
                    new ProcessBuilder("stty", "cooked", "echo").inheritIO().start().waitFor();
                    pb.start().waitFor();    
                    /* Eneter raw mode to detect each caracter */ 
                    new ProcessBuilder("stty", "raw", "-echo").inheritIO().start().waitFor();        
                
                }
            };       

       } 
        new ProcessBuilder("stty", "cooked", "echo").inheritIO().start().waitFor();
        history.close();
        //scanner.close();

    }
}
