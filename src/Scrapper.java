import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scrapper {

	private static Pattern regexUrl = Pattern.compile("/class=\"r\"><a href=\"(.*)\"/gU");
	private static Pattern regexUrlGoogle = Pattern.compile("/\\/url\\?q=(.*)&amp;/gU");
	private static Pattern regexEmail = Pattern.compile("/([\\w.-]+@[\\w-]+\\.+\\w{2,})/g");
	private static String[] nameContact = {"contact", "contactus", "contact-us"};
	private static String[] extensionContact = {".php", "/", ".html", ".aspx"};
	
	private String[] keywords;
	private String search;
	private HttpClient client;
	private String googleSearchUrl;
	
	public Scrapper(String search, String[] keywords)
	{
		this.search = search;
		this.keywords = keywords;
		this.googleSearchUrl = "https://www.google.com/search?client=firefox-b-d&q=" + this.search.replace(" ", "+");
		this.client = HttpClient.newBuilder().build();
	}
	
	public String getWebContent(String url)
	{
		String res = null;
		try {
			 HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0").build();
			 HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			 res =response.body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public String[] getPagesMails(int start) {
		return getUrls(getWebContent(this.googleSearchUrl + "&start=" + start));
	}

	public String[] getUrls(String googleContent)
	{
		Matcher m = regexUrl.matcher(googleContent);
		System.out.println(m.groupCount());
		System.out.println(m.group());
		return null;
	}

	/*
	private function getRegexUrl($array)
	{
		$result = array();
		for ($i = 0; $i < sizeof($array[1]); $i++) {
			$url = $array[1][$i];
			preg_match($this->regexUrlGoogle, $url, $out); // Si l'url est une url de google
			if ($out)
				$url = $out[1]; // On prend l'url directement
			if (!in_array($url, $result))
				$result[] = $url;
			unset($url);
		}
		unset($array);
		return $result;
	}


	## Partie Mail / Verif site ##
	 function getMails($urls)
	{
		$list = array();
		foreach ($urls as $url) {
			$content = $this->getWebContent($url);
			$list[$url] = '?';
			if($content != null) {
				if ($this->checkWebsite($content)) {
					$mails = $this->searchEmail($url);
					if (sizeof($mails) > 0) {
						$list[$url] = implode(',', $mails);
					} else {
						$list[$url] = '-';
					}
					unset($mails);
				} else
					$list[$url] = 'X';
			}
			unset($content);
		}
		return $list;
	}

	private function checkWebsite($content)
	{
		$keywordsArray = explode(' ', $this->keywords);
		$verified = null;
		$i = 0;
		while ($i < sizeof($keywordsArray) && !$verified) {
			$verified = strpos($content, $keywordsArray[$i]);
			$i++;
		}
		return $verified;
	}

	function searchEmail($url)
	{
		$mails = array();
		$parse = parse_url($url);
		$domain = $parse['scheme'] . '://' . $parse['host'];
		$i = 0;
		while ($i < sizeof($this->nameContact)) {
			$j = 0;
			while ($j < sizeof($this->extensionContact)) {
				preg_match_all($this->regexEmail, $this->getWebContent($domain . '/' . $this->nameContact[$i] . $this->extensionContact[$j]), $out);
				$mails = $this->getClearedMailsArray($out, $mails);
				unset($out);
				$j++;
			}
			unset($j);
			$i++;
		}
		unset($i, $domain);
		return $mails;
	}

	function getClearedMailsArray($array, $mails) {
		if(sizeof($array) > 1) {
			for ($i = 0; $i < sizeof($array[1]); $i++) {
				$url = $array[1][$i];
				if(!empty($url) && !in_array($url, $mails))
					$mails[] = $url;
				unset($url);
			}
		}
		return $mails;
	}
	*/
}
