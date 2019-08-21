package org.micro.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Main {

    private static final String REGEX = "g_page_config = \\{(.*?)\\}\\;";

    public static void main(String[] args) throws Exception {
//        System.out.println(JSON.toJSONString(parse()));
        Connection connection = Jsoup.connect("https://s.taobao.com/search?q=%E8%A3%99%E5%AD%90%E4%BB%99%E5%A5%B3%E8%B6%85%E4%BB%99%E6%A3%AE%E7%B3%BB");
        connection.header("cookie", "t=430bf2f04ea1a50e00842306d0f61ca1; thw=cn; x=e%3D1%26p%3D*%26s%3D0%26c%3D0%26f%3D0%26g%3D0%26t%3D0%26__ll%3D-1%26_ato%3D0; ali_ab=171.221.137.112.1543159399970.5; _uab_collina=154610135020377644256724; _fbp=fb.1.1556972741577.943744343; UM_distinctid=16c5aebc0e92c6-0c0271982bf8cd-c343162-1fa400-16c5aebc0ea370; hng=CN%7Czh-CN%7CCNY%7C156; enc=dNP8g5L0i5OwAl42LqElZT%2B6E2j1h5oDZW7miNppGj59QahjhcBL5tJ%2B41pk6BYDGxTue5iUJg83t6JE%2BWRhnQ%3D%3D; _m_h5_tk=414fbdd2e132159f5db56d52aa8adc29_1566112187374; _m_h5_tk_enc=3bf5c92bbbc7622770fca6f321784a03; cookie2=1f527cb996588600a99117464c537994; _tb_token_=7948abe7b7605; alitrackid=www.taobao.com; lastalitrackid=www.taobao.com; mt=ci=0_0; v=0; cna=RspxFOqarkgCAavVDIoZOK81; JSESSIONID=071BFE5BDFC5614072410B56BDB5CF09; isg=BKurfO7Fcq9CoK7gZFD7yhLIOs9Sjr9C8yNUmx0pa-pBvMkepZNhkhteFrx3nBc6; l=cBgRqioVqU3IziAbBOfZKurza77O1Idb8sPzaNbMiICPOH1H5NfhWZeIzcTMCnGVK6SeR37uMFCUB-TGyyCqJxpsw3k_J_f..");
        Document document =  connection.get();
        System.out.println(document.html());
    }

    public static List<TaoBaoGoods> parse() throws Exception {
        try {
            String json = parseJson();
            JSONObject jsonObject = JSON.parseObject(json);
            if (jsonObject == null) {
                return Collections.emptyList();
            }
            JSONObject mods = jsonObject.getJSONObject("mods");
            if (mods == null) {
                return Collections.emptyList();
            }
            JSONObject itemList = mods.getJSONObject("itemlist");
            if (itemList == null) {
                return Collections.emptyList();
            }
            JSONObject data = itemList.getJSONObject("data");
            if (data == null) {
                return Collections.emptyList();
            }
            JSONArray auctions = data.getJSONArray("auctions");
            if (auctions == null || auctions.isEmpty()) {
                return Collections.emptyList();
            }

            return JSON.parseArray(auctions.toJSONString(), TaoBaoGoods.class);
        } catch (Exception e) {
            log.error("商品解析失败", e);
            return Collections.emptyList();
        }
    }

    private static String parseJson() throws Exception {
        Document document = Jsoup.parse(IOUtils.resourceToURL("/女装_淘宝搜索.html").openStream(), StandardCharsets.UTF_8.name(), "");
        Elements elements = document.select("script");
        for (Element element : elements) {
            String data = element.data();
            if (data.contains("g_page_config")) {
                return getJsonData(data);
            }
        }

        return "{}";
    }

    private static String getJsonData(String data) {
        Matcher matcher = Pattern.compile(REGEX).matcher(data);
        while (matcher.find()) {
            return "{" + matcher.group(1) + "}";
        }

        return "{}";
    }

}
