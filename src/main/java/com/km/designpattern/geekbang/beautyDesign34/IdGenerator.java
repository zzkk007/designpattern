package com.km.designpattern.geekbang.beautyDesign34;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * 整个 ID 由三部分组成。
 * 第一部分是本机名的最后一个字段。
 * 第二部分是当前时间戳，精确到毫秒。
 * 第三部分是 8 位的随机字符串，包含大小写字母和数字。
 */
public class IdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(IdGenerator.class);

    public static String generate(){
        String id = "";
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            String[] tokens = hostName.split("\\.");
            if (tokens.length > 0){
                hostName = tokens[tokens.length - 1];
            }

            char[] randomChars = new char[8];
            int count = 0;
            Random random = new Random();
            while (count < 8){
                int randomAscii = random.nextInt(122);
                if (randomAscii >= 48 && randomAscii <= 57){
                    randomChars[count] = (char)('0' + (randomAscii - 65));
                    count++;
                }else if (randomAscii >= 65 && randomAscii <= 90) {
                    randomChars[count] = (char)('A' + (randomAscii - 65));
                    count++;
                } else if (randomAscii >= 97 && randomAscii <= 122) {
                    randomChars[count] = (char)('a' + (randomAscii - 97));
                    count++;
                }
            }
            id = String.format("%s-%d-%s", hostName, System.currentTimeMillis(), new String(randomChars));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return id;
    }
}
