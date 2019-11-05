import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Recherche google:");
		String search = scanner.nextLine();
		System.out.println("Mots clés (séparés par une virgule)");
		String[] keywords = scanner.nextLine().split(",");
		Scrapper scrapper = new Scrapper(search, keywords);
		scrapper.getPagesMails(0);
	}
	
}
