package com.traburiss.tools.test.utils;

import com.traburiss.tools.utils.DESUtils;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.security.Key;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

/**
 * com.traburiss.tools.utils
 *
 * @author ltc
 * @version 1.0.0.1
 * create at 2018/4/27 10:15
 */
public class DESedeCoderTest {

    private Logger logger = Logger.getLogger(DESedeCoderTest.class.getName());

    @Test
    @DisplayName("随机中文测试")
    public void cnTest() throws Exception {

        byte[] keyByte = DESUtils.initKey();
        Key key = DESUtils.keyByteToKey(keyByte);
        String text = radomCn(10);
        byte[] encryptData = DESUtils.encrypt(text.getBytes(), key.getEncoded());
        byte[] decryptData = DESUtils.decrypt(encryptData, key.getEncoded());
        String result = new String(decryptData);
        logger.info("cn text is " + text + " result is " + result);
        assert Arrays.equals(text.getBytes(), decryptData);
    }

    @Test
    @DisplayName("随机英文测试")
    public void enTest() throws Exception {

        byte[] keyByte = DESUtils.initKey();
        Key key = DESUtils.keyByteToKey(keyByte);
        String text = radomEn(10);
        byte[] encryptData = DESUtils.encrypt(text.getBytes(), key.getEncoded());
        byte[] decryptData = DESUtils.decrypt(encryptData, key.getEncoded());
        String result = new String(decryptData);
        logger.info("en text is " + text + " result is " + result);
        assert Arrays.equals(text.getBytes(), decryptData);
    }

    @Test
    @DisplayName("随机文本测试")
    public void chTest() throws Exception {

        byte[] keyByte = DESUtils.initKey();
        Key key = DESUtils.keyByteToKey(keyByte);
        String text = radomCh(10);
        byte[] encryptData = DESUtils.encrypt(text.getBytes(), key.getEncoded());
        byte[] decryptData = DESUtils.decrypt(encryptData, key.getEncoded());
        String result = new String(decryptData);
        logger.info("ch text is " + text + " result is " + result);
        assert Arrays.equals(text.getBytes(), decryptData);
    }

    private String radomCn(int len){

        Character start = '一';
        Character end = '李';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {

            Character now = (char) (new Random().nextInt(end - start) + start);
            sb.append(now);
        }
        return sb.toString();
    }

    private String radomEn(int len){

        Character start = 'A';
        Character end = 'z';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {

            Character now = (char) (new Random().nextInt(end - start) + start);
            sb.append(now);
        }
        return sb.toString();
    }

    private String radomCh(int len){

        Character start = Character.MIN_VALUE;
        Character end = Character.MAX_VALUE;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {

            Character now = (char) (new Random().nextInt(end - start) + start);
            sb.append(now);
        }
        return sb.toString();
    }
}
