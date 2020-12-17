package cn.edu.xmu.advertisement;

import cn.edu.xmu.advertisement.model.vo.AdvertisementUpdateVo;
import cn.edu.xmu.advertisement.model.vo.AuditAdVo;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AdvertisementApplication.class)
@AutoConfigureMockMvc
@Transactional
class AdvertisementApplicationTests {
    @Autowired
    private MockMvc mvc;
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementApplicationTests.class);
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }
    /**
     * 设置默认广告 成功
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void setDefaultAdTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/122/default").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 设置默认广告 失败 id不存在
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void setDefaultAdTest2(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/2/default").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":504}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改广告内容 成功
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:43
     */
    @Test
    public void updateAdTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AdvertisementUpdateVo vo = new AdvertisementUpdateVo();
        vo.setBeginDate("2020-12-12");
        vo.setEndDate("2020-12-13");
        vo.setContent("test1");
        vo.setLink("/test1");
        vo.setRepeat(true);
        vo.setWeight(5);
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改广告内容 成功
     * 仅修改开始日期
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:43
     */
    @Test
    public void updateAdTest2(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AdvertisementUpdateVo vo = new AdvertisementUpdateVo();
        vo.setBeginDate("2019-12-12");
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 修改广告内容 成功
     * 仅修改结束日期
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:43
     */
    @Test
    public void updateAdTest3(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AdvertisementUpdateVo vo = new AdvertisementUpdateVo();
        vo.setEndDate("2022-12-12");
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改广告内容 失败 开始日期大于结束日期
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:43
     */
    @Test
    public void updateAdTest4(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AdvertisementUpdateVo vo = new AdvertisementUpdateVo();
        vo.setBeginDate("2020-12-14");
        vo.setEndDate("2020-12-13");
        vo.setContent("test1");
        vo.setLink("/test1");
        vo.setRepeat(true);
        vo.setWeight(5);
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":610}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改广告内容 失败 id不存在
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:43
     */
    @Test
    public void updateAdTest5(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AdvertisementUpdateVo vo = new AdvertisementUpdateVo();
        vo.setBeginDate("2020-12-12");
        vo.setEndDate("2020-12-13");
        vo.setContent("test1");
        vo.setLink("/test1");
        vo.setRepeat(true);
        vo.setWeight(5);
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/1").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":504}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 修改广告内容 失败 开始日期格式不正确
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:43
     */
    @Test
    public void updateAdTest6(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AdvertisementUpdateVo vo = new AdvertisementUpdateVo();
        vo.setBeginDate("2020 12 12");
        vo.setEndDate("2020-12-13");
        vo.setContent("test1");
        vo.setLink("/test1");
        vo.setRepeat(true);
        vo.setWeight(5);
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":503}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 修改广告内容 失败 开始日期大于结束日期
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:43
     */
    @Test
    public void updateAdTest7(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AdvertisementUpdateVo vo = new AdvertisementUpdateVo();
        vo.setBeginDate("2022-12-14");
        //vo.setEndDate("");
        vo.setContent("test1");
        vo.setLink("/test1");
        vo.setRepeat(true);
        vo.setWeight(5);
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":610}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 上架广告 成功
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void onshelvesAdTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            //this.mvc.perform(put("/advertise/shops/1/advertisement/123/offshelves").header("authorization",token));
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123/onshelves").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 上架广告 失败 id不存在
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void onshelvesAdTest2(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/2/onshelves").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":504}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 下架广告 成功
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void offshelvesAdTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123/offshelves").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 下架广告 失败 id不存在
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void offshelvesAdTest2(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/2/offshelves").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":504}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除广告 成功
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void deleteAdTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            this.mvc.perform(put("/advertise/shops/1/advertisement/123/offshelves").header("authorization",token));
            responseString=this.mvc.perform(delete("/advertise/shops/1/advertisement/123").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除广告 失败 id不存在
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void deleteAdTest2(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(delete("/advertise/shops/1/advertisement/12").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":504}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除广告 失败 广告状态禁止（默认广告）
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void deleteAdTest3(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(delete("/advertise/shops/1/advertisement/121").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":608}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除广告 失败 广告状态禁止（上线广告）
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:32
     */
    @Test
    public void deleteAdTest4(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(delete("/advertise/shops/1/advertisement/123").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":608}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 审核广告 失败 广告状态禁止
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/15 15:17
     */
    @Test
    public void auditAdTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AuditAdVo vo = new AuditAdVo();
        vo.setConclusion(true);
        vo.setMessage("test audit ok");
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/123/audit").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":608}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 审核广告 失败 广告id不存在
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/15 15:17
     */
    @Test
    public void auditAdTest2(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        AuditAdVo vo = new AuditAdVo();
        vo.setConclusion(true);
        vo.setMessage("test audit ok");
        String adJson = JacksonUtil.toJson(vo);
        try{
            responseString=this.mvc.perform(put("/advertise/shops/1/advertisement/12/audit").header("authorization",token).contentType("application/json;charset=UTF-8").content(adJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":504}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
