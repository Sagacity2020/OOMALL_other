package cn.edu.xmu.share.controller;


import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;

import cn.edu.xmu.share.ShareServiceApplication;
import cn.edu.xmu.share.dao.ShareActivityDao;
import cn.edu.xmu.share.mapper.ShareActivityPoMapper;
import cn.edu.xmu.share.model.bo.Rule;
import cn.edu.xmu.share.model.bo.Stategy;
import cn.edu.xmu.share.model.po.ShareActivityPo;
import cn.edu.xmu.share.model.vo.ShareActivityVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ShareServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class ShareControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(ShareControllerTest.class);

    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    @Test
    //测试用户查找自己的分享
    public void getShares() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/shares/?page=1&pageSize=1").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442316,\n" +
                "                \"sharerId\": 2,\n" +
                "\"sku\": {\n" +
                "                    \"price\": 666\n" +
                "                }," +
                "                \"quantity\": 1,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    @Test
    //测试用户查找自己的分享
    public void getShares1() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/shares?page=1&pageSize=4").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":13,\"pages\":4,\"pageSize\":4,\"page\":1,\"list\":[{\"id\":442316,\"sharerId\":2,\"sku\":{\"price\":666}},{\"id\":442318,\"sharerId\":2,\"sku\":{\"price\":666}},{\"id\":442319,\"sharerId\":2,\"sku\":{\"price\":666}},{\"id\":442321,\"sharerId\":2,\"sku\":{\"price\":666}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找自己的分享 goodsSpuId
    public void getShares2() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/shares").header("authorization",token)
                .queryParam("goodsSpuId", "406"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":278496,\"sharerId\":2,\"goodsSpuId\":406,\"quantity\":684,\"gmtCreate\":\"2020-12-01T23:04:45\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    //测试用户查找自己的分享 beginTime
    public void getShares3() throws Exception{

        String token = creatTestToken(781L,1L,100);

        String responseString = this.mvc.perform(get("/share/shares").header("authorization",token)
                .queryParam("beginTime", "2020-12-01 23:04:50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":279064,\"sharerId\":781,\"goodsSpuId\":624,\"quantity\":928,\"gmtCreate\":\"2020-12-01T23:05:45\"},{\"id\":279244,\"sharerId\":781,\"goodsSpuId\":488,\"quantity\":566,\"gmtCreate\":\"2020-12-01T23:06:45\"},{\"id\":279436,\"sharerId\":781,\"goodsSpuId\":483,\"quantity\":391,\"gmtCreate\":\"2020-12-01T23:07:45\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    @Autowired
    ShareActivityDao shareActivityDao;
    @Test
    //测试用户查找自己的分享 beginTime endTime
    public void getShares4() throws Exception{

        String token = creatTestToken(781L,1L,100);

        String responseString = this.mvc.perform(get("/share/shares").header("authorization",token)
                .queryParam("beginTime", "2020-12-01 23:04:50")
                .queryParam("endTime", "2020-12-01 23:07:30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":279064,\"sharerId\":781,\"goodsSpuId\":624,\"quantity\":928,\"gmtCreate\":\"2020-12-01T23:05:45\"},{\"id\":279244,\"sharerId\":781,\"goodsSpuId\":488,\"quantity\":566,\"gmtCreate\":\"2020-12-01T23:06:45\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    //测试用户查找自己的分享
    public void getSharesAdmin() throws Exception{

        String token = creatTestToken(2L,0L,100);

        String responseString = this.mvc.perform(get("/shops/0/skus/501/shares?beginTime=2020-12-06 22:00:00&endTime=2020-12-07 22:00:00&page=1&pageSize=1").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 442327,\n" +
                "                \"sharerId\": 1912,\n" +
                "\"sku\": {\n" +
                "                    \"price\": 666\n" +
                "                }," +
                "                \"quantity\": 720,\n" +
                "                \"gmtCreate\": \"2020-12-07T21:47:20\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";


        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找自己的分享
    public void getSharesAdmin1() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/shops/1/shares?page=1&pageSize=4").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":25,\"pages\":7,\"pageSize\":4,\"page\":1,\"list\":[{\"id\":442347,\"sharerId\":136,\"sku\":{\"price\":666}},{\"id\":442507,\"sharerId\":1787,\"sku\":{\"price\":666}},{\"id\":442766,\"sharerId\":1914,\"sku\":{\"price\":666}},{\"id\":442833,\"sharerId\":2476,\"sku\":{\"price\":666}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找自己的分享成功
    public void getBeShared() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/beshared?beginTime=2020-12-22:00:00&endTime=2019-12-44 :00:00&page=1&pageSize=1").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 1,\n" +
                "        \"page\": 1,\n" +
                "        \"list\": []\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";


        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找自己的分享成功
    public void getBeShared1() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/beshared?page=2&pageSize=4").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":13,\"pages\":4,\"pageSize\":4,\"page\":2,\"list\":[{\"id\":434159,\"sku\":{\"price\":666}},{\"id\":434160,\"sku\":{\"price\":666}},{\"id\":434171,\"sku\":{\"price\":666}},{\"id\":434526,\"sku\":{\"price\":666}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找自己的分享成功 goodsSpuId
    public void getBeShared2() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/beshared").header("authorization",token)
                .queryParam("goodsSpuId", "448"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":368619,\"goodsSpuId\":448,\"sharerId\":2,\"shareId\":null,\"customerId\":null,\"orderItemId\":null,\"rebate\":null,\"gmtCreate\":\"2020-12-03T21:45:44\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    //测试用户查找自己的分享成功 goodsSpuId beginTime endTime
    public void getBeShared3() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/beshared").header("authorization",token)
                .queryParam("goodsSpuId", "448")
                .queryParam("beginTime", "2020-12-01 23:04:50")
                .queryParam("endTime", "2020-12-02 23:07:30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":10,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    //测试管理员查找自己店铺的分享成功
    public void getBeSharedAdmin() throws Exception{

        String token = creatTestToken(2L,0L,100);

        String responseString = this.mvc.perform(get("/shops/0/skus/505/beshared?beginTime=2020-:00&endTime=2020-12-07 22:00:00").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\n" +
                "    \"errno\": 503\n" +
                "}";


        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试管理员查找自己店铺的分享成功
    public void getBeSharedAdmin1() throws Exception{

        String token = creatTestToken(2L,1L,100);

        String responseString = this.mvc.perform(get("/share/shops/1/beshared?page=2&pageSize=4").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":25,\"pages\":7,\"pageSize\":4,\"page\":2,\"list\":[{\"id\":434725,\"sku\":{\"price\":666}},{\"id\":434824,\"sku\":{\"price\":666}},{\"id\":434892,\"sku\":{\"price\":666}},{\"id\":434927,\"sku\":{\"price\":666}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找分享活动 shopId spuId
    public void getShareActivities() throws Exception{

        String token = creatTestToken(781L,1L,100);

        String responseString = this.mvc.perform(get("/shareactivities?skuId=501&page=2&pageSize=5").header("authorization",token)
                //.queryParam("shopId", "2"))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"pageSize\": 5,\n" +
                "        \"page\": 2,\n" +
                "        \"list\": [\n" +
                "            {\n" +
                "                \"id\": 306268,\n" +
                "                \"shopId\": 0,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 306288,\n" +
                "                \"shopId\": 0,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 306361,\n" +
                "                \"shopId\": 0,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 306366,\n" +
                "                \"shopId\": 0,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": 307518,\n" +
                "                \"shopId\": 0,\n" +
                "                \"skuId\": 501,\n" +
                "                \"beginTime\": \"2020-12-07T21:47:19\",\n" +
                "                \"endTime\": \"2021-10-10T23:23:23\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";


        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找分享活动 shopId spuId
    public void getShareActivities1() throws Exception{

        String token = creatTestToken(781L,1L,100);

        String responseString = this.mvc.perform(get("/share/shareactivities").header("authorization",token)
                .queryParam("shopId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":274933,\"goodsSpuId\":666}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试用户查找分享活动 shopId spuId
    public void getShareActivities2() throws Exception{

        String token = creatTestToken(781L,1L,100);

        String responseString = this.mvc.perform(get("/share/shareactivities").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":274933,\"shopId\":2,\"goodsSpuId\":666},{\"id\":274937,\"shopId\":0,\"goodsSpuId\":667},{\"id\":274939,\"shopId\":2,\"goodsSpuId\":668}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    @Test
    //测试新建分享
    public void createShare() throws Exception{

        String token = creatTestToken(2L,0L,100);

        String responseString1= null;
        try{
            responseString1 = this.mvc.perform(put("/shops/0/shareactivities/304113/online").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        //Boolean flag = shareActivityDao.isShared(2L,668L);
        //Boolean flag=true;
        String responseString= null;
        try{
            responseString = this.mvc.perform(post("/skus/501/shares").header("authorization",token))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"sharerId\": 2," +
                "        \"sku\": {" +
                "            \"price\": 666\n" +
                "        },\n" +
                "        \"quantity\": 0\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
        JSONObject jsonObject = JSONObject.parseObject(new String(responseString)).getJSONObject("data");
        Long shareId = jsonObject.getLong("id");


        String responseString2 = this.mvc.perform(get("/shares?skuId=501&page=1&pageSize=1").header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonObject2 = JSONObject.parseObject(new String(responseString2)).getJSONObject("data");
        JSONArray jsonArray = jsonObject2.getJSONArray("list");
        Long shareId2 = jsonArray.getJSONObject(0).getLong("id");
        JSONAssert.assertEquals(shareId.toString(), shareId2.toString(), false);



    }

    @Test
    //测试新建分享
    public void createShare1() throws Exception{

        String token = creatTestToken(781L,0L,100);
        //Boolean flag = shareActivityDao.isShared(2L,668L);
        Boolean flag=true;
        String responseString= null;
        try{
            responseString = this.mvc.perform(post("/share/skus/666/shares").header("authorization",token))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"data\":{\"sharerId\":781,\"sku\":{\"price\":666}},\"errmsg\":\"成功\"}";
        //else
          //  expectedResponse = "{\"errno\":0,\"data\":{\"sharerId\":781,\"sku\":{\"price\":666}},\"errmsg\":\"失败\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }
    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;

    @Test
    //测试管理员新建分享活动 shopId spuId
    public void createShareActivity() throws Exception{

        String token = creatTestToken(781L,0L,100);
        //Boolean flag = shareActivityDao.isShared(2L,668L);

        ShareActivityVo vo = new ShareActivityVo();
        vo.setBeginTime("2021-11-30 23:59:00");
        vo.setEndTime("2021-12-15 23:23:23");
        vo.setStrategy("{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                "}"
        );
        String responseString= null;
//        String sset = "{\"rule\":[{\"num\":2, \"rate\":0.11},{\"num\":5, \"rate\":0.2},{\"num\":8, \"rate\":0.3}],\"firstOrAvg\":0}";
//        String st = "{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}";
//        ShareActivityPo po = shareActivityPoMapper.selectByPrimaryKey(307696L);
//        String sst = StringEscapeUtils.unescapeJava(st);
//        Stategy s = JacksonUtil.toObj(sst, Stategy.class);
//
//            System.out.println(s.getFirstOrAvg());
//        List<Rule> rule = s.getRule();
//        for(Rule r : rule){
//            if(r.getNum() == null)
//                System.out.println("num is null");
//            else
//                System.out.println("num is"+r.getNum());
//            if(r.getRate() == null)
//                System.out.println("rate is null");
//            else
//                System.out.println("rate is"+r.getRate());
//        }

        try{
            responseString = this.mvc.perform(post("/share/shops/0/skus/501/shareactivities").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse ="{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                "        \"shopId\": 0,\n" +
                "        \"skuId\": 501,\n" +
                "        \"beginTime\": \"2021-11-30T23:59\",\n" +
                "        \"endTime\": \"2021-12-15T23:23:23\",\n" +
                "        \"state\": 1\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试管理员新建分享活动 shopId spuId
    public void createShareActivity1() throws Exception{

        String token = creatTestToken(781L,1L,100);
        //Boolean flag = shareActivityDao.isShared(2L,668L);

        ShareActivityVo vo = new ShareActivityVo();
        vo.setBeginTime("2020-12-04 15:30:50");
        vo.setEndTime("2021-12-04 15:30:50");
        vo.setStrategy("****");
        String responseString= null;
//        String sset = "{\"rule\":[{\"num\":2, \"rate\":0.11},{\"num\":5, \"rate\":0.2},{\"num\":8, \"rate\":0.3}],\"firstOrAvg\":0}";
//        String st = "{\\\"rule\\\" :[{ \\\"num\\\" :0},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}";
//        ShareActivityPo po = shareActivityPoMapper.selectByPrimaryKey(307696L);
//        String sst = StringEscapeUtils.unescapeJava(st);
//        Stategy s = JacksonUtil.toObj(sst, Stategy.class);
//
//            System.out.println(s.getFirstOrAvg());
//        List<Rule> rule = s.getRule();
//        for(Rule r : rule){
//            if(r.getNum() == null)
//                System.out.println("num is null");
//            else
//                System.out.println("num is"+r.getNum());
//            if(r.getRate() == null)
//                System.out.println("rate is null");
//            else
//                System.out.println("rate is"+r.getRate());
//        }

        try{
            responseString = this.mvc.perform(post("/share/shops/1/goods/666/shareactivities").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"errno\":0,\"data\":{\"shopId\":1,\"goodsSpuId\":666,\"beginTime\":\"2020-12-04T15:30:50\",\"endTime\":\"2021-12-04T15:30:50\",\"strategy\":\"****\",\"state\":0},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试管理员修改分享活动 shopId shareActivity
    public void putShareActivity() throws Exception{

        String token = creatTestToken(70L,0L,100);

        ShareActivityVo vo1 = new ShareActivityVo();
        vo1.setBeginTime("2022-07-30 23:59:00");
        vo1.setEndTime("2022-08-15 23:23:23");
        vo1.setStrategy("{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                "}"
        );
        String responseString1= null;
        try{
            responseString1 = this.mvc.perform(post("/shops/0/skus/502/shareactivities").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo1)))
                    //.andExpect(status().)
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSONObject.parseObject(new String(responseString1)).getJSONObject("data");
        Long shareActivityId = jsonObject.getLong("id");


        ShareActivityVo vo = new ShareActivityVo();
        vo.setBeginTime("2021-12-10 21:47:19");
        vo.setEndTime("2021-12-07 23:23:23");
        vo.setStrategy("{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                "}"


        );
        String responseString= null;
        try{
            responseString = this.mvc.perform(put("/shops/0/shareactivities/"+shareActivityId).header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    //.andExpect(status().isNotFound())
                    //.andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\n" +
                "    \"errno\": 610\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    @Test
    //测试管理员修改分享活动 shopId shareActivity
    public void putShareActivity1() throws Exception{

        String token = creatTestToken(70L,0L,100);

        ShareActivityVo vo = new ShareActivityVo();
        vo.setBeginTime("2020-12-04 15:30:50");
        vo.setEndTime("2019-12-04 15:30:50");
        vo.setStrategy("strategy");
        String responseString= null;
        try{
            responseString = this.mvc.perform(put("/share/shops/0/shareactivities/270304").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"errno\":610,\"errmsg\":\"开始时间大于结束时间\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试管理员修改分享活动 shopId shareActivity
    public void putShareActivity2() throws Exception{

        String token = creatTestToken(70L,0L,100);

        ShareActivityVo vo = new ShareActivityVo();
        vo.setBeginTime("2020-12-04 15:30:50");
        vo.setEndTime("2021-12-04 15:30:50");
        vo.setStrategy("strategy");
        String responseString= null;
        try{
            responseString = this.mvc.perform(put("/share/shops/0/shareactivities/270304").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试管理员删除分享活动 shopId shareActivity
    public void deleteShareActivity() throws Exception{

        String token = creatTestToken(70L,0L,100);

        String responseString= null;
        try{
            responseString = this.mvc.perform(delete("/shops/1/shareactivities/303068").header("authorization",token))
                    //.andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\n" +
                "    \"errno\": 505\n" +
                "}";
        ;
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    //测试管理员删除分享活动 shopId shareActivity
    public void deleteShareActivity1() throws Exception{

        String token = creatTestToken(70L,0L,100);

        String responseString= null;
        try{
            responseString = this.mvc.perform(delete("/share/shops/0/shareactivities/270304").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    //测试管理员修改分享活动状态 shopId shareActivity
    public void putShareActivityState() throws Exception{

        String token = creatTestToken(70L,0L,100);

        ShareActivityVo vo1 = new ShareActivityVo();
        vo1.setBeginTime("2023-03-30 23:59:00");
        vo1.setEndTime("2023-04-15 23:23:23");
        vo1.setStrategy("{\\\"rule\\\" :[{ \\\"num\\\" :0, \\\"rate\\\":1},{ \\\"num\\\" :10, \\\"rate\\\":10}],\\\"firstOrAvg\\\" : 0}\"\n" +
                "}"
        );
        String responseString1= null;
        try{
            responseString1 = this.mvc.perform(post("/shops/0/skus/502/shareactivities").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(JacksonUtil.toJson(vo1)))
                    //.andExpect(status().)
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSONObject.parseObject(new String(responseString1)).getJSONObject("data");
        Long shareActivityId = jsonObject.getLong("id");

        String responseString= null;
        try{
            responseString = this.mvc.perform(put("/shops/0/shareactivities/"+shareActivityId+"/online").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\n" +
                "    \"errno\": 0\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    @Test
    //测试管理员修改分享活动状态 shopId shareActivity
    public void putShareActivityState1() throws Exception{

        String token = creatTestToken(70L,0L,100);

        String responseString= null;
        try{
            responseString = this.mvc.perform(put("/share/shops/2/shareactivities/274934/online").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"errno\":605,\"errmsg\":\"分享活动时段冲突了\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    //测试管理员修改分享活动状态 shopId shareActivity
    public void putShareActivityState2() throws Exception{

        String token = creatTestToken(70L,0L,100);

        String responseString= null;
        try{
            responseString = this.mvc.perform(put("/share/shops/2/shareactivities/274935/online").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    //测试管理员修改分享活动状态 shopId shareActivity
    public void putShareActivityState3() throws Exception{

        String token = creatTestToken(70L,0L,100);

        String responseString= null;
        try{
            responseString = this.mvc.perform(put("/share/shops/5/shareactivities/0/online").header("authorization",token))
                    .andExpect(status().is(404))
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String expectedResponse = "{\"errno\":504,\"errmsg\":\"分享活动Id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

}
