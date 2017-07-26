package com.jhinwins;

import jxl.read.biff.BiffException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

/**
 * Created by Jhinwins on 2017/7/25  10:03.
 * Desc:
 */
public class DomainSpider {
    public static final String BASEON_BAIDU = "baidu";
    public static final String BASEON_CHINAZ = "chinaz";

    /**
     * 快速搜索
     */
    public static final String SPEED_QUICK = "quick";
    /**
     * 信息准确
     */
    public static final String SPEED_EXACT = "exact";

    public static void main(String[] params) throws IOException, BiffException, ClassNotFoundException, SQLException {
        String url = new DomainSpider().getUrlByName("人人网",SPEED_EXACT);
        System.out.println("人人网 url ：" + url);
    }

    /**
     * 根据网站名称查询网站域名 默认根据准确的查询
     *
     * @param name
     * @return
     */
    public String getUrlByName(String name) {
        String url;
        //站长之家
        url = findByNameBaseChinaz(name);
        if (url != null && url.length() > 0) {
            return url;
        }
        //百度
        url = findByNameBaseBaidu(name);

        if (url != null && url.trim().length() > 0) {
            url = url.replace("www.", "").replaceAll("https+", "");
            url = url.substring(0, url.lastIndexOf("/") == -1 ? url.length() : url.lastIndexOf("/"));
        }
        return url.trim();
    }

    /**
     * 根据网站名称查询网站域名
     *
     * @param name
     * @param speed 要求查询的速度,快的话不准,准的话不快
     * @return
     */
    public String getUrlByName(String name, String speed) {
        String url = null;
        if (SPEED_EXACT.equals(speed)) {
            //站长之家
            String url_chinaz = findByNameBaseChinaz(name);
            if (url_chinaz != null && url_chinaz.trim().length() > 0) {
                url = url_chinaz;
            } else {
                //百度
                url = findByNameBaseBaidu(name);
            }
        }
        if (SPEED_QUICK.equals(speed)) {
            //站长之家
            //百度
            String url_baidu = findByNameBaseBaidu(name);
            if (url_baidu != null && url_baidu.trim().length() > 0) {
                url = url_baidu;
            } else {
                url = findByNameBaseChinaz(name);
            }
        }

        if (url != null && url.trim().length() > 0) {
            url = url.replace("www.", "").replaceAll("https+", "");
            url = url.substring(0, url.lastIndexOf("/") == -1 ? url.length() : url.lastIndexOf("/"));
        }
        return url.trim();
    }


    public String findByNameBaseChinaz(String name) {
        return findByName(name, BASEON_CHINAZ);
    }

    public String findByNameBaseBaidu(String name) {
        return findByName(name, BASEON_BAIDU);
    }

    int recursionCount = 0;

    private String findByName(String name, String baseOn) {
        ++recursionCount;
        //如果递归数量大于2则结束递归
        if (recursionCount > 2) {
            return null;
        }

        String url = null;

        String html = "";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {

            String baseUrl = "";
            if (BASEON_BAIDU.equals(baseOn)) {
                baseUrl = "http://www.baidu.com/s?wd=" + name + "主页";
            } else if (BASEON_CHINAZ.equals(baseOn)) {
                baseUrl = "http://search.top.chinaz.com/Search.aspx?url=" + name;
            }

            HttpGet httpGet = new HttpGet(baseUrl);

            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            InputStream contentIS = response.getEntity().getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(contentIS));
            String buff;
            while ((buff = bufferedReader.readLine()) != null) {
                html += buff;
            }
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (html != null && html.length() > 0) {

            Document document = Jsoup.parse(html);
            if (BASEON_BAIDU.equals(baseOn)) {
                url = parseBaidu(document);
            } else if (BASEON_CHINAZ.equals(baseOn)) {
                url = parseChinaz(document);
            }

        } else {
            url = findByName(name, baseOn);
        }
        recursionCount = 0;
        return url;
    }

    //站长之家解析
    private String parseChinaz(Document document) {
        String url = null;

        Element target = null;

        Elements targetElements = document.select("li.clearfix.LCliTheOne");
        if (targetElements.size() > 0) {
            target = targetElements.first();
        } else {
            Elements elements = document.select("li.clearfix");
            if (elements.size() > 0) {
                target = elements.first();
            }
        }
        if (target != null) {
            Element urlElement = target.select("span.col-gray").first();
            url = urlElement.text();
        }
        return url;
    }

    //百度解析
    private String parseBaidu(Document html) {
        String url = null;

        Element domainHome = html.getElementById("1");
        Element target = domainHome.select("a.c-showurl").first();
        if (target != null) {
            url = target.text();
        }
        return url;
    }

}
