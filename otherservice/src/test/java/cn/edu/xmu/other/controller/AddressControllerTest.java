package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.other.OtherServiceApplication;

import cn.edu.xmu.other.model.vo.AddressVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = OtherServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class AddressControllerTest {

    private Logger logger = LoggerFactory.getLogger(AddressControllerTest.class);


    @Autowired
    private MockMvc mvc;



    private final String creatTestToken(Long userId, Long departId, int expireTime)
    {
        String token = new JwtHelper().createToken(userId,departId,expireTime);
        log.debug(token);
        return token;

    }

    /**
     * @Create 12/3
     * @author zrh
     * 新建地址成功
     * @throws Exception
     */
    @Test
    public void createAddress()throws Exception{
        AddressVo vo=new AddressVo();
        vo.setRegionId(1L);
        vo.setConsignee("a");
        vo.setDetail("abc");
        vo.setMobile("1234567");
        String requireJson= JacksonUtil.toJson(vo);
        logger.debug(requireJson.toString());
        String responseString=null;
        String token=creatTestToken(1L,0L,100);
        String expectedResponse = "";
        try {
            responseString = this.mvc.perform(post("/address/addresses").header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject responseData = new JSONObject();
        responseData.put("regionId",1L);
        responseData.put("detail","abc");
        responseData.put("consignee","a");
        responseData.put("mobile","1234567");
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("errno", 0);
        expectedJson.put("errmsg", "成功");
        expectedJson.put("data", responseData);
        expectedResponse = expectedJson.toString();
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/6 10:03
     * @author zrh
     * 取消默认地址成功
     * @throws Exception
     */
    @Test
    public void cancelDefaultAddress() throws Exception{
        String responseString = null;
        String token = creatTestToken(1L,0L,100);
        try{
            responseString = this.mvc.perform(put("/address/addresses/1/default").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try{
            JSONAssert.assertEquals(expectedResponse,responseString,true);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/6 10:08
     * @author zrh
     * 取消默认地址 id不存在错误
     * @throws Exception
     */

    @Test
    public void cancelDefaultAddress1() throws Exception{
        String responseString = null;
        String token = creatTestToken(1L,0L,100);
        try{
            responseString = this.mvc.perform(put("/address/addresses/5/default").header("authorization",token))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try{
            JSONAssert.assertEquals(expectedResponse,responseString,true);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    /**
     * @Created at 12/6 10:03
     * @author zrh
     * 修改地址信息成功
     * @throws Exception
     */
    @Test
    public void changeAddress() throws Exception{
        String token = creatTestToken(1L,0L,100);
        AddressVo vo=new AddressVo();
        vo.setRegionId(1L);
        vo.setConsignee("a");
        vo.setDetail("abc");
        vo.setMobile("1234567");
        String requireJson= JacksonUtil.toJson(vo);
        logger.debug(requireJson.toString());
        String responseString=null;
        String expectedResponse = "";
        try {
            responseString = this.mvc.perform(put("/address/addresses/1").header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("errno", 0);
        expectedJson.put("errmsg", "成功");

        expectedResponse = expectedJson.toString();
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/6 10:12
     * @author zrh
     * 修改地址信息 地址id不存在
     * @throws Exception
     */
    @Test
    public void changeAddress1() throws Exception{
        String token = creatTestToken(1L,0L,100);
        AddressVo vo=new AddressVo();
        vo.setRegionId(1L);
        vo.setConsignee("a");
        vo.setDetail("abc");
        vo.setMobile("1234567");
        String requireJson= JacksonUtil.toJson(vo);
        logger.debug(requireJson.toString());
        String responseString=null;
        String expectedResponse = "";
        try {
            responseString = this.mvc.perform(put("/address/addresses/5").header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("errno", 504);
        expectedJson.put("errmsg", "地址id不存在：5");

        expectedResponse = expectedJson.toString();
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/6 10:24
     * @author zrh
     * 修改地址信息 用户id不匹配
     * @throws Exception
     */
    @Test
    public void changeAddress2() throws Exception{
        String token = creatTestToken(2L,0L,100);
        AddressVo vo=new AddressVo();
        vo.setRegionId(1L);
        vo.setConsignee("a");
        vo.setDetail("abc");
        vo.setMobile("1234567");
        String requireJson= JacksonUtil.toJson(vo);
        logger.debug(requireJson.toString());
        String responseString=null;
        String expectedResponse = "";
        try {
            responseString = this.mvc.perform(put("/address/addresses/1").header("authorization", token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("errno", 504);
        expectedJson.put("errmsg", "地址id不存在：1");

        expectedResponse = expectedJson.toString();
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Created at 12/6 10:03
     * @author zrh
     * 删除地址成功
     * @throws Exception
     */
    @Test
    public void deleteAddress() throws Exception{
        String token = creatTestToken(1L,0L,100);
        String responseString=null;
        try{
            responseString= this.mvc.perform(delete("/address/addresses/1").header("authorization",token)
            .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("errno",0);
        expectedJson.put("errmsg","成功");
        String expectedString = expectedJson.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/6 10:20
     * @author zrh
     * 删除地址 地址id不存在
     * @throws Exception
     */
    @Test
    public void deleteAddress1() throws Exception{
        String token = creatTestToken(1L,0L,100);
        String responseString=null;
        try{
            responseString= this.mvc.perform(delete("/address/addresses/21").header("authorization",token)
                    .contentType("application/json;charset=UTF-8"))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("errno",504);
        expectedJson.put("errmsg","地址id不存在：21");
        String expectedString = expectedJson.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/6
     * @author zrh
     * 查询地址信息 分页返回
     * @throws Exception
     */
    @Test
    public void selectAllAddress() throws Exception{
        String token = creatTestToken(1L,0L,100);
        String responseString="";
        try{
            responseString= this.mvc.perform(get("/address/addresses?/page=1&pageSize=10").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"\"data\":{\"total\":3,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"region_id\":111,\"detail\":\"111\",\"consignee\":111,\"mobile\":\"111\",\"default\":1},{\"id\":2,\"region_id\":222,\"detail\":\"222\",\"consignee\":222,\"mobile\":\"222\",\"default\":0},{\"id\":3,\"region_id\":333,\"detail\":\"333\",\"consignee\":333,\"mobile\":\"333\",\"default\":0}]}}";
        try{
            JSONAssert.assertEquals(expectedResponse,responseString,false);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }



}
