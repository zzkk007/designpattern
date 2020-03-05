package com.km.designpattern.geekbang.beautyDesign36;

import com.km.designpattern.geekbang.beautyDesign35.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 *
 *     这段代码中有四个函数。针对这四个函数的出错处理方式，我总结出下面这样几个问题。
 *     (1) 对于 generate() 函数，如果本机名获取失败，函数返回什么？这样的返回值是否合理？
 *     (2) 对于 getLastFiledOfHostName() 函数，是否应该将 UnknownHostException 异常
 *         在函数内部吞掉（try-catch 并打印日志）？还是应该将异常继续往上抛出？
 *         如果往上抛出的话，是直接把 UnknownHostException 异常原封不动地抛出，
 *         还是封装成新的异常抛出？
 *     (3) 对于 getLastSubstrSplittedByDot(String hostName) 函数，
 *         如果 hostName 为 NULL 或者是空字符串，这个函数应该返回什么？
 *
 *     (4) 对于 generateRandomAlphameric(int length) 函数，
 *         如果 length 小于 0 或者等于 0，这个函数应该返回什么？
 */

public class RandomIdGenerator implements IdGenerator {
    private static final Logger logger = LoggerFactory.getLogger(RandomIdGenerator.class);

    @Override
    public String generate() {
        String substrOfHostName = getLastFiledOfHostName();
        long currentTimeMillis = System.currentTimeMillis();
        String randomString = generateRandomAlphameric(8);
        String id = String.format("%s-%d-%s",
                substrOfHostName, currentTimeMillis, randomString);
        return id;
    }

    private String getLastFiledOfHostName() {
        String substrOfHostName = null;
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            substrOfHostName = getLastSubstrSplittedByDot(hostName);
        } catch (UnknownHostException e) {
            logger.warn("Failed to get the host name.", e);
        }
        return substrOfHostName;
    }

    protected String getLastSubstrSplittedByDot(String hostName) {
        String[] tokens = hostName.split("\\.");
        String substrOfHostName = tokens[tokens.length - 1];
        return substrOfHostName;
    }

    protected String generateRandomAlphameric(int length) {
        char[] randomChars = new char[length];
        int count = 0;
        Random random = new Random();
        while (count < length) {
            int maxAscii = 'z';
            int randomAscii = random.nextInt(maxAscii);
            boolean isDigit= randomAscii >= '0' && randomAscii <= '9';
            boolean isUppercase= randomAscii >= 'A' && randomAscii <= 'Z';
            boolean isLowercase= randomAscii >= 'a' && randomAscii <= 'z';
            if (isDigit|| isUppercase || isLowercase) {
                randomChars[count] = (char) (randomAscii);
                ++count;
            }
        }
        return new String(randomChars);
    }
}