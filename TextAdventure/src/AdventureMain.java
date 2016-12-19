import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class AdventureMain {

	// get user input
	public static String getAdventureChoiceFromUserInput(HashMap<String, String> validAdventures) {

		String out = "";

		try {

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			String test;

			System.out.print("Please Enter the corresponding letter of the Adventure you'd like to play:\n");

			System.out.println("Valid Adventures are: " + validAdventures.toString() + "\n");

			// !(test = input.readLine()).equals("")
			while ((test = input.readLine()) != null) {

				if (validAdventures.containsKey(test)) {
					System.out.println("Now Playing: " + validAdventures.get(test) + " !!\n");
					out = validAdventures.get(test) + ".txt";
					break;
				}
			}
			System.out.println("Disengaging: Adventure Selection Mode!!!\n");
			//input.close();
		} catch (IOException io) {

			io.printStackTrace();

		}
		
		return out;
	}
	
	public static String getNextSceneChoiceFromUserInput(ArrayList<String> nextChoices) {

		String out = "";

		try {

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			String test;

			// !(test = input.readLine()).equals("")
			while ((test = input.readLine()) != null) {

				if (nextChoices.contains(test)) {
					System.out.println("Now heading to: " + test + " !!\n");
					out = test;
					break;
				}
			}
			System.out.println("Our Hero Progresses from this moment to...\n");
			//input.close();
		} catch (IOException io) {

			io.printStackTrace();

		}
		
		return out;
	}

	public static HashMap<String, String> getValidAdventures() {

		HashMap<String, String> validAdventures = new HashMap<String, String>();
		String adventures = addPathToFileName("Adventures.txt");
		String inputadventure = readFile(adventures);
		try {
			// buffer for file in
			BufferedReader input = new BufferedReader(new StringReader(inputadventure));
			String line = null;

			// read each line
			while ((line = input.readLine()) != null) {

				// split along the delimiters
				String[] lineParse;
				lineParse = line.split(Pattern.quote("|"));

				if (lineParse.length == 2) {

					validAdventures.put(lineParse[0], lineParse[1]);

				}

			}
		} catch (IOException e) {
			// if file not found we cannot continue; do nothing
			System.err.println("File not found. This was loaded: " + e);
		}

		return validAdventures;
	}

	// file reading
	public static String readFile(String myFile) {
		// pass command line argument of the name of the file to this
		// consider an option
		String out = "";

		try {
			// open the file for reading
			BufferedReader file = new BufferedReader(new FileReader(myFile));

			// read the contents and output it to stdout
			String line = null;

			while ((line = file.readLine()) != null) {
				out += line + "\n";
			}

			file.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return out;
	}

	private static void printArgs(String[] myArgs) {
		// prints out args for testing. uncomment in main to see
		System.out.println("My args are: \n" + Arrays.toString(myArgs) + " \n");
	}

	private static boolean checkArgsLen(String[] myArgs) {

		if (!(myArgs.length >= 0)) {
			System.out.println("you need to pass in the Adventures.txt");
			return false;
		} else
			return true;
	}
	
	public static String addPathToFileName(String fileName) {
		
		// store files in 1 above src and this should work...on windows..
		StringBuffer buffer = new StringBuffer();
		
		//buffer.append("..");
		//buffer.append("src//adventures//");
		buffer.append(fileName);
		
		return buffer.toString();
	}
	
	public static String getChoicesWithFormating(ArrayList<String> choices) {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\nOur Hero's current choices are: \n\n");
		
		for (String choice : choices){
			
			buffer.append("\t\t" + choice + "\n\n");
		}
		
		return buffer.toString();
	}

	public static void main(String[] args) {

		if (checkArgsLen(args)) {
			// call adventure
			// we need these messages to go to the client... so their response would go into the constructor
			// not this method inside there now
			
			// from client
			// pass to adventure constructor fro client
			Adventure adventure = new Adventure(getAdventureChoiceFromUserInput(getValidAdventures()));
			
			String currentChoice = adventure.getFirstSceneKey();
			String currentScene = adventure.getCurrentScene(currentChoice);
			ArrayList<String> currentSceneKeys = adventure.getSceneKeys(currentChoice, currentScene);
			
			
			
			// the client sending/receiving stuff would have to go here
			while(!currentChoice.contains("Z")) {
				
				System.out.println(getChoicesWithFormating(currentSceneKeys));
				
				currentChoice = getNextSceneChoiceFromUserInput(currentSceneKeys);
				currentScene = adventure.getCurrentScene(currentChoice);
				currentSceneKeys = adventure.getSceneKeys(currentChoice, currentScene);	
				
				System.out.println(currentScene);
			}

		} else if (!checkArgsLen(args)) {
			printArgs(args);
		}

	}

}
