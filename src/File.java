import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class File {
	
	public static void writeFile(String data) {
		try {
			FileOutputStream stream = new FileOutputStream("out.test");
			BufferedOutputStream writer = new BufferedOutputStream(stream);
			writer.write(data.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
