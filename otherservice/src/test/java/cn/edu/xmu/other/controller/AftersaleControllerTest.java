package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.other.OtherServiceApplication;
import cn.edu.xmu.other.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.other.model.bo.Aftersale;
import cn.edu.xmu.other.model.po.AftersaleServicePo;
import cn.edu.xmu.other.model.vo.AftersaleDeliverVo;
import cn.edu.xmu.other.model.vo.AftersaleSendbackVo;
import cn.edu.xmu.other.model.vo.AftersaleVo;
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
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OtherServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AftersaleControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private AftersaleServicePoMapper aftersalePoMapper;

    private static final Logger logger = LoggerFactory.getLogger(AftersaleControllerTest.class);

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }


    /*
    修改售后信息
    修改成功
     */
    @Test
    public void aftersaleUpdateTest() throws Exception{
        AftersaleVo vo=new AftersaleVo();
        vo.setConsignee("test");
        vo.setMobile(null);
        vo.setReason("test");
        vo.setRegionId(null);
        vo.setQuantity(2);
        vo.setDetail(null);

        String token=creatTestToken(1L,0L,100);

        String aftersaleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/aftersale/aftersales/1").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
            AftersaleServicePo updatedPo = aftersalePoMapper.selectByPrimaryKey(1L);
            Assert.state(updatedPo.getQuantity().equals(2), "修改数量失败");
            Assert.state(updatedPo.getReason().equals("test"), "修改原因失败");
            Assert.state(updatedPo.getConsignee().equals("test"), "修改联系人失败");
            Assert.state(updatedPo.getRefund().equals(20L), "修改退款金额失败");
            Assert.state(Aftersale.State.getTypeByCode(updatedPo.getState().intValue()).equals(Aftersale.State.CHECK), "修改状态失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    修改售后信息
    当前状态不能进行修改
     */
    @Test
    public void aftersaleUpdateTest1() throws Exception{
        AftersaleVo vo=new AftersaleVo();
        vo.setConsignee("test");
        vo.setMobile(null);
        vo.setReason("test");
        vo.setRegionId(null);
        vo.setQuantity(2);
        vo.setDetail(null);

        String token=creatTestToken(3L,0L,100);

        String aftersaleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/aftersale/aftersales/3").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":609,\"errmsg\":\"售后单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    修改售后信息
    售后单不存在
     */
    @Test
    public void aftersaleUpdateTest2() throws Exception{
        AftersaleVo vo=new AftersaleVo();
        vo.setConsignee("test");
        vo.setMobile(null);
        vo.setReason("test");
        vo.setRegionId(null);
        vo.setQuantity(2);
        vo.setDetail(null);

        String token=creatTestToken(1L,0L,100);

        String aftersaleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/aftersale/aftersales/20").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    /*
    修改售后信息
    售后单已删除
     */
    @Test
    public void aftersaleUpdateTest3() throws Exception{
        AftersaleVo vo=new AftersaleVo();
        vo.setConsignee("test");
        vo.setMobile(null);
        vo.setReason("test");
        vo.setRegionId(null);
        vo.setQuantity(2);
        vo.setDetail(null);

        String token=creatTestToken(5L,0L,100);

        String aftersaleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/aftersale/aftersales/16").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    /*
    修改售后信息
    没有权限修改
     */
    @Test
    public void aftersaleUpdateTest4() throws Exception{
        AftersaleVo vo=new AftersaleVo();
        vo.setConsignee("test");
        vo.setMobile(null);
        vo.setReason("test");
        vo.setRegionId(null);
        vo.setQuantity(2);
        vo.setDetail(null);

        String token=creatTestToken(1L,0L,100);

        String aftersaleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/aftersale/aftersales/2").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    /*
    获取所有状态
     */
    @Test
    public void aftersaleStateTest()throws Exception{
        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(get("/aftersale/aftersales/states").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"data\": [ {\"name\": \"待管理员审核\", \"code\": 0 }, {\"name\": \"待买家发货\", \"code\": 1 }, {\"name\": \"买家已发货\", \"code\": 2 }, {\"name\": \"待店家退款\", \"code\": 3 }, {\"name\": \"待店家发货\", \"code\": 4 }, {\"name\": \"店家已发货\", \"code\": 5 }, {\"name\": \"审核不通过\", \"code\": 6 }, {\"name\": \"已取消\", \"code\": 7 }, {\"name\": \"已结束\", \"code\": 8 } ], \"errmsg\": \"成功\" }";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    买家填写售后的运单信息
    成功
     */
    @Test
    public void aftersaleSendBackTest()throws Exception{
        AftersaleSendbackVo vo=new AftersaleSendbackVo();
        vo.setCustomerLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/2/sendback").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
            AftersaleServicePo updatedPo = aftersalePoMapper.selectByPrimaryKey(2L);
            Assert.state(updatedPo.getCustomerLogSn().equals("20201203"), "买家发货失败");
            Assert.state(Aftersale.State.getTypeByCode(updatedPo.getState().intValue()).equals(Aftersale.State.SENDBACKING), "买家发货失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    买家填写售后运单信息
    当前状态不能修改
     */
    @Test
    public void aftersaleSendBackTest1()throws Exception{
        AftersaleSendbackVo vo=new AftersaleSendbackVo();
        vo.setCustomerLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/1/sendback").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":609,\"errmsg\":\"售后单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    买家填写售后运单信息
    售后单不存在
     */
    @Test
    public void aftersaleSendBackTest2()throws Exception{
        AftersaleSendbackVo vo=new AftersaleSendbackVo();
        vo.setCustomerLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/20/sendback").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    买家确认售后单结束
    成功
     */
    @Test
    public void aftersaleConfirmTest()throws Exception{

        String token=creatTestToken(5L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/5/confirm").header("authorization",token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
            AftersaleServicePo updatedPo = aftersalePoMapper.selectByPrimaryKey(5L);
            Assert.state(Aftersale.State.getTypeByCode(updatedPo.getState().intValue()).equals(Aftersale.State.SUCESS), "确认售后结束失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    买家确认售后结束
    当前状态不能修改
     */
    @Test
    public void aftersaleConfirmTest1()throws Exception{

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/1/confirm").header("authorization",token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":609,\"errmsg\":\"售后单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    买家确认售后结束
    售后单不存在
     */
    @Test
    public void aftersaleConfirmTest2()throws Exception{

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/20/comfirm").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    店家寄出维修好（调换）的货物
    成功
 */
    @Test
    public void aftersaleDeliverTest()throws Exception{
        AftersaleDeliverVo vo=new AftersaleDeliverVo();
        vo.setShopLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(5L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/3/aftersales/4/deliver").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
            AftersaleServicePo updatedPo = aftersalePoMapper.selectByPrimaryKey(4L);
            Assert.state(Aftersale.State.getTypeByCode(updatedPo.getState().intValue()).equals(Aftersale.State.DELIVERING), "确认售后结束失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    店家寄出维修好（调换）的货物
    当前状态不能修改
     */
    @Test
    public void aftersaleDeliverTest1()throws Exception{
        AftersaleDeliverVo vo=new AftersaleDeliverVo();
        vo.setShopLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/1/aftersales/1/deliver").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":609,\"errmsg\":\"售后单状态禁止\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    店家寄出维修好（调换）的货物
    售后单不存在
     */
    @Test
    public void aftersaleDeliverTest2()throws Exception{
        AftersaleDeliverVo vo=new AftersaleDeliverVo();
        vo.setShopLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/3/aftersales/20/deliver").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    店家寄出维修好（调换）的货物
    没有权限修改
     */
    @Test
    public void aftersaleDeliverTest3()throws Exception{
        AftersaleDeliverVo vo=new AftersaleDeliverVo();
        vo.setShopLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/1/aftersales/4/deliver").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
