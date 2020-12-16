package cn.edu.xmu.address.controller;

import cn.edu.xmu.address.AddressApplication;
import cn.edu.xmu.address.model.vo.AddressVo;
import cn.edu.xmu.address.model.vo.RegionVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = AddressApplication.class)
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

    /**
     * @Created at 12/8 16:02
     * @author zrh
     * 查看上级地区 成功
     * @throws Exception
     */
    @Test
    public void seleteParentRegion() throws Exception{
        String token = creatTestToken(1L,-2L,100);
        String responseString="";
        try{
            responseString=this.mvc.perform(get("/address/region/1/ancestor").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("id",2);
        expectedJSON.put("pid",3);
        expectedJSON.put("name","222");
        expectedJSON.put("postalCode",22);
        expectedJSON.put("state",1);
        JSONObject expectedString=new JSONObject();
        expectedString.put("errno",0);
        expectedString.put("errmsg","成功");
        expectedString.put("data",expectedJSON);
        String expectedResponse=expectedString.toString();
        try{
            JSONAssert.assertEquals(expectedResponse,responseString,false);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * @Created at 12/8 21:59
     * @author zrh
     * 上级地区废弃
     * @throws Exception
     */
    @Test
    public void selectParentRegion1() throws Exception{
        String token= creatTestToken(1L,-2L,100);
        String responseString="";
        try{
            responseString=this.mvc.perform(get("/address/region/2/ancestor").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",602);
        expectedJSON.put("errmsg","地区已废弃");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Created at 12/8 22:22
     * @author zrh
     * @throws Exception
     */
    @Test
    public void selectParentRegion2() throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responseString="";
        try{
            responseString=this.mvc.perform(get("/address/region/3/ancestor").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON= new JSONObject();
        expectedJSON.put("errno",504);
        expectedJSON.put("errmsg","上级地区不存在");
        String expectedString =expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * @Created at 12/8 22:22
     * @author zrh
     * @throws Exception
     */
    @Test
    public void selectParentRegion3() throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responseString="";
        try{
            responseString=this.mvc.perform(get("/address/region/5/ancestor").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON= new JSONObject();
        expectedJSON.put("errno",504);
        expectedJSON.put("errmsg","地区id不存在");
        String expectedString =expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * @Created at 12/10 14:56
     * @author zrh
     * 添加子地区成功
     * @throws Exception
     */
    @Test
    public void insertRegion() throws Exception{
        String token=creatTestToken(1L,3L,100);
        String responseString="";
        RegionVo regionVo=new RegionVo();
        regionVo.setName("444");
        regionVo.setPostalCode(444L);
        String requireJson= JacksonUtil.toJson(regionVo);
        try{
            responseString=this.mvc.perform(post("/address/shops/0/regions/3/subregions").header("authorzation",token)
            .contentType("application/json;charset=UTF-8")
            .content(requireJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",0);
        expectedJSON.put("errmsg","成功");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/10 14:56
     * @author zrh
     * 无权限增加
     * @throws Exception
     */
    @Test
    public void insertRegion1() throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responseString="";
        RegionVo regionVo=new RegionVo();
        regionVo.setName("444");
        regionVo.setPostalCode(444L);
        String requireJson= JacksonUtil.toJson(regionVo);
        try{
            responseString=this.mvc.perform(post("/address/shops/0/regions/3/subregions").header("authorzation",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",503);
        expectedJSON.put("errmsg","无权限增加地区");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/10 16:00
     * @author zrh
     * 地区无效错误
     * @throws Exception
     */
    @Test
    public void insertRegion2() throws Exception{
        String token=creatTestToken(1L,0L,100);
        String responseString="";
        RegionVo regionVo=new RegionVo();
        regionVo.setName("222");
        regionVo.setPostalCode(222L);
        String requireJson= JacksonUtil.toJson(regionVo);
        try{
            responseString=this.mvc.perform(post("/address/shops/0/regions/3/subregions").header("authorzation",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",504);
        expectedJSON.put("errmsg","新增失败：222");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/10 17:25
     * @author zrh
     * 更新成功
     * @throws Exception
     */
    @Test
    public void updateRegion()throws Exception{
        String token = creatTestToken(1L,0L,100);
        String responseString ="";
        RegionVo vo=new RegionVo();
        vo.setName("999");
        vo.setPostalCode(99L);
        String requireJson=JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/address/shops/0/regions/1").header("authorization",token)
            .contentType("application/json;charset=UTF-8")
            .content(requireJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",0);
        expectedJSON.put("errmsg","成功");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * @Created at 12/10 18:10
     * @author zrh
     * 地区id不存在
     * @throws Exception
     */
    @Test
    public void updateRegion1()throws Exception{
        String token = creatTestToken(1L,0L,100);
        String responseString ="";
        RegionVo vo=new RegionVo();
        vo.setName("999");
        vo.setPostalCode(99L);
        String requireJson=JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/address/shops/0/regions/5").header("authorization",token)
                    .contentType("application/json;charset=UTF-8")
                    .content(requireJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }

        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",504);
        expectedJSON.put("errmsg","地区id不存在");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * @Created at 12/10 21:27
     * @author zrh
     * 废弃地区成功
     * @throws Exception
     */
    @Test
    public void cancleRegion()throws Exception{
        String token=creatTestToken(1L,0L,100);
        String responseString="";
        try{
            responseString=this.mvc.perform(delete("/address/shops/0/regions/1").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",0);
        expectedJSON.put("errmsg","成功");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * @Created at 12/10 21:28
     * @author zrh
     * 地区已废弃 废弃失败
     * @throws Exception
     */
    @Test
    public void cancleRegion1()throws Exception{
        String token=creatTestToken(1L,0L,100);
        String responseString="";
        try{
            responseString=this.mvc.perform(delete("/address/shops/0/regions/3").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJSON=new JSONObject();
        expectedJSON.put("errno",602);
        expectedJSON.put("errmsg","地区已废弃");
        String expectedString = expectedJSON.toString();
        try{
            JSONAssert.assertEquals(expectedString,responseString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

}