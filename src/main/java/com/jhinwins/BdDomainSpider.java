package com.jhinwins;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

/**
 * Created by Jhinwins on 2017/7/25  14:11.
 * Desc:
 */
public class BdDomainSpider {

    public static void main(String[] params) throws ClassNotFoundException, SQLException {
    }

    //递归的数量
    int recursionCount = 0;

    public String findByNameBaseBaidu(String name) {
        ++recursionCount;
        //如果递归数量大于2则结束递归
        if (recursionCount > 2) {
            System.out.println("jump out : " + recursionCount);
            return null;
        }
        try {
            Thread.sleep(20000 + ((int) (Math.random() * 10000)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String url = null;

        String html = "";

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet("http://www.baidu.com/s?wd=" + name + "官网");
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
            Element domainHome = document.getElementById("1");
            Element target = domainHome.select("a.c-showurl").first();
            if (target != null) {
                url = target.text();
            }

        } else {
            url = findByNameBaseBaidu(name);
        }
        recursionCount = 0;
        return url;
    }


}
