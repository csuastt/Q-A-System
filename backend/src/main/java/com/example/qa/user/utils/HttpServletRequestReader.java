package com.example.qa.user.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class HttpServletRequestReader
{

    /**
     * @param request  Http请求包
     * @return         request中的请求体的字符串
     */
    public static String ReadAsChars(HttpServletRequest request) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null)
                sb.append(str);
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (null != br) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
