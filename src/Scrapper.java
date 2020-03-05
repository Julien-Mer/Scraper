import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scrapper {

	private static Pattern regexEmail = Pattern.compile(".*?([\\w\\.-]+?@[\\w-]+?\\.\\w{2,}).*", Pattern.DOTALL);
	
	private ArrayList<String> contactUrls;
	private Foreman foreman;	
	private String[] keywords;
	private String search;
	private HttpClient client;
	private String googleSearchUrl;
	private ArrayList<String> mailsBlacklist;
	private ArrayList<String> urlsBlacklist;
	
	public Scrapper(String search, String[] keywords)
	{
		this.mailsBlacklist = File.readFileLines("mails.blacklist");
		this.urlsBlacklist = File.readFileLines("urls.blacklist");
		this.foreman = new Foreman(40, this);
		
		contactUrls = new ArrayList<String>();
		for(String name : File.readFileLines("urls.list")) 
			for(String extension : File.readFileLines("extensions.list")) 
				contactUrls.add(name + extension);
		this.search = search;
		this.keywords = keywords;
		this.googleSearchUrl = "https://www.google.com/search?client=firefox-b-d&q=" + this.search.replace(" ", "+");
		this.client = HttpClient.newBuilder().build();
	}
	
	public String getWebContent(String url)
	{
		String res = "";
		try {
			 HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0").timeout(Duration.ofMillis(1500)).build();
			 HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			 if(response.statusCode() == HttpURLConnection.HTTP_OK)
				 res = response.body();
		} catch (Exception e) {
		}
		return res;
	}
	
	public ArrayList<String[]> getPagesMails(int start) {
		return getUrls(getWebContent(this.googleSearchUrl + "&start=" + start));
	}

	public ArrayList<String[]> getUrls(String googleContent)
	{
		ArrayList<String[]> urls = new ArrayList<String[]>();
		String[] res = googleContent.split("class=\"r\"><a href=\"");
		String[] url;
		for(int i = 1; i < res.length; i++)
		{
			url = new String[2];
			url[0] = res[i].substring(0, res[i].indexOf("\" onmousedown=\""));
			if(url[0].contains("/url?q=")) {
				url[0] = url[0].substring(7, url[0].indexOf("&amp;"));
			}
			try {
				URL aURL = new URL(url[0]);
				url[0] = aURL.getProtocol() + "://" + aURL.getHost();
				url[1] = aURL.getPath();
			} catch (MalformedURLException e) { }
			if(!this.urlsBlacklist.contains(url[0]))
				urls.add(url);
		}
			
		return urls;
	}
	
	public ArrayList<String[]> checkUrls(ArrayList<String[]> urls) {
		return this.foreman.checkUrls(urls, this.keywords);
	}
	
	public void searchMails(String url, ArrayList<String> mails) {
		System.out.println("Recherche sur: " + url);
		for(String urlContact : contactUrls) {
			String content = this.getWebContent(url + urlContact);
			if(!content.equals("")) {
				content = content.replace(" [at] ", "@").replace("[at]", "@").replace("(@)", "@").replace(" [@] ", "@").replace("[@]", "@").replace("[.]", ".");
				Matcher m = regexEmail.matcher(content);
				while(m.matches()) {
					String mail = m.group(1);
					if(!mails.contains(mail) && !mailsBlacklist.contains(mail) && !mail.toLowerCase().contains("clientsidemetricsauijavascript") && !mail.toLowerCase().contains("x.jpg") && !mail.toLowerCase().contains("x.png") && !mail.toLowerCase().contains("algoliasearch") && !mail.toLowerCase().contains("sentry.")) {
						System.out.println("-> " + mail);
						mails.add(mail);
					}
					content = content.substring(content.indexOf(mail) + mail.length());
					m = regexEmail.matcher(content);
				}
			}
		}
	}
	
	public HashMap<String, ArrayList<String>> getMails(ArrayList<String[]> urls) {
		return this.foreman.getMails(urls);
	}
	
}
