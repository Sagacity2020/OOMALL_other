package cn.edu.xmu.other.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.OtherServiceApplication;

import cn.edu.xmu.other.model.vo.AddressVo;
import cn.edu.xmu.other.model.vo.NewAddressVo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = OtherServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class AddressControllerTest {

    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime)
    {
        String token = new JwtHelper().createToken(userId,departId,expireTime);
        log.debug(token);
        return token;

    }
    @Test
    public void createAddress()throws Exception{
        AddressVo vo=new AddressVo();
        vo.setRegionId(1L);
        vo.setConsignee("a");
        vo.setDetail("abc");
        vo.setMobile("1234567");
        String requireJson= JacksonUtil.toJson(vo);

        String responseString=null;
        String token=creatTestToken(1L,0L,100);
        String expectedResponse = "";
        try {
            responseString = this.mvc.perform(post("address").header("authorization", token).contentType("application/json;charset=UTF-8").content(requireJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse=requireJson;
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
