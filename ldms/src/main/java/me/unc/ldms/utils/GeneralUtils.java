package me.unc.ldms.utils;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description 通用工具类
 * @Date 2020/2/8 17:51
 * @author LZS
 * @version v1.0
 */
//@Component
public class GeneralUtils {

    /**
     * 日期转字符串
     * @param date 日期
     * @return 日期字符串
     */
    public static String parseToYYYYMMDDStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    /**
     * 通用日期转换字符串
     * @param date 日期
     * @param pattern 格式
     * @return 日期字符串
     */
    public static String parseDateToStr(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * JSHash
     * Justin Sobel提出的基于位运算的哈希函数。
     * @param str 字符串
     * @return Hash值
     */
    public static int genHash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash ^= (hash << 5) + (int)str.charAt(i) + (hash >> 2);
        }
        return hash & 0x7FFFFFFF;
    }

    /**
     * 合并数组
     * @param first 第一个数组
     * @param second 第二个数组
     * @param <T> 泛型T
     * @return 合并后的数组
     */
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * 判断邮箱格式
     * @param mail 邮箱地址
     * @return boolean
     */
    public static boolean filterMailAddress(String mail) {
        return mail.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
    }

    /**
     * 发送邮件
     * @param to 收件邮箱
     */
    @Deprecated
    public static void mailService(String to) {
        if (!filterMailAddress(to)) {
            throw new IllegalArgumentException("邮箱格式错误");
        }
        //设置发件人
        final String from = "714299056@qq.com";
        //key
        final String key = "znesczhogvpwbbag";
        //设置邮件发送的服务器，这里为QQ邮件服务器
        String host = "smtp.qq.com";
        //获取系统属性
        Properties properties = System.getProperties();
        //SSL加密
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            //设置系统属性
            properties.setProperty("mail.smtp.host", host);
            properties.put("mail.smtp.auth", "true");

            //获取发送邮件会话、获取第三方登录授权码
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    //第三方登录授权码
                    return new PasswordAuthentication(from, key);
                }
            });

            MimeMessage message = new MimeMessage(session);
            //防止邮件被当然垃圾邮件处理，披上Outlook的马甲
            message.addHeader("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");
            //设置发件人
            message.setFrom(new InternetAddress(from));
            //设置收件人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            //邮件主题
            message.setSubject("测试发送邮件");
            //消息体（正文）
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText("测试 --来自希罗哥的问候，今晚记得打部落战");
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
//            //附件
//            bodyPart = new MimeBodyPart();
//            String fileName = "D:\\a.txt";
//            DataSource dataSource = new FileDataSource(fileName);
//            bodyPart.setDataHandler(new DataHandler(dataSource));
//            //文件显示的名称
//            bodyPart.setFileName("a.txt");
//            multipart.addBodyPart(bodyPart);
            //整合
            message.setContent(multipart);

            //发送
            Transport.send(message);
            System.out.println("发送成功！");
        } catch (GeneralSecurityException | MessagingException e) {
            e.printStackTrace();
            System.out.println("发送失败");
        }
    }

    /**
     * 生成验证码
     * @return 验证码
     */
    public static String VerificationCode() {
        return RandomStringUtils.randomAlphanumeric(5);
    }

    /**
     * 比较两个bean的不同
     * @param beforeObj 原bean
     * @param afterObj 更改后的bean
     * @return 更改的属性名
     * @throws Exception exception
     */
    public static List<String> compareObj(Object beforeObj, Object afterObj) throws Exception{
        List<String> different = new ArrayList<>();

        if(beforeObj == null) {
            throw new IllegalArgumentException("原对象不能为空");
        }
        if(afterObj == null) {
            throw new IllegalArgumentException("新对象不能为空");
        }
        if(!beforeObj.getClass().isAssignableFrom(afterObj.getClass())){
            throw new IllegalArgumentException("两个对象不相同，无法比较");
        }

        //取出属性
        Field[] beforeFields = beforeObj.getClass().getDeclaredFields();
        Field[] afterFields = afterObj.getClass().getDeclaredFields();
        Field.setAccessible(beforeFields, true);
        Field.setAccessible(afterFields, true);

        //遍历取出差异值
        if(beforeFields.length > 0){
            for(int i=0; i<beforeFields.length; i++){
                Object beforeValue = beforeFields[i].get(beforeObj);
                Object afterValue = afterFields[i].get(afterObj);
                if((beforeValue != null && !"".equals(beforeValue) && !beforeValue.equals(afterValue)) || ((beforeValue == null || "".equals(beforeValue)) && afterValue != null)){
                    different.add(beforeFields[i].getName());
                }
            }
        }

        return different;
    }

    /**
     * 字符串首字母大写
     * @param str 字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);

//        char[] ch = str.toCharArray();
//        if (ch[0] >= 'a' && ch[0] <= 'z') {
//            ch[0] = (char) (ch[0] - 32);
//        }
//        return new String(ch);
    }

}
