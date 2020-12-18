package cn.edu.xmu.aftersale.controller;

import cn.edu.xmu.aftersale.AftersaleApplication;
import cn.edu.xmu.aftersale.mapper.AftersaleServicePoMapper;
import cn.edu.xmu.aftersale.model.bo.Aftersale;
import cn.edu.xmu.aftersale.model.po.AftersaleServicePo;
import cn.edu.xmu.aftersale.model.vo.*;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
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

@SpringBootTest(classes = AftersaleApplication.class)   //标识本类是一个SpringBootTest
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

    @Test
    public void createToken(){
        String token=creatTestToken(3835L,-2L,1000000000);
        System.out.println(token);
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

        String token=creatTestToken(1L,-2L,100);

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

        String token=creatTestToken(3L,-2L,100);

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

        String token=creatTestToken(1L,-2L,100);

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

        String token=creatTestToken(5L,-2L,100);

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

        String token=creatTestToken(1L,-2L,100);

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
    成功
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


    @Test
    public void aftersaleStateTest1()throws Exception{
        String token="tokenTest";

        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(get("/aftersale/aftersales/states").header("authorization", token))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":501, \"errmsg\": \"JWT不合法\" }";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void aftersaleStateTest2()throws Exception{

        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(get("/aftersale/aftersales/states"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":704, \"errmsg\": \"需要先登录\" }";
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
        vo.setLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(2L,-2L,100);

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
        vo.setLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,-2L,100);

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
        vo.setLogSn("20201203");
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,-2L,100);

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
    买家填写运单号
    售后单已删除
     */
    @Test
    public void aftersaleSendBackTest3()throws Exception{
        AftersaleSendbackVo vo=new AftersaleSendbackVo();
        vo.setLogSn("20201206");
        String aftersaleJson=JacksonUtil.toJson(vo);

        String token=creatTestToken(5L,-2L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/16/sendback").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try{
            JSONAssert.assertEquals(expectedString,responseString,false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void aftersaleSendBackTest4()throws Exception {
        AftersaleSendbackVo vo = new AftersaleSendbackVo();
        vo.setLogSn(null);
        String aftersaleJson = JacksonUtil.toJson(vo);

        String token = creatTestToken(2L, -2L, 100);

        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(put("/aftersale/aftersales/2/sendback").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":503,\"errmsg\":\"运单信息不能为空\"}";
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

        String token=creatTestToken(4L,0L,100);

        String expectedResponse = "";
        String responseString = null;

        try{
            responseString=this.mvc.perform(put("/aftersale/aftersales/5/confirm").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
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
            Assert.state(Aftersale.State.getTypeByCode(updatedPo.getState().intValue()).equals(Aftersale.State.DELIVERING), "店家寄出货物失败");
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



    /*
    买家取消售后单
    成功
     */
    @Test
    public void aftersaleCancelTest()throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(delete("/aftersale/aftersales/1").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"errmsg\":\"成功\"}";
        try{
            JSONAssert.assertEquals(expectedString,responseString,false);
            AftersaleServicePo po=aftersalePoMapper.selectByPrimaryKey(1L);
            Assert.state(Aftersale.State.getTypeByCode(po.getState().intValue()).equals(Aftersale.State.CANCEL),"取消售后单失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /*
    买家删除售后单
    成功
     */
    @Test
    public void aftersaleDeleteTest()throws Exception{
        String token=creatTestToken(3L,-2L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(delete("/aftersale/aftersales/7").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"errmsg\":\"成功\"}";
        try{
            JSONAssert.assertEquals(expectedString,responseString,false);
            AftersaleServicePo po=aftersalePoMapper.selectByPrimaryKey(7L);
            Assert.state(po.getBeDeleted().intValue()==1,"删除售后单失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    管理员同意售后
    成功
     */
    @Test
    public void aftersaleComfirmByShopTest()throws Exception{
        AftersaleConfirmVo vo=new AftersaleConfirmVo();
        vo.setConfirm(true);
        vo.setConclusion("同意");
        String aftersaleJson=JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/1/aftersales/1/confirm").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"errmsg\":\"成功\"}";
        try{
            JSONAssert.assertEquals(expectedString,responseString,false);
            AftersaleServicePo po=aftersalePoMapper.selectByPrimaryKey(1L);
            Assert.state(po.getConclusion().equals("同意"),"审核售后单失败");
            Assert.state(Aftersale.State.getTypeByCode(po.getState().intValue()).equals(Aftersale.State.SENDBACKWAIT),"审核售后单失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /*
    管理员不同意售后
    成功
     */
    @Test
    public void aftersaleConfirmByShopTest()throws Exception{
        AftersaleConfirmVo vo=new AftersaleConfirmVo();
        vo.setConfirm(false);
        vo.setPrice(20);
        vo.setType(0);
        vo.setConclusion("不同意");
        String aftersaleJson=JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/1/aftersales/1/confirm").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"errmsg\":\"成功\"}";
        try{
            JSONAssert.assertEquals(expectedString,responseString,false);
            AftersaleServicePo po=aftersalePoMapper.selectByPrimaryKey(1L);
            Assert.state(po.getConclusion().equals("不同意"),"审核售后单失败");
            Assert.state(Aftersale.State.getTypeByCode(po.getState().intValue()).equals(Aftersale.State.DISAGREE),"审核售后单失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /*
    店家验收换货物品
    成功
     */
    @Test
    public void aftersaleRecieveTest()throws Exception{
        AftersaleConfirmVo vo=new AftersaleConfirmVo();
        vo.setConfirm(true);
        vo.setConclusion("同意换货");
        String aftersaleJson=JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/2/aftersales/3/recieve").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedString,responseString,false);
            AftersaleServicePo po=aftersalePoMapper.selectByPrimaryKey(3L);
            Assert.state(po.getConclusion().equals("同意换货"),"店家验收失败");
            Assert.state(Aftersale.State.getTypeByCode(po.getState().intValue()).equals(Aftersale.State.DILIVERWAIT),"店家验收失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    /*
    店家验收退货物品
    成功
     */
    @Test
    public void aftersaleRecieveTest1()throws Exception{
        AftersaleConfirmVo vo=new AftersaleConfirmVo();
        vo.setConfirm(true);
        vo.setConclusion("同意退货");
        String aftersaleJson=JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/2/aftersales/11/recieve").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"errmsg\":\"成功\"}";

        try{
            JSONAssert.assertEquals(expectedString,responseString,false);
            AftersaleServicePo po=aftersalePoMapper.selectByPrimaryKey(11L);
            Assert.state(po.getConclusion().equals("同意退货"),"店家验收失败");
            Assert.state(Aftersale.State.getTypeByCode(po.getState().intValue()).equals(Aftersale.State.REFUNDWAIT),"店家验收失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /*
    店家验收
    验收不通过
     */
    @Test
    public void aftersaleRecieveTest2()throws Exception{
        AftersaleConfirmVo vo=new AftersaleConfirmVo();
        vo.setConfirm(false);
        vo.setConclusion("验收不通过");
        String aftersaleJson=JacksonUtil.toJson(vo);

        String token=creatTestToken(1L,0L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(put("/aftersale/shops/2/aftersales/11/recieve").header("authorization",token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"errmsg\":\"成功\"}";

        try{
            JSONAssert.assertEquals(expectedString,responseString,false);
            AftersaleServicePo po=aftersalePoMapper.selectByPrimaryKey(11L);
            Assert.state(po.getConclusion().equals("验收不通过"),"店家验收失败");
            Assert.state(Aftersale.State.getTypeByCode(po.getState().intValue()).equals(Aftersale.State.SENDBACKWAIT),"店家验收失败");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }







    /*
    新建售后单
    成功
     */
    @Test
    public void aftersaleCreateTest()throws Exception{
        CreateAftersaleVo vo=new CreateAftersaleVo();
        vo.setType((byte)0);
        vo.setQuantity(1);
        vo.setReason("test");
        vo.setRegionId(null);
        vo.setDetail(null);
        vo.setConsignee("江");
        vo.setMobile("15206067798");

        String token = creatTestToken(1L, 0L, 100);
        String aftersaleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/aftersale/orderItems/17/aftersales").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"data\":{\"id\":19,\"orderId\":10,\"orderItemId\":17,\"skuId\":1,\"skuName\":\"ipad\",\"customerId\":1,\"shopId\":1,\"type\":0,\"reason\":\"test\",\"conclusion\":null,\"refund\":100,\"quantity\":1,\"regionId\":null,\"detail\":null,\"consignee\":\"江\",\"mobile\":\"15206067798\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    /*
    新建售后单
    该订单已申请售后
     */
    @Test
    public void aftersaleCreateTest1()throws Exception{
        CreateAftersaleVo vo=new CreateAftersaleVo();
        vo.setType((byte)0);
        vo.setQuantity(1);
        vo.setReason("test");
        vo.setRegionId(null);
        vo.setDetail(null);
        vo.setConsignee("江");
        vo.setMobile("15206067798");

        String token = creatTestToken(1L, 0L, 100);
        String aftersaleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/aftersale/orderItems/1/aftersales").header("authorization", token).contentType("application/json;charset=UTF-8").content(aftersaleJson))
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
    买家查询某个售后单
    成功
     */
    @Test
    public void aftersaleGetByIdTest()throws Exception{
        String token = creatTestToken(3835L, -2L, 100);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(get("/aftersale/aftersales/1").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"orderId\":10,\"orderSn\":\"20201204\",\"orderItemId\":1,\"skuId\":1,\"skuName\":\"ipad\",\"customerId\":1,\"shopId\":1,\"serviceSn\":\"20200101\",\"type\":0,\"reason\":\"test1\",\"conclusion\":null,\"refund\":50,\"quantity\":5,\"regionId\":null,\"detail\":null,\"consignee\":\"江\",\"mobile\":\"15206067798\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    店家获取自己店铺的某个售后单
    成功
     */
    @Test
    public void aftersaleGetByShopIdTest()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(get("/aftersale/shops/1/aftersales/1").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"data\":{\"id\":1,\"orderId\":10,\"orderSn\":\"20201204\",\"orderItemId\":1,\"skuId\":1,\"skuName\":\"ipad\",\"customerId\":1,\"shopId\":1,\"serviceSn\":\"20200101\",\"type\":0,\"reason\":\"test1\",\"conclusion\":null,\"refund\":50,\"quantity\":5,\"regionId\":null,\"detail\":null,\"consignee\":\"江\",\"mobile\":\"15206067798\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /*
    买家查询自己的售后单
    成功
     */
    @Test
    public void aftersaleGetByUserIdTest()throws Exception{
        String token = creatTestToken(3835L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(get("/aftersale/aftersales?beginTime=&endTime=&page=1&pageSize=2&type=&state=").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"data\":{\"total\":4,\"pages\":2,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":1,\"orderId\":10,\"orderSn\":\"20201204\",\"orderItemId\":1,\"skuId\":1,\"skuName\":\"ipad\",\"customerId\":1,\"shopId\":1,\"serviceSn\":\"20200101\",\"type\":0,\"reason\":\"test1\",\"conclusion\":null,\"refund\":50,\"quantity\":5,\"regionId\":null,\"detail\":null,\"consignee\":\"江\",\"mobile\":\"15206067798\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0},{\"id\":4,\"orderId\":10,\"orderSn\":\"20201204\",\"orderItemId\":4,\"skuId\":1,\"skuName\":\"ipad\",\"customerId\":1,\"shopId\":3,\"serviceSn\":\"20200104\",\"type\":0,\"reason\":\"test4\",\"conclusion\":null,\"refund\":100,\"quantity\":1,\"regionId\":null,\"detail\":null,\"consignee\":\"江\",\"mobile\":\"15206067798\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":4}]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void aftersaleGetAllTest()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(get("/aftersale/shops/1/aftersales?beginTime=&endTime=&page=1&pageSize=2&type=&state=").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"data\":{\"total\":6,\"pages\":3,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":1,\"orderId\":10,\"orderItemId\":1,\"customerId\":1,\"shopId\":1,\"serviceSn\":\"20200101\",\"type\":0,\"reason\":\"test1\",\"conclusion\":null,\"refund\":50,\"quantity\":5,\"regionId\":null,\"detail\":null,\"consignee\":\"江\",\"mobile\":\"15206067798\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0},{\"id\":2,\"orderId\":10,\"orderItemId\":2,\"customerId\":2,\"shopId\":1,\"serviceSn\":\"20200102\",\"type\":0,\"reason\":\"test2\",\"conclusion\":null,\"refund\":50,\"quantity\":2,\"regionId\":null,\"detail\":null,\"consignee\":\"高\",\"mobile\":\"13459784977\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":1}]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
