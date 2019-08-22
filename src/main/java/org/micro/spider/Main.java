package org.micro.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Main {

    private static final String REGEX = "g_page_config = \\{(.*?)\\}\\;";

    public static void main(String[] args) throws Exception {
        String cookie = "cookie2=1836ed7f2c09c6044dacab6550f44632; t=260997cf9f7d2d3817ed05afc967c33e; _tb_token_=b77a3375e57; cna=fNdaFTC1lH8CAXZ0D5HWhFc1; v=0; unb=667973791; uc3=id2=VWn7iBuaIu92&nk2=o%2FNV%2Fiqr4j4%3D&lg2=WqG3DMC9VAQiUQ%3D%3D&vt3=F8dBy3MPTaQh7Cuwx8M%3D; csg=08481633; lgc=%5Cu674E%5Cu666F%5Cu67AB99; cookie17=VWn7iBuaIu92; dnk=%5Cu674E%5Cu666F%5Cu67AB99; skt=7ee374f9f2ef4151; existShop=MTU2NjQ0ODg2Mw%3D%3D; uc4=id4=0%40V8jl9Eau4DFYkyfyD0uxOxjLWtE%3D&nk4=0%40oaWe6%2FXC2uIBjhJyIiy6iAMeEA%3D%3D; publishItemObj=Ng%3D%3D; tracknick=%5Cu674E%5Cu666F%5Cu67AB99; _cc_=W5iHLLyFfA%3D%3D; tg=0; _l_g_=Ug%3D%3D; sg=91e; _nk_=%5Cu674E%5Cu666F%5Cu67AB99; cookie1=VvbcIEAFt2%2FrmQFJFSw9rt1%2BJ93GGSXyVI6VGoVEtWU%3D; enc=KsYgvf7LTp7GTxl4JmZfL71eB81VZJnRmMshZ5mXdHBt6ByK%2BVtLRFSRxX9x1yiSRR90FuxfT6otbIX3CMPWkg%3D%3D; mt=ci=43_1; thw=cn; hng=CN%7Czh-CN%7CCNY%7C156; alitrackid=www.taobao.com; lastalitrackid=nz.taobao.com; uc1=cookie14=UoTaHo8K%2F2PpOg%3D%3D&cookie15=U%2BGCWk%2F75gdr5Q%3D%3D; JSESSIONID=E95A1BD027C8E1837E9649FB61D05CF4; l=cBPTxLtqqz-YKEKyBOCZourza779SIRAguPzaNbMi_5pr6L6iY_OkJz0nFp6cjWdtrLB4M4Lmjp9-etbi-y06Pt-g3fP.; isg=BCAgni6XaT17I9U5UJ1sQ4li8Shj9wTzdN4IGJox6DvOlcC_QjhRgz4nKX2wJbzL";
        System.out.println(JSON.toJSONString(parse(cookie, "女装", 0.01, 1000.00, 1)));
    }

    public static List<Goods> parse(String cookie, String keywords, Double startPrice, Double endPrice, Integer page) throws Exception {
        App app = App.TAO_BAOl;

        try {
            String json = parseJson(app, cookie, keywords, startPrice, endPrice, page);
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

            Map<App, Map<String, String>> appFieldMapping = App.APP_FIELD_MAPPING;
            Map<String, String> fieldMapping = appFieldMapping.get(app);
            int auctionsSize = auctions.size();
            List<JSONObject> jsonObjectList = new ArrayList<>();
            for (int i = 0; i < auctionsSize; i++) {
                JSONObject goodsJson = new JSONObject();
                JSONObject tempJson = auctions.getJSONObject(i);
                for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
                    Object value = tempJson.get(entry.getValue());
                    if ("view_sales".equals(entry.getValue())) {
                        String salesStr = String.valueOf(value).replace("人付款", "").replace("人收货", "");
                        Integer sales;
                        if (salesStr.contains("万+")) {
                            sales = (int) (Double.valueOf(salesStr.substring(0, salesStr.length() - 2)) * 10000);
                        } else if (salesStr.contains("+")) {
                            sales = Integer.valueOf(salesStr.substring(0, salesStr.length() - 1));
                        } else {
                            sales = Integer.valueOf(salesStr);
                        }
                        goodsJson.put(entry.getKey(), sales);
                    } else {
                        goodsJson.put(entry.getKey(), value);
                    }
                }
                jsonObjectList.add(goodsJson);
            }

            return JSON.parseArray(JSON.toJSONString(jsonObjectList), Goods.class);
        } catch (Exception e) {
            log.error("商品解析失败", e);
            return Collections.emptyList();
        }
    }

    private static String parseJson(App app, String cookie, String keywords, Double startPrice, Double endPrice, Integer page) throws Exception {
        String url = app.buildUrl(keywords, startPrice, endPrice, page);
        Connection connection = Jsoup.connect(url);
        connection.header("cookie", cookie);
        Document document = connection.get();
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
