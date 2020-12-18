package cn.edu.xmu.time;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.time.dao.TimeSegmentDao;
import cn.edu.xmu.time.model.vo.TimeSegmentVo;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TimeServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
public class TimeSegmentControllerTest {
    @Autowired
    private MockMvc mvc;
    private static final Logger logger = LoggerFactory.getLogger(TimeSegmentControllerTest.class);
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    @Autowired
    TimeSegmentDao timeSegmentDao;
    /**
     * 测试获取广告时间段列表 成功
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 16:41
    */
    @Test
    public void selectAdTimeSegmentsTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try{
            responseString=this.mvc.perform(get("/time/shops/1/advertisement/timesegments?page=1&pageSize=5").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"data\":{\"total\":22,\"pages\":5,\"pageSize\":5},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 新增广告时间段 成功
     * @author zwl
     * @param 
     * @return  
     * @Date:  2020/12/14 17:14
    */
    ///id怎么。。。
    @Test
    public void insertAdTimeSegmentTest1(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("2020-12-12 22:00:00");
        vo.setEndTime("2020-12-10 23:00:00");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":0,\"data\":{\"id\":31,\"beginTime\":\"2020-12-12T22:00:00\",\"endTime\":\"2020-12-10T23:00:00\",\"type\":0,\"gmtModified\":null},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 新增广告时间段 失败 时间段冲突（重复）
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 17:43
    */
    @Test
    public void insertAdTimeSegmentTest2(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("2020-12-14 17:00:00");
        vo.setEndTime("2020-12-15 18:00:00");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(responseString);
        String expectedResponse ="{\"errno\":604}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 新增时间段 失败 开始时间为空
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 17:55
    */
    @Test
    public void insertAdTimeSegmentTest3(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("");
        vo.setEndTime("2020-12-15 23:00:00");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(responseString);
        String expectedResponse ="{\"errno\":611}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 新增广告时间段 失败 结束时间为空
     * @author zwl
     * @param 
     * @return  
     * @Date:  2020/12/14 17:57
    */
    @Test
    public void insertAdTimeSegmentTest4(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("2020-12-14 22:00:00");
        vo.setEndTime("");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(responseString);
        String expectedResponse ="{\"errno\":612}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增广告时间段 失败 开始时间大于结束时间
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 17:57
     */
    @Test
    public void insertAdTimeSegmentTest5(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("2020-12-14 23:00:00");
        vo.setEndTime("2020-12-15 22:00:00");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
                    .andExpect(status().isCreated())
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
     * 新增广告时间段 失败 时间段冲突
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 17:43
     */
    @Test
    public void insertAdTimeSegmentTest6(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("2020-12-14 17:30:00");
        vo.setEndTime("2020-12-15 19:30:00");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(responseString);
        String expectedResponse ="{\"errno\":604}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 新增广告时间段 失败 开始时间格式不正确
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 17:43
     */
    @Test
    public void insertAdTimeSegmentTest7(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("2020 12 14 22:00:00");
        vo.setEndTime("2020-12-15 23:00:00");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
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
     * 新增广告时间段 失败 结束时间格式不正确
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 17:43
     */
    @Test
    public void insertAdTimeSegmentTest8(){
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        TimeSegmentVo vo = new TimeSegmentVo();
        vo.setBeginTime("2020-12-14 22:00:00");
        vo.setEndTime("2020-12-15 23 00 00");
        String timeJson = JacksonUtil.toJson(vo);
        try{
            responseString = this.mvc.perform(post("/time/shops/1/advertisement/timesegments").header("authorization", token).contentType("application/json;charset=UTF-8").content(timeJson))
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
     * 删除时间段 成功
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:12
    */
    @Test
    public void deleteAdTimeSegmentTest1(){
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        //测试删除成功
        try {
            responseString = this.mvc.perform(delete("/time/shops/1/advertisement/timesegments/2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除时间段 失败 id不存在
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/14 18:12
     */
    @Test
    public void deleteAdTimeSegmentTest2(){
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        //测试删除成功
        try {
            responseString = this.mvc.perform(delete("/time/shops/1/advertisement/timesegments/30").header("authorization", token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1(){
        System.out.println(timeSegmentDao.getCurrentFlashSaleTimeSegs());
    }
    @Test
    public void test2(){
        if(timeSegmentDao.timeSegIsFlashSale(22L))
            System.out.println("22 true");
        else System.out.println("22 false");
        if(timeSegmentDao.timeSegIsFlashSale(23L))
            System.out.println("23 true");
    }
}
