package com.buko;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author 徐健威
 */
@Data
@Slf4j
public class Json {
    public static String httpRequest(String string) {
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(string);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                try (InputStream inputStream = conn.getInputStream();
                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                     BufferedReader in = new BufferedReader(inputStreamReader)) {
                    String str;
                    while ((str = in.readLine()) != null) {
                        result.append(str);
                    }
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void printf(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                printf(JSON.parseObject(String.valueOf(entry.getValue())));
            } else {
                log.debug("key: {}, value: {}", entry.getKey(), entry.getValue());
            }
        }
    }

    public static void tran(String s, String file) {
        try {
            s = s.replaceAll("[0-9]", "-");
            File json = new File(file);
            if (!json.exists()) {
                if (!json.createNewFile()) {
                    throw new RuntimeException("创建文件失败");
                }
            }
            byte[] bytes;
            bytes = s.getBytes();

            try (FileOutputStream fos = new FileOutputStream(json)) {
                fos.write(bytes);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String s = httpRequest("https://gw.tvs.qq.com/echo?msg=abc123xyz");
        log.debug(s);
        printf(JSON.parseObject(s));
        // linux 下: sed -i 's/[0-9]/-/g' file.json
        tran(s, "file.json");
    }
}
