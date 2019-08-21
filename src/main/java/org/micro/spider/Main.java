package org.micro.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
        System.out.println(JSON.toJSONString(parse()));
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
