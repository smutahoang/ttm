package hoang.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLExtractor {
	public String urlRE;

	public URLExtractor(String re) {
		urlRE = re;
	}

	public List<String> getURLs(String tweet) {
		// System.out.println("urlRE = \t" + urlRE);
		Pattern pattern = Pattern.compile(urlRE);
		Matcher matcher = pattern.matcher(tweet);
		List<String> result = new ArrayList<String>();
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result;
	}

	public static void main(String[] args) {
		String urlRE = "(\\.net)|(\\.org)|(\\.com)|(www\\.)|"
				+ "((https|ftp|gopher|telnet|file|Unsure|http)"
				+ ":((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		URLExtractor urlExtractor = new URLExtractor(urlRE);

		List<String> urlList = urlExtractor
				.getURLs("Our new Media Blog showcases how partners & publishers are using Twitter in TV, music, sports, govt, news biz: https://blog.twitter.com/media");
		System.out.println("urlList.size() =\t" + urlList.size());
		for (int i = 0; i < urlList.size(); i++) {
			System.out.println("Found ulr:\t" + urlList.get(i));
		}
	}
}
