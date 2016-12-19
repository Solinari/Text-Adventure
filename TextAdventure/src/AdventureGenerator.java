import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class AdventureGenerator {

	public AdventureGenerator() {
	}

	private static ArrayList<String> getPreclusion() {
		ArrayList<String> begins = new ArrayList<>(
				Arrays.asList("After a ", "Coming from ", "Following a ", "Investigating a ", "Fleeing a "));

		return begins;
	}

	private static ArrayList<String> getIntroAdjectives() {
		ArrayList<String> adjects = new ArrayList<>(
				Arrays.asList("narrow escape from ", "sweet ", "harrowing ", "bloody ", "delightful "));

		return adjects;
	}

	private static ArrayList<String> getIntroNouns() {
		ArrayList<String> groupNouns = new ArrayList<>(Arrays.asList("battle with a mighty dragon... ",
				"all night party... ", "quest to remove curse placed on them by an evil wizard... ",
				"minding searing teleportation through the Eye of Terror... ", "quest from across the land... "));

		return groupNouns;
	}

	private static ArrayList<String> getIntro() {
		ArrayList<String> subjects = new ArrayList<String>();

		int count = 10;

		while (count > 0) {
			subjects.add(randomPick(getPreclusion()) + randomPick(getIntroAdjectives()) + randomPick(getIntroNouns()));
			count--;
		}
		return subjects;
	}

	private static ArrayList<String> getBeginnings() {
		ArrayList<String> begins = new ArrayList<>(Arrays.asList("The ", "Our ", "That "));

		return begins;
	}

	private static ArrayList<String> getAdjectives() {
		ArrayList<String> adjects = new ArrayList<>(Arrays.asList("tenacious ", "intrepid ", "brave ", "courageous ",
				"foolish ", "haughty ", "suicidial ", "dim-witted "));

		return adjects;
	}

	private static ArrayList<String> getGroupNouns() {
		ArrayList<String> groupNouns = new ArrayList<>(Arrays.asList("group ", "party ", "entourage ", "troupe ",
				"band of companions ", "friends ", "pack of mooks ", "squad of sell-swords "));

		return groupNouns;
	}

	private static ArrayList<String> getGroup() {
		ArrayList<String> subjects = new ArrayList<String>();

		int count = 10;

		while (count > 0) {
			subjects.add(randomPick(getBeginnings()) + randomPick(getAdjectives()) + randomPick(getGroupNouns()));
			count--;
		}
		return subjects;
	}

	private static ArrayList<String> getSceneAction() {
		ArrayList<String> predicates = new ArrayList<>(
				Arrays.asList("get drunk with ", "rush forwards and do battle with ", "flee in terror from ",
						"stare, mouths agape at ", "totally obliterate "));
		return predicates;
	}

	private static ArrayList<String> getBeginMobs() {
		ArrayList<String> mobBegins = new ArrayList<>(
				Arrays.asList("the ", "another ", "a ", "a pack of ", "a floating, ", ""));
		return mobBegins;
	}

	private static ArrayList<String> getAdjectiveMobs() {
		ArrayList<String> mobAdject = new ArrayList<>(
				Arrays.asList("tiny ", "adorable ", "terrifying ", "adventuring ", "huge ", "completely invisible "));
		return mobAdject;
	}

	private static ArrayList<String> getMobs() {
		ArrayList<String> mobs = new ArrayList<>(Arrays.asList("Minotaur.", "Box of Puppies.", "Party of Heroes",
				"Beholder.", "Displacer Beast.", "Mind Flayer.", "Sphere of Annihilation."));
		return mobs;
	}

	private static ArrayList<String> getEncounters() {

		ArrayList<String> objects = new ArrayList<String>();

		int count = 10;

		while (count > 0) {
			objects.add(randomPick(getSceneAction()) + randomPick(getBeginMobs()) + randomPick(getAdjectiveMobs())
					+ randomPick(getMobs()));
			count--;
		}

		return objects;
	}

	private static ArrayList<String> getEnds() {
		// do
		ArrayList<String> complements = new ArrayList<>(Arrays.asList("You all die of dysentery. End of Adventure.",
				"The Party realizes they are characters in an adventure, and the fourth wall is broken.",
				"The Party realizes they had really good time and everyone goes home happy!"));
		return complements;
	}

	private static ArrayList<String> getActions() {
		ArrayList<String> actions = new ArrayList<>(Arrays.asList("Fight", "Run", "Hide", "Dance"));
		return actions;
	}

	static String randomPick(ArrayList<String> choices) {
		Random randomGenerator = new Random();
		int choice = randomGenerator.nextInt(choices.size());
		return choices.get(choice);
	}

	static int randomNumber() {
		Random randomNumber = new Random();

		int randomNum = randomNumber.nextInt((2 - 1) + 1) + 1;

		return randomNum;
	}

	static Stack<String> sceneKeys() {
		ArrayList<String> keys = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E"));

		Stack<String> out = new Stack<String>();

		while (!(out.size() == keys.size())) {
			String test = randomPick(keys);

			if (!out.contains(test)) {
				out.push(test);
			}

		}

		return out;
	}

	static ArrayList<String> choiceKeys() {
		ArrayList<String> keys = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "Z"));

		return keys;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ArrayList<String> intro = getIntro();
		ArrayList<String> subject = getGroup();
		ArrayList<String> object = getEncounters();
		ArrayList<String> actions = getActions();

		ArrayList<String> getKey = choiceKeys();

		Stack<String> keys = sceneKeys();
		Set<String> done = new HashSet<String>();

		// while(true) {
		// System.out.println(randomNumber());
		// }

		String S = "S";
		String bar = "|";
		String hy = "-";
		String pick = sceneKeys().pop() + hy + randomPick(actions);
		done.add(pick);
		String semi = ";";

		String adventure = S + bar + randomPick(intro) + bar + pick + semi + "\n";
		int count = 0;
		while (!(done.size() == getKey.size() + 1)) {

			pick = sceneKeys().pop();
			

			if (!done.contains(pick)) {
				adventure += pick + bar + randomPick(subject) + randomPick(object) + bar;

				count = randomNumber();
				Set<String> picked = new HashSet<String>();
				String isPicked = randomPick(actions);
				picked.add(isPicked);
				// now do new picks
				while (count > 0) {
					
					String nextKey = randomPick(getKey);
					
					while(done.contains(nextKey)) {
						nextKey = randomPick(getKey);
					}
					adventure += nextKey + hy + isPicked + semi;
					count--;
					
					while(picked.contains(isPicked)) {
						isPicked = randomPick(actions);
					}
					picked.add(isPicked);
				}

				adventure += "\n";
			}
			
			done.add(pick);

		}
		
		adventure += "Z" + bar + randomPick(getEnds()) + bar + hy + semi;
		
		System.out.println(adventure);
//		AdventureMain.writeFile("RandomAdventure.txt", adventure);

	}
}
