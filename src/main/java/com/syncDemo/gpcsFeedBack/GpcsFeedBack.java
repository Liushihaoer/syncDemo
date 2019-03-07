package com.syncDemo.gpcsFeedBack;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syncDemo.common.ApiResponse;

public class GpcsFeedBack {

    Logger log = Logger.getLogger(getClass());

    public void start() throws Exception {
        log.info("GpcsFeedBack.start.......");
        String domain_prop = "";
        String driver_prop = "";
        String url_prop = "";
        String user_prop = "";
        String password_prop = "";
        String time_prop = "";
        boolean flag = false;
        // 读取配置文件
        try (FileInputStream in = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(in);
            domain_prop = properties.getProperty("domain");
            driver_prop = properties.getProperty("driver");
            url_prop = properties.getProperty("url");
            user_prop = properties.getProperty("user");
            password_prop = properties.getProperty("password");
            time_prop = properties.getProperty("gpcsFeedBackTime");
            flag = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("GpcsFeedBack.start.loadProperties.error", ex);
        }
        log.info("GpcsFeedBack.start 【domain_prop : " + domain_prop + ", driver_prop : " + driver_prop + ", url_prop : "
                + url_prop + ", user_prop : " + user_prop + ", password_prop : " + password_prop + "time_prop : "
                + time_prop + ", flag : " + flag + "】");
        if (!flag) {
            return;
        }
        String domain = domain_prop;
        String driver = driver_prop;
        String url = url_prop;
        String user = user_prop;
        String password = password_prop;
        long time = Integer.parseInt(time_prop);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ScheduledExecutorService feedBackScheduled = Executors.newScheduledThreadPool(1);
        feedBackScheduled.scheduleWithFixedDelay(() -> {
            Connection connection = null;
            Statement statement = null;
            try {
                Date date = new Date();
                String times = dateFormat.format(date);
                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                ObjectMapper objectMapper = new ObjectMapper();
                // 创建默认的httpclient
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String uri = domain + "/api/szlf/getFbakDate";
                HttpPost fbakDatePost = new HttpPost(uri);
                // 执行请求操作，并拿到结果（同步阻塞）
                log.info("execute_getFbakDateApi");
                CloseableHttpResponse fbakDateResponse = httpClient.execute(fbakDatePost);
                log.info("execute_getFbakDateApi.response : " + fbakDateResponse);
                // 获取结果实体
                HttpEntity fbakDateEntity = fbakDateResponse.getEntity();
                String fbakDate = null;
                if (fbakDateEntity != null) {
                    String responseList = EntityUtils.toString(fbakDateEntity, "utf-8");
                    log.info("getFbakDateApi.resonseList : " + responseList);
                    ApiResponse apiResponse = objectMapper.readValue(responseList, ApiResponse.class);
                    Boolean success = apiResponse.getSuccess();
                    if (success != null && success) {
                        fbakDate = (String) apiResponse.getResult();
                    } else {
                        log.error("getFbakDateApi.error : " + apiResponse.getMessage());
                    }
                    // 关流
                    EntityUtils.consume(fbakDateEntity);
                    fbakDateResponse.close();
                }
                
                if (fbakDate == null || "".equals(fbakDate)) {
                    fbakDate = "1970-01-01 00:00:00";
                }
                
                // 通过时间段查询商品信息
                String sql = "select dtl.fincid, dtl.fnote, dtl.fqty from v_so_item_jys dtl left join v_so_jys doc on dtl.fid = doc.fid "
                        + "where doc.fbak_date <= SYSTIMESTAMP and doc.fbak_date >= TO_DATE('" + fbakDate + "','yyyy-mm-dd hh24:mi:ss')";
                ResultSet resultSet = statement.executeQuery(sql);
                List<Map<String, Object>> feedBackList = new ArrayList<>();
                if (resultSet != null) {
                    while (resultSet.next()) {
                        Map<String, Object> feedBack = new HashMap<>();
                        String fincid = resultSet.getString("FINCID");
                        feedBack.put("fincid", fincid);
                        String fnote = resultSet.getString("FNOTE");
                        feedBack.put("fnote", fnote);
                        String fqty = resultSet.getString("FQTY");
                        feedBack.put("fqty", fqty);
                        feedBack.put("times", times);
                        
                        feedBackList.add(feedBack);
                    }
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                
                log.info("feedBackScheduled.feedBackList : " + feedBackList.toString());
                if (feedBackList != null && feedBackList.size() > 0) {
                    // 调用ibs配送回传API
                    uri = domain + "/api/szlf/gpcsFeedBack";
                    // 创建post请求对象
                    HttpPost httpPost = new HttpPost(uri);
                    // 装填请求参数
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    String feedBackResult = objectMapper.writeValueAsString(feedBackList);
                    list.add(new BasicNameValuePair("feedBackResult", feedBackResult));
                    // 设置参数到请求对象中
                    httpPost.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
                    // 执行请求操作，并拿到结果（同步阻塞）
                    log.info("feedBackScheduled.gpcsFeedBackApi");
                    CloseableHttpResponse response = httpClient.execute(httpPost);
                    log.info("gpcsFeedBackApi.response : " + response);
                    // 获取结果实体
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseList = EntityUtils.toString(entity, "utf-8");
                        log.info("gpcsFeedBackApi.resonseList : " + responseList);
                        ApiResponse gpcsFeedBackResponse = objectMapper.readValue(responseList, ApiResponse.class);
                        Boolean success = gpcsFeedBackResponse.getSuccess();
                        if (success == null || !success) {
                            log.error("gpcsFeedBackApi.error response : " + response);
                        }
                    }
                    // 关流
                    EntityUtils.consume(entity);
                    response.close();
                }
                
                httpClient.close();
                connection.commit();
                log.info("feedBackScheduled_commit........................................");
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("feedBackScheduled.error", ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error("feedBackScheduled.closeConnection.error", ex);
                    }
                }
            }
        } , 0, time, TimeUnit.MINUTES);

    }

}
