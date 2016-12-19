import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;


public class InputOutputLogger {

	
	static void logToFile(String myFile, String myData) {
	// this is also a command line file doing this

	try {
		// open the file for reading
		// true makes it append to existing log
		PrintWriter file = new PrintWriter(new BufferedWriter(new FileWriter(myFile, true)));

		// read from myData and write to file
		BufferedReader read = new BufferedReader(new StringReader(myData));
		//System.out.println("\nBeginning file logging...\n");
		String line = null;
		while ((line = read.readLine()) != null){
			//System.out.println("\nLogging:\n" + line);
			file.println(line);
		}
		file.close();
		read.close();
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}

}

	
}
