import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Nom du fichier d'export:");
		String name = scanner.nextLine();
		
		System.out.println("Recherche google:");
		String search = scanner.nextLine();
		
		System.out.println("Mots clés (séparés par une virgule):");
		String[] keywords = scanner.nextLine().toLowerCase().split(",");
		
		Scrapper scrapper = new Scrapper(search, keywords);
		
		System.out.println("Nombre de pages à parcourir:");
		int page = scanner.nextInt();
		
		ArrayList<String[]> urls = new ArrayList<String[]>();
		
		for(int i = 0; i < page; i++) 
		{
			System.out.println("=======================================");
			System.out.println("Recherche page " + (i+1));
			System.out.println("=======================================");
			ArrayList<String[]> urlsLocal = scrapper.getPagesMails(i + urls.size());
			System.out.println(urlsLocal.size() + " sites trouvés.");
			urls.addAll(urlsLocal);
		}
		Thread.sleep(1000);
		System.out.println("=======================================");
		System.out.println(urls.size() + " sites à vérifier.");
		Thread.sleep(1000);
		System.out.println("=======================================");
		if(!(keywords.length == 1 && keywords[0].equals("")))
			urls = scrapper.checkUrls(urls);
		System.out.println(urls.size() + " sites sont vérifiés.");
		Thread.sleep(1000);
		System.out.println("=======================================");
		ArrayList<String[]> originalUrls = (ArrayList<String[]>) urls.clone();
		HashMap<String, ArrayList<String>> mails = scrapper.getMails(urls);
		String data = "";
		for(Entry<String, ArrayList<String>> entry : mails.entrySet()) {
			data += entry.getKey() + " ";
			for(int i = 0; i < entry.getValue().size(); i++) {
				data += entry.getValue().get(i);
				if(i < entry.getValue().size() -1)
					data += ",";
			}
			data += '\n';
		}
		System.out.println(mails.size() + " sites trouvés.");
		if(mails.size() > 0) {
			File.writeFile(data, name);
			System.out.println("=======================================");
			System.out.println("Mails exportés dans " + name);
			System.out.println("=======================================");
		}
		data = "";
		for(String[] url : originalUrls) {
			if(!mails.containsKey(url[0] + url[1]))
				data += url[0] + url[1] + '\n';
		}
		File.writeFile(data, "fails-" + name);
	}
	
}
