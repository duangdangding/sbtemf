package com.lshs.sbtemf.utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.lshs.sbtemf.entry.BiAn;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 爬取网页工具
 * @author: LuShao
 * @create: 2018-08-26 8:30
 **/
public class JsoupUtil {

	/**
	 * 得到url响应状态
	 *
	 * @param url
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static int getStatus(String url) throws URISyntaxException, IOException {
		HttpGet get = new HttpGet(new URI(url));
		HttpResponse response = new DefaultHttpClient().execute(get);
		int statusCode = response.getStatusLine().getStatusCode();
		return statusCode;
	}

	/**
	 * 得到第一个页面的list内容
	 *
	 * @param url1
	 * @param biAn
	 * @param start 从这个页面的第几个开始
	 * @param end   到第几个结束  1-18
	 * @return
	 * @throws IOException
	 */
	public static List<BiAn> getUrl1List(String url1, BiAn biAn, Integer start, Integer end) throws IOException {
		List<BiAn> url1s = new ArrayList<>();
		Document document = Jsoup.connect(url1).timeout(300000).get();
		Element list = document.getElementsByClass("list").get(0);
		Elements lis = list.getElementsByTag("li");

		int num = 0;
		int size;
		if (start != null) num = start - 1;
		if (end != null) size = end;
		else size = lis.size();

		for (int i = num; i < size; i++) {
			Element li = lis.get(i);
			Element a = li.getElementsByTag("a").get(0);
			String href1 = a.attr("href");
			if (!href1.equals("http://pic.netbian.com/")) {
				BiAn biAn1 = new BiAn();
				biAn1.setPage(biAn.getPage());
				biAn1.setScreen(biAn.getScreen());
				biAn1.setFromurl(biAn.getFromurl());
				String href = "http://www.netbian.com" + href1;
				biAn1.setUrl1(href);
				String title = a.attr("title");
				biAn1.setTitle(title);
				Element img = a.getElementsByTag("img").get(0);
				String src = img.attr("src");
				biAn1.setImg1(src);
				Element p = li.getElementsByTag("p").get(0);
//			更新时间：2017-01-07
				String replace = p.text().replace("更新时间：", "");
				biAn1.setUpdatetime(replace);
				url1s.add(biAn1);
			}
		}
		return url1s;
	}

	/**
	 * 根据传入的url得到第二个页面内容
	 *
	 * @param url1
	 * @param biAn
	 * @return
	 * @throws IOException
	 */
	public static BiAn getUrl2(String url1, BiAn biAn) throws IOException {
		Document document = Jsoup.connect(url1).timeout(300000).get();
		Element action = document.getElementsByClass("action").get(0);
//		风景壁纸
		String s = action.getElementsByTag("a").get(1).text().replaceAll("壁纸", "");
		biAn.setCategory(s);
		Element pic = document.getElementsByClass("pic").get(0);
		Element a = pic.getElementsByTag("a").get(0);
		String href = a.attr("href");
		href = "http://www.netbian.com" + href;
		biAn.setUrl2(href);
		Element img = a.getElementsByTag("img").get(0);
		String src = img.attr("src");
		biAn.setImg2(src);
		return biAn;
	}

	/**
	 * 得到最后一页的内容
	 *
	 * @param url2
	 * @param biAn
	 * @return
	 * @throws IOException
	 */
	public static BiAn getUrl3(String url2, BiAn biAn) throws IOException {
		Document document = Jsoup.connect(url2).timeout(300000).get();
		String attr = document.getElementById("endimg").getElementsByTag("a").get(0).attr("href");
		biAn.setUrl3(attr);
		return biAn;
	}

	//Document document = Jsoup.connect(url)
	//		.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0")
	//		.header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
	//		.timeout(50000000).maxBodySize(0).get();
	public static void getDemo1() throws IOException, InterruptedException {
		String url = "https://www.walmart.com/search/?clickid=zn1VgOTgixyOTHhwUx0Mo34BUkiy5xXk%3ASGl0M0&facet=retailer%3APharmapacks&grid=true&irgwc=1&sharedid=&sourceid=imp_zn1VgOTgixyOTHhwUx0Mo34BUkiy5xXk%3ASGl0M0&veh=aff&wmlspartner=imp_1927457";
		Document document = getDocument(url);
		Element mainSearchContent = document.getElementById("mainSearchContent");
		Element ul = mainSearchContent.getElementsByTag("ul").get(0);
		Elements lis = ul.getElementsByTag("li");
		System.out.println(lis.size());
		for (Element li : lis) {
			Element aClass = li.getElementsByClass("search-result-productimage").get(0);
			String href = aClass.attr("href");
			System.out.println(href);
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		//setProxy();
		getDemo1();
	}


	public static void setProxy() throws IOException {
		// 使用java.net.Proxy类设置代理IP 114.243.67.243:8118
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("123.22.43.3", 8080));
		HttpURLConnection connection = (HttpURLConnection) new URL("http://www.baidu.com/").openConnection(proxy);
		connection.setConnectTimeout(6000); // 6s  
		connection.setReadTimeout(6000);
		connection.setUseCaches(false);

		if (connection.getResponseCode() == 200) {
			System.out.println("代理ip连接成功");
		}
	}

	public static Document getDocument(String url) throws IOException, InterruptedException {
		WebClient wc = new WebClient(BrowserVersion.CHROME);
		//是否使用不安全的SSL
		wc.getOptions().setUseInsecureSSL(true);
		//启用JS解释器，默认为true
		wc.getOptions().setJavaScriptEnabled(true);
		//禁用CSS
		wc.getOptions().setCssEnabled(false);
		//js运行错误时，是否抛出异常
		wc.getOptions().setThrowExceptionOnScriptError(false);
		//状态码错误时，是否抛出异常
		wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
		//是否允许使用ActiveX
		wc.getOptions().setActiveXNative(false);
		//等待js时间
		wc.waitForBackgroundJavaScript(600 * 1000);
		//设置Ajax异步处理控制器即启用Ajax支持
		wc.setAjaxController(new NicelyResynchronizingAjaxController());
		//设置超时时间
		wc.getOptions().setTimeout(1000000);
		//不跟踪抓取
		wc.getOptions().setDoNotTrackEnabled(false);
		WebRequest request = new WebRequest(new URL(url));
		//request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
		//request.setAdditionalHeader("Cookie", "PLAY_LANG=cn; _plh=b9289d0a863a8fc9c79fb938f15372f7731d13fb; PLATFORM_SESSION=39034d07000717c664134556ad39869771aabc04-_ldi=520275&_lsh=8cf91cdbcbbb255adff5cba6061f561b642f5157&csrfToken=209f20c8473bc0518413c226f898ff79cd69c3ff-1539926671235-b853a6a63c77dd8fcc364a58&_lpt=%2Fcn%2Fvehicle_sales%2Fsearch&_lsi=1646321; _ga=GA1.2.2146952143.1539926675; _gid=GA1.2.1032787565.1539926675; _plh_notime=8cf91cdbcbbb255adff5cba6061f561b642f5157");
		try {
			//模拟浏览器打开一个目标网址
			HtmlPage htmlPage = wc.getPage(request);
			//为了获取js执行的数据 线程开始沉睡等待
			Thread.sleep(1000);//这个线程的等待 因为js加载需要时间的
			//以xml形式获取响应文本
			String xml = htmlPage.asXml();
			//并转为Document对象return
			return Jsoup.parse(xml);
			//System.out.println(xml.contains("结果.xls"));//false
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
