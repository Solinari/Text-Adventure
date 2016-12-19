import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

public class Adventure {

	private String choice;
	HashMap<String, HashMap<String, ArrayList<String>>> theAdventure;

	public Adventure(String choice) {
		this.choice = choice;
		this.theAdventure = buildAdventure();
	}

	private String getChoice() {
		return choice;
	}

	public HashMap<String, HashMap<String, ArrayList<String>>> getTheAdventure() {
		return theAdventure;
	}

	private HashMap<String, HashMap<String, ArrayList<String>>> buildAdventure() {

		// line key | text | next line keys
		// S is start
		// E is end
		// final structure: {scene key: {1 scene: it's scene keys} }
		HashMap<String, HashMap<String, ArrayList<String>>> builtAdventure = new HashMap<String, HashMap<String, ArrayList<String>>>();

		// String adventureChoice = AdventureMain.readFile(getChoice());
		System.out.println("Adventure choice is: " + getChoice());

		String adventureFilePath = AdventureMain.readFile(AdventureMain.addPathToFileName(getChoice()));

		System.out.println("\n\nAdventure file path is: " + getChoice());
		try {
			// buffer for file in
			BufferedReader input = new BufferedReader(new StringReader(adventureFilePath));
			String line = null;

			// read each line
			while ((line = input.readLine()) != null) {

				// split along the delimiters
				String[] lineParse;
				lineParse = line.split(Pattern.quote("|"));

				// container for next scene keys
				ArrayList<String> nextChoice = new ArrayList<String>();

				// container for scene and it's scene keys
				HashMap<String, ArrayList<String>> textAndChoices = new HashMap<String, ArrayList<String>>();

				// scene key and content
				String choiceKey = "";
				String choiceContent = "";

				if (lineParse.length == 3) {

					choiceKey = lineParse[0];
					choiceContent = lineParse[1];
					String[] choiceSplit;
					choiceSplit = lineParse[2].split(Pattern.quote(";"));

					for (String choice : choiceSplit) {

						if (!(choice.length() == 0)) {
							nextChoice.add(choice);
						}
					}

					textAndChoices.put(choiceContent, nextChoice);

					builtAdventure.put(choiceKey, textAndChoices);

				}

			}
		} catch (IOException e) {
			// if file not found we cannot continue; do nothing
			System.err.println("File not found. This was loaded: " + e);
		}

		return builtAdventure;
	}
	
	// begin adventure
	public String getFirstSceneKey() {
		
		String firstScene = "";
		for (String scene : getTheAdventure().keySet()) {
//			System.out.println("Scene is: " +scene);
			
			if (scene.equals("S")) {
				firstScene = scene;
			}
			
		}
		return firstScene;
	}
	
	public String getNextChoice(String currentChoice) {
		
		String currentScene = "";
		
		for (String scene : getTheAdventure().get(currentChoice).keySet()) {
			currentScene = scene;
		}

		// Iterate over value of scene key, display choices to user
		// display the string
		ArrayList<String> currentSceneKeys = getSceneKeys(currentChoice, currentScene);
		String nextChoices = AdventureMain.getChoicesWithFormating(currentSceneKeys);
		
		return nextChoices;
	}
	
	// process adventure scene
	
	public String getCurrentScene(String currentChoice) {
		
		String outChoice = "";
		
		// there is only one
		for (String scene : getTheAdventure().get(currentChoice).keySet()) {
			outChoice = scene;
		}
		
		return outChoice;
	}
	public ArrayList<String> getSceneKeys(String currentChoice, String currentScene) {
		
		return getTheAdventure().get(currentChoice).get(currentScene);
	}
	

	public void playAdventure() {
		// this is for testing to show the flow of the adventure. do not call this in server.
		HashMap<String, HashMap<String, ArrayList<String>>> myAdventure = getTheAdventure();

		Set<String> choices = myAdventure.keySet();

		String currentChoice = "";
		String currentScene = "";

		// since we're building the files this should work
		if (choices.contains("S")) {
			currentChoice = "S";
		}

		while (!currentChoice.equals("Z")) {

			// there is only one scene per scene key but this pulls it out of
			// the set
			for (String scene : myAdventure.get(currentChoice).keySet()) {
				currentScene = scene;
			}

			// Iterate over value of scene key, display choices to user
			// display the string
			ArrayList<String> currentSceneKeys = myAdventure.get(currentChoice).get(currentScene);
			// placeholder
			String nextChoices = AdventureMain.getChoicesWithFormating(currentSceneKeys);
			System.out.println(nextChoices);
			
			// get player choice
			String playerNextChoice = "";
			while (!currentSceneKeys.contains(playerNextChoice)) {
				playerNextChoice = AdventureMain
						.getNextSceneChoiceFromUserInput(currentSceneKeys);
			}
			
			currentChoice = playerNextChoice;
		}
		System.out.println("Thank you for playing, Champion. Go forth and be awesome!");
	}

}
