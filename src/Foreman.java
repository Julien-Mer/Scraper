import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class Foreman {

	private ArrayList<Thread> workers;

	private HashMap<String, ArrayList<String>> mailsRes;
	private ArrayList<String[]> checkedUrls;
	
	private int maxWorkers;
	private Scrapper scrapper;
	
	public Foreman(int maxWorkers, Scrapper scrapper) {
		this.workers = new ArrayList<Thread>();
		this.maxWorkers = maxWorkers;
		this.scrapper = scrapper;
	}
	
	public ArrayList<String[]> checkUrls(ArrayList<String[]> urls, String[] keywords) {
		this.checkedUrls = new ArrayList<String[]>();
		while(isWorking() || urls.size() > 0) {
			while(this.workers.size() < this.maxWorkers && urls.size() > 0) {
				String[] url = urls.remove(0);
				Thread worker = new Thread(new Runnable() {
					public void run() {
						boolean contains = false;
						String content = scrapper.getWebContent(url[0] + url[1]).toLowerCase();
						for(String keyword : keywords)
							if(content.contains(keyword) || keyword.trim().equals(""))
								contains = true;
						if(contains)
							checkedUrls.add(url);
					}
				});
				worker.start();
				this.workers.add(worker);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}
		return checkedUrls;
	}	
	
	public HashMap<String, ArrayList<String>> getMails(ArrayList<String[]> urls) {
		this.mailsRes = new HashMap<String, ArrayList<String>>();
		while(isWorking() || urls.size() > 0) {
			while(this.workers.size() < this.maxWorkers && urls.size() > 0) {
				String[] url = urls.remove(0);
				Thread worker = new Thread(new Runnable() {
					public void run() {
						ArrayList<String> mails = new ArrayList<String>();
						scrapper.searchMails(url[0] + "/", mails);
						if(!(url[0] + url[1]).equals(url[0] + "/")) {
							scrapper.searchMails(url[0] + url[1], mails);
						}
						if(mails.size() > 0) {
							mailsRes.put(url[0] + url[1], mails);
						}
					}
				});
				worker.start();
				this.workers.add(worker);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
		}
		return mailsRes;
	}
	
	public boolean isWorking() {
		for (Iterator<Thread> it = this.workers.iterator(); it.hasNext(); ) {
		    Thread worker = it.next();
			if(worker != null && !worker.isAlive())
				it.remove();
		}
		return this.workers.size() > 0;
	}
	
}
