package cn.edu.xmu.cart.controller;


import cn.edu.xmu.cart.CartApplication;
import cn.edu.xmu.ooad.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.jar.JarOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CartApplication.class)
@AutoConfigureMockMvc
@Transactional
@Slf4j
public class CartControllerTest {

    private static final Logger logger= LoggerFactory.getLogger(CartControllerTest.class);

    @Autowired
    private MockMvc mvc;

    private final String creatTestToken(Long userId, Long departId, int expireTime)
    {
        String token = new JwtHelper().createToken(userId,departId,expireTime);
        log.debug(token);
        return token;

    }

    /**
     * @Created at 12/11 18:14
     * @author zrh
     * 删除所有购物车 成功
     * @throws Exception
     */
    @Test
    public void deleteAllCart()throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responceString="";
        try{
            responceString=this.mvc.perform(delete("/cart/carts").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJson=new JSONObject();
        expectedJson.put("errno",0);
        expectedJson.put("errmsg","成功");
        String expectedString=expectedJson.toString();
        try{
            JSONAssert.assertEquals(expectedString,responceString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/11 18:14
     * @author zrh
     * 清空购物车 购物车为空
     * @throws Exception
     */
    @Test
    public void deleteAllCart1()throws Exception{
        String token=creatTestToken(3L,-2L,100);
        String responceString="";
        try{
            responceString=this.mvc.perform(delete("/cart/carts").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJson=new JSONObject();
        expectedJson.put("errno",504);
        expectedJson.put("errmsg","购物车为空");
        String expectedString=expectedJson.toString();
        try{
            JSONAssert.assertEquals(expectedString,responceString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/11 18:15
     * @author zrh
     * 删除购物车某个商品 成功
     * @throws Exception
     */
    @Test
    public void deleteCart()throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responceString="";
        try{
            responceString=this.mvc.perform(delete("/cart/carts/1").header("authorization",token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJson=new JSONObject();
        expectedJson.put("errno",0);
        expectedJson.put("errmsg","成功");
        String expectedString=expectedJson.toString();
        try{
            JSONAssert.assertEquals(expectedString,responceString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/11 18:16
     * @author zrh
     * 用户不拥有该购物车商品
     * @throws Exception
     */
    @Test
    public void deleteCart1()throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responceString="";
        try{
            responceString=this.mvc.perform(delete("/cart/carts/3").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJson=new JSONObject();
        expectedJson.put("errno",505);
        expectedJson.put("errmsg","购物车商品不属于该用户");
        String expectedString=expectedJson.toString();
        try{
            JSONAssert.assertEquals(expectedString,responceString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * @Created at 12/11 18:16
     * @author zrh
     * 购物车无该商品
     * @throws Exception
     */
    @Test
    public void deleteCart2()throws Exception{
        String token=creatTestToken(1L,-2L,100);
        String responceString="";
        try{
            responceString=this.mvc.perform(delete("/cart/carts/4").header("authorization",token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();

        }catch (Exception e){
            e.printStackTrace();
        }
        JSONObject expectedJson=new JSONObject();
        expectedJson.put("errno",504);
        expectedJson.put("errmsg","购物车无该商品");
        String expectedString=expectedJson.toString();
        try{
            JSONAssert.assertEquals(expectedString,responceString,true);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
