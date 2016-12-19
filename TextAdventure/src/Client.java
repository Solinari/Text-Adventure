
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.util.ArrayList;

public class Client {

    static ArrayList<String> decisionsList = null;
    String intialScene = "";
    static BufferedReader input;
    static PrintWriter output;
    static Socket clientSock;
    static String currentScene = "";
    static boolean continueAdventure = false;
    

    public Client(String hostNameIn, int portNumIn) {
        // connects to server
        // grab i/o streams
        // wait for response (send/receive)

        decisionsList = new ArrayList<String>();
        
        try {

            clientSock = new Socket(hostNameIn, portNumIn);
            output = new PrintWriter(clientSock.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostNameIn);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostNameIn);
            System.exit(1);
        }

    }

    public static  void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        new Client(hostName, portNumber);
        String clientAdventureChoice = AdventureMain.getAdventureChoiceFromUserInput(AdventureMain.getValidAdventures());
        
        System.out.println("My adventure choice is: " + clientAdventureChoice);
        output.println(clientAdventureChoice);
        receive();
    }

    private static  void send(String choice) {
        output.println("Player chose: "+choice);
        output.flush();
    }

    private static  void receive() {
        try {
        
            String inputLine;
            while ((inputLine = input.readLine()) != null) {
            	
                if (inputLine.charAt(0) == 'Z') {
                    System.out.print("End of adventure, thanks for playing! :D");
                    // gracefully close if hit the end!
                    clientSock.close();
                    System.exit(-1);
                }
                
                // index of options
                Integer textToTheRightOfBar = inputLine.lastIndexOf("|") + 1;

                currentScene = inputLine.substring(0,textToTheRightOfBar-2);
                // Decision all in one string, seperated by a semicolon (org
                // implememtation)
                String decisionsWithSemiColons = inputLine.substring(textToTheRightOfBar,
                        inputLine.length());
                
                // Gives array of options as strings with story logic for console output
                String[] decisionsFormatted = decisionsWithSemiColons.split(";");
                // Separates the A-logic B-logic etc... for input checking 
                decisionsList = new ArrayList<String>();
               for(String decisionLineOfText : decisionsFormatted){
                	//String[] a = decisionLineOfText.split("-");
                	decisionsList.add(decisionLineOfText);
                	System.out.println("This is non split:"+decisionLineOfText);
                	//System.out.println(a[0]);
                }
                	//System.out.println("From server options: ");
                System.out.println(currentScene);
                System.out.println("\nChoices are:");
                for (String decision : decisionsFormatted) {
//                	if (decision.length()==1 && decision.startsWith("-")){
//                		
//                		System.out.print("End of adventure");
//                        // gracefully close if hit the end!
//                		output.println("End of adventure");
//                		
//                	}
                    System.out.println(decision+"\n");
                    //decisionsList.add(decision);

                }

                processInput();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        //}
    }

    static private  void processInput() {

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));

        try {
            String inputChoice = null;
            while ((!(decisionsList.contains(inputChoice)))) {
                if ((inputChoice = stdInput.readLine()) != null) {
                    //System.out.println(currentScene);
                    //System.out.println(AdventureMain.getChoicesWithFormating(decisionsList));
                    if (decisionsList.contains(inputChoice)) {
                        send(inputChoice);
                        break;
                    }
                    System.out.println("That is not an option! Enter one of the options above!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}
