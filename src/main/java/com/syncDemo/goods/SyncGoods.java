package com.syncDemo.goods;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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

public class SyncGoods {

    Logger log = Logger.getLogger(getClass());

    public void start() throws Exception {
        log.info("SyncGoods.start.......");
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
            time_prop = properties.getProperty("goodstime");
            flag = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("syncGoods.start.loadProperties.error", ex);
        }
        log.info("syncGoods.start 【domain_prop : " + domain_prop + ", driver_prop : " + driver_prop + ", url_prop : "
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
        ScheduledThreadPoolExecutor goodsScheduled = new ScheduledThreadPoolExecutor(1);
        goodsScheduled.scheduleWithFixedDelay(() -> {
            Connection connection = null;
            Statement statement = null;
            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                // 查询未被读取的商品信息
                String sql = "select * from spkfk where isnull(read_status,0) = 0";
                ResultSet resultSet = statement.executeQuery(sql);
                List<Map<String, Object>> goodsList = new ArrayList<>();
                if (resultSet != null) {
                    while (resultSet.next()) {
                        Map<String, Object> goods = new HashMap<>();
                        Integer id = resultSet.getInt("id");
                        goods.put("id", id);
                        String spbh = resultSet.getString("spbh");
                        goods.put("spbh", spbh);
                        String spbh_akf = resultSet.getString("spbh_akf");
                        goods.put("spbh_akf", spbh_akf);
                        String spmch = resultSet.getString("spmch");
                        goods.put("spmch", spmch);
                        String zjm = resultSet.getString("zjm");
                        goods.put("zjm", zjm);
                        String dw = resultSet.getString("dw");
                        goods.put("dw", dw);
                        String shpgg = resultSet.getString("shpgg");
                        goods.put("shpgg", shpgg);
                        String shengccj = resultSet.getString("shengccj");
                        goods.put("shengccj", shengccj);
                        String pizhwh = resultSet.getString("pizhwh");
                        goods.put("pizhwh", pizhwh);
                        String jixing = resultSet.getString("jixing");
                        goods.put("jixing", jixing);
                        Integer jlgg = resultSet.getInt("jlgg");
                        goods.put("jlgg", jlgg);
                        String ccfl = resultSet.getString("ccfl");
                        goods.put("ccfl", ccfl);
                        Integer shlv = resultSet.getInt("shlv");
                        goods.put("shlv", shlv);
                        String ifclzbz = resultSet.getString("ifclzbz");
                        goods.put("ifclzbz", ifclzbz);
                        String jingyjm = resultSet.getString("jingyjm");
                        goods.put("jingyjm", jingyjm);
                        String zcpjyxq = resultSet.getString("zcpjyxq");
                        goods.put("zcpjyxq", zcpjyxq);
                        String swbh = resultSet.getString("swbh");
                        goods.put("swbh", swbh);
                        Integer zbz = resultSet.getInt("zbz");
                        goods.put("zbz", zbz);
                        Integer read_status = resultSet.getInt("read_status");
                        goods.put("read_status", read_status);
                        Integer gsp_flag = resultSet.getInt("gsp_flag");
                        goods.put("gsp_flag", gsp_flag);
                        Integer first_flag = resultSet.getInt("first_flag");
                        goods.put("first_flag", first_flag);
                        String barcode = resultSet.getString("barcode");
                        goods.put("barcode", barcode);
                        
                        goodsList.add(goods);
                    }
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                
                log.info("goodsScheduled.goodsList : " + goodsList.toString());
                if (goodsList != null && goodsList.size() > 0) {
                    // 创建默认的httpclient
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    // 调用ibs同步商品API
                    String uri = domain + "/api/IBSWMSAKFJZT/syncGoods";
                    // 创建post请求对象
                    HttpPost httpPost = new HttpPost(uri);
                    // 装填请求参数
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    ObjectMapper objectMapper = new ObjectMapper();
                    String goodsResult = objectMapper.writeValueAsString(goodsList);
                    list.add(new BasicNameValuePair("goodsList", goodsResult));
                    // 设置参数到请求对象中
                    httpPost.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
                    // 执行请求操作，并拿到结果（同步阻塞）
                    log.info("goodsScheduled_syncGoodsApi");
                    CloseableHttpResponse response = httpClient.execute(httpPost);
                    log.info("syncGoodsApi.response : " + response);
                    // 获取结果实体
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseList = EntityUtils.toString(entity, "utf-8");
                        log.info("syncGoodsApi.resonseList : " + responseList);
                        ApiResponse syncGoodsResponse = objectMapper.readValue(responseList, ApiResponse.class);
                        Boolean success = syncGoodsResponse.getSuccess();
                        if (success != null && success) {
                            List<Map<String, Object>> result = (List<Map<String, Object>>) syncGoodsResponse
                                    .getResult();
                            log.info("syncGoodsResponse.result : " + result);
                            if (result != null && result.size() > 0) {
                                // 更新商品读取状态
                                String updateSql = "update spkfk set incamodifytime = getdate(), read_status =1, spbh_akf= ? where spbh = ?";
                                PreparedStatement preparedStatement = connection.prepareStatement(updateSql,
                                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                                result.stream().forEach(goods -> {
                                    String spbh_akf = (String) goods.get("spbh_akf");
                                    String spbh = (String) goods.get("spbh");
                                    if (spbh_akf != null && !"".equals(spbh_akf)) {
                                        try {
                                            preparedStatement.setString(1, spbh_akf);
                                            preparedStatement.setString(2, spbh);
                                            preparedStatement.addBatch();
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            log.error("syncGoodsResponse.preparedStatement.addBatch.error", ex);
                                        }
                                    }
                                });
                                preparedStatement.executeBatch();
                                if (preparedStatement != null) {
                                    preparedStatement.close();
                                }
                            }
                        } else {
                            log.error("syncGoodsApi.error response : " + response);
                        }
                    }
                    // 关流
                    EntityUtils.consume(entity);
                    response.close();
                    httpClient.close();
                }
                
                connection.commit();
                log.info("goodsScheduled_commit........................................");
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("goodsScheduled.error", ex);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.error("goodsScheduled.closeConnection.error", ex);
                    }
                }
            }
        } , 0, time, TimeUnit.MINUTES);

    }

}
