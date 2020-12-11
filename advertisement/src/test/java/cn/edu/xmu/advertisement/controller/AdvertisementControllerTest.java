package cn.edu.xmu.advertisement.controller;

import cn.edu.xmu.advertisement.AdvertisementApplication;
import cn.edu.xmu.advertisement.model.vo.AdvertisementCreateVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import org.json.JSONException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = AdvertisementApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AdvertisementControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(AdvertisementControllerTest.class);

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    @Test
    public void advertisementCreateTest()throws Exception{
        AdvertisementCreateVo vo=new AdvertisementCreateVo();
        vo.setContent("test");
        vo.setLink(null);
        vo.setWeight(null);
        vo.setBeginDate(LocalDate.of(2020,12,8));
        vo.setEndDate(LocalDate.of(2020,12,10));
        vo.setRepeats((byte)0);

        String token = creatTestToken(1L, 0L, 100);
        String advertisementJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/advertise/timesegments/1/advertisement").header("authorization", token).contentType("application/json;charset=UTF-8").content(advertisementJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"data\":{\"id\":170,\"link\":null,\"imagePath\":null,\"content\":\"test\",\"segId\":1,\"state\":0,\"weight\":null,\"beDefault\":null,\"repeat\":0},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void advertisementInsertTest()throws Exception {

        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;

        try {
            responseString = this.mvc.perform(post("/advertise/timesegments/1/advertisement/121").header("authorization", token))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedResponse = "{\"errno\":0,\"data\":{\"id\":121,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1475991949547324589.jpg\",\"content\":null,\"segId\":1,\"state\":4,\"weight\":null,\"beDefault\":1,\"beginDate\":\"2020-12-15\",\"endDate\":\"2021-10-10\",\"repeat\":1},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void selectAdvertisementTest()throws Exception{
        String token=creatTestToken(1L,0L,100);
        String responseString=null;
        String expectedString="";

        try{
            responseString=this.mvc.perform(get("/advertise/timesegments/2/advertisement?page=1&pageSize=2").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        expectedString="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1,\"list\":[{\"id\":122,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1476498482597274807.jpg\",\"content\":null,\"segId\":2,\"state\":4,\"weight\":null,\"beDefault\":null,\"beginDate\":\"2020-12-15\",\"endDate\":\"2021-10-10\",\"repeat\":1}]},\"errmsg\":\"成功\"}";
        try{
            JSONAssert.assertEquals(expectedString, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void uploadFileTest() throws Exception{
        String token = creatTestToken(1L,0L,100);

        File file = new File("."+File.separator + "src" + File.separator + "test" + File.separator+"resources" + File.separator + "img" + File.separator+"timg.png");
        MockMultipartFile firstFile = new MockMultipartFile("img", "timg.png" , "multipart/form-data", new FileInputStream(file));

        String responseString = mvc.perform(MockMvcRequestBuilders
                .multipart("/advertise/advertisement/123/uploadImg")
                .file(firstFile)
                .header("authorization", token)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
}
