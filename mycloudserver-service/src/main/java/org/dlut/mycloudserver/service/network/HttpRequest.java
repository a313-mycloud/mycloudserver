package org.dlut.mycloudserver.service.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class HttpRequest {
    private static Logger log = LoggerFactory.getLogger(HttpRequest.class);

    /**
     * @param str_url
     * @param params
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String post(String str_url, HashMap<String, String> params) throws Exception {
        URL url = new URL(str_url);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);

        JSONObject json = new JSONObject();
        Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            json.put(key, value);
        }
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();
        InputStreamReader reder = new InputStreamReader(conn.getInputStream(), "utf-8");
        BufferedReader breader = new BufferedReader(reder);
        String content = "";
        String result = "";
        while ((content = breader.readLine()) != null) {
            result += content;
        }
        return result;

    }
}
