import java.io.*;
import java.util.ArrayList;

public class File {
	
	public static void writeFile(String data, String name) {
		try {
			FileWriter stream = new FileWriter(name);
			BufferedWriter writer = new BufferedWriter(stream);
			writer.write(data);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> readFileLines(String name) {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			FileReader stream = new FileReader(name);
			BufferedReader reader = new BufferedReader(stream);
			String line = reader.readLine();
			while(line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

}
