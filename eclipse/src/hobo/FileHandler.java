package hobo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
	
	private FileWriter fileWriter;
	private File file;
	
	public FileHandler(String url) {
		file = new File(url);
		try {
			fileWriter = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeFile(String input) {
		try {
			fileWriter.write(input+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
