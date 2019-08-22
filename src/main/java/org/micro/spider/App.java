package org.micro.spider;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public enum App {

    // ====

    TAO_BAOl() {
        @Override
        protected String buildUrl(Object... args) throws Exception {
            String q = URLEncoder.encode(String.valueOf(args[0]), StandardCharsets.UTF_8.name());
            String filter = URLEncoder.encode("reserve_price[" + (args[1] == null ? "" : args[1]) + "," + (args[2] == null ? "" : args[2]) + "]", StandardCharsets.UTF_8.name());
            String s = String.valueOf(44 * ((int) args[3] - 1));
            return "https://s.taobao.com/search?q=" + q + "&sort=sale-desc&filter=" + filter + "&bcoffset=0&p4ppushleft=,44&s=" + s;
        }
    };

    public static Map<App, Map<String, String>> APP_FIELD_MAPPING = new HashMap<>();

    static {
        try {
            Field[] fields = Goods.class.getDeclaredFields();
            for (Field field : fields) {
                Attrs attrs = field.getDeclaredAnnotation(Attrs.class);
                if (attrs != null) {
                    for (Attr attr : attrs.value()) {
                        Map<String, String> fieldMapping = APP_FIELD_MAPPING.computeIfAbsent(attr.app(), k -> new HashMap<>());
                        fieldMapping.put(field.getName(), attr.value());
                    }
                }
                Attr attr = field.getDeclaredAnnotation(Attr.class);
                if (attr != null) {
                    Map<String, String> fieldMapping = APP_FIELD_MAPPING.computeIfAbsent(attr.app(), k -> new HashMap<>());
                    fieldMapping.put(field.getName(), attr.value());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract String buildUrl(Object... args) throws Exception;

}
