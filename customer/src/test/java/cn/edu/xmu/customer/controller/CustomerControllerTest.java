package cn.edu.xmu.customer.controller;

import cn.edu.xmu.customer.model.vo.ModifyPwdVo;
import cn.edu.xmu.customer.model.vo.ResetPwdVo;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.customer.model.vo.LoginVo;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback(false)
public class CustomerControllerTest {
    @Autowired
    private MockMvc mvc;
    private static final Logger logger = LoggerFactory.getLogger(CustomerControllerTest.class);
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    /*
    获取所有状态
     */

@Test
    public void getAllStateTest()throws Exception{

        String responseString=this.mvc.perform(get("/user/users/states"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{ \"errno\": 0, \"data\": [ { \"name\": \"后台\", \"code\": 0 }, { \"name\": \"正常\", \"code\": 4 }, { \"name\": \"封禁\", \"code\": 6 } ], \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }
/*
注册新用户（正常）
 */
    @Test
    public void reigsterTest1()throws Exception{
      String requireJson="{\n" +
              "  \"mobile\": \"13950004260\",\n" +
              "  \"email\": \"1309339909@qq.com\",\n" +
              "  \"userName\": \"xskxsk\",\n" +
              "  \"password\": \"000105\",\n" +
              "  \"realName\": \"xsk\",\n" +
              "  \"gender\": 0,\n" +
              "  \"birthday\": \"2000-01-05\"\n" +
              "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{" +
                "\"errno\": 0," +
                "\"data\": {" +
                //"\"id\": 17363," +
                "\"userName\": \"xskxsk\"," +
                "\"name\": \"xsk\"," +
                "\"mobile\": \"13950004260\"," +
                "\"email\": \"1309339909@qq.com\"," +
                "\"gender\": 0," +
                "\"birthday\": \"2000-01-05\"," +
                "\"state\": 4,"+
                //"\"gmtCreate\": \"2020-12-04T14:20:24\"," +
                "\"gmtModified\": null" +
                "}," +
                "\"errmsg\": \"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /*
    注册新用户（用户名已注册）
     */
    @Test
    public void registerTest2()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13955554260\",\n" +
                "  \"email\": \"1309339990@qq.com\",\n" +
                "  \"userName\": \"xskxsk\",\n" +
                "  \"password\": \"000105\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.USER_NAME_REGISTERED.getCode()))
                .andExpect(jsonPath("$.errmsg").value("用户名已被注册"))
                .andReturn().getResponse().getContentAsString();
    }
    /*
        注册新用户（电话已注册）
         */
    @Test
    public void registerTest4()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13950004260\",\n" +
                "  \"email\": \"1309339990@qq.com\",\n" +
                "  \"userName\": \"xskxkk\",\n" +
                "  \"password\": \"000105\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.MOBILE_REGISTERED.getCode()))
                .andExpect(jsonPath("$.errmsg").value("电话已被注册"))
                .andReturn().getResponse().getContentAsString();
    }
    /*
    注册新用户（邮箱已被注册）
     */
    @Test
    public void registerTest3()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13955554260\",\n" +
                "  \"email\": \"1309339909@qq.com\",\n" +
                "  \"userName\": \"xskxhk\",\n" +
                "  \"password\": \"0105Xsk;\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.EMAIL_REGISTERED.getCode()))
                .andExpect(jsonPath("$.errmsg").value("邮箱已被注册"))
                .andReturn().getResponse().getContentAsString();
    }
    /*
        注册新用户（用户名为空）
         */
    @Test
    public void registerTest5()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13955554260\",\n" +
                "  \"email\": \"xiangshuke@qq.com\",\n" +
                "  \"userName\": \"\",\n" +
                "  \"password\": \"0105Xsk..\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"用户名不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
    /*
        注册新用户（邮箱格式不正确）
         */
    @Test
    public void registerTest6()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13950004260\",\n" +
                "  \"email\": \"xiangshukeqq.com\",\n" +
                "  \"userName\": \"xkxsk\",\n" +
                "  \"password\": \"0105Xsk..\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"email格式不正确;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
    /*
        注册新用户（邮箱为空）
         */
    @Test
    public void registerTest7()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13950004260\",\n" +
                "  \"email\": \"\",\n" +
                "  \"userName\": \"xkxsk\",\n" +
                "  \"password\": \"0105Xsk..\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"邮箱不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
    /*
    注册新用户（手机号格式不正确）
     */
    @Test
    public void registerTest8()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"1abc394260\",\n" +
                "  \"email\": \"1308839909@qq.com\",\n" +
                "  \"userName\": \"xskKsk\",\n" +
                "  \"password\": \"0105Xsk..\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"手机号格式不正确;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
    /*
    注册新用户（电话为空）
*/
    @Test
    public void registerTest9()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"\",\n" +
                "  \"email\": \"1309339909@qq.com\",\n" +
                "  \"userName\": \"xskxsk\",\n" +
                "  \"password\": \"0105xsk\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"手机号格式不正确;手机号不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /*
       注册新用户（密码为空）
   */
    @Test
    public void registerTest11()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13950004266\",\n" +
                "  \"email\": \"1309339909@qq.com\",\n" +
                "  \"userName\": \"xskxsk\",\n" +
                "  \"password\": \"\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"密码不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /*
       注册新用户（性别为空）
   */
    @Test
    public void registerTest12()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13950004266\",\n" +
                "  \"email\": \"1309339909@qq.com\",\n" +
                "  \"userName\": \"xskxsk\",\n" +
                "  \"password\": \"123456\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": \"\",\n" +
                "  \"birthday\": \"2000-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"性别不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /*
      注册新用户（生日为空）
  */
    @Test
    public void registerTest13()throws Exception{
        String requireJson="{\n" +
                "  \"mobile\": \"13950004266\",\n" +
                "  \"email\": \"1309339909@qq.com\",\n" +
                "  \"userName\": \"xskxsk\",\n" +
                "  \"password\": \"123456\",\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 1,\n" +
                "  \"birthday\": \"\"\n" +
                "}";
        String responseString=this.mvc.perform(post("/user/users")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"生日不能为空;\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /*
    平台管理员封禁买家(正常）
*/
     @Test
    public void banCustomerTest1()throws Exception{
         String token = creatTestToken(1L, 0L, 100);
         String responseString=this.mvc.perform(put("/user/shops/0/users/1/ban").header("authorization", token))
                         .andExpect(status().isOk())
                         .andExpect(content().contentType("application/json;charset=UTF-8"))
                         .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                         .andExpect(jsonPath("$.errmsg").value("成功"))
                         .andReturn().getResponse().getContentAsString();
     }

     /*
     平台管理员封禁买家(id不存在）
      */
    @Test
     public void banCustomerTest2()throws Exception{
         String token = creatTestToken(1L, 0L, 100);
         String responseString=this.mvc.perform(put("/user/shops/0/users/0/ban").header("authorization", token))
                 .andExpect(status().isNotFound())
                 .andExpect(content().contentType("application/json;charset=UTF-8"))
                 .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
                 .andExpect(jsonPath("$.errmsg").value("操作的资源id不存在"))
                 .andReturn().getResponse().getContentAsString();
     }

    /*
    平台管理员封禁买家(用户已被逻辑删除）
    */
//    @Test
//    public void banCustomerTest3()throws Exception{
//        String token = creatTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(put("/user/shops/0/users/1/ban").header("authorization", token))
//                .andExpect(status().isNotFound())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
//                .andExpect(jsonPath("$.errmsg").value("操作的资源id不存在"))
//                .andReturn().getResponse().getContentAsString();
//    }

    /*
   平台管理员封禁买家(无权限）
*/
    @Test
    public void banCustomerTest4()throws Exception{
        String token = creatTestToken(1L, -2L, 100);
        String responseString=this.mvc.perform(put("/user/shops/0/users/2/ban").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_NOT_ALLOW.getCode()))
                .andExpect(jsonPath("$.errmsg").value("没有权限"))
                .andReturn().getResponse().getContentAsString();
    }
    /*
   平台管理员解禁买家(正常）
*/
    @Test
    public void releaseCustomerTest1()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(put("/user/shops/0/users/1/release").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 平台管理员解禁买家(用户id不存在）
     * @throws Exception
     */
    @Test
    public void releaseCustomerTest2()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(put("/user/shops/0/users/0/release").header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
                .andExpect(jsonPath("$.errmsg").value("操作的资源id不存在"))
                .andReturn().getResponse().getContentAsString();
    }
    /**
     * 平台管理员解禁买家(用户已被逻辑删除）
     * @throws Exception
     */
//    @Test
//    public void releaseCustomerTest3()throws Exception{
//        String token = creatTestToken(1L, 0L, 100);
//        String responseString=this.mvc.perform(put("/user/shops/0/users/1/release").header("authorization", token))
//                .andExpect(status().isNotFound())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
//                .andExpect(jsonPath("$.errmsg").value("操作的资源id不存在"))
//                .andReturn().getResponse().getContentAsString();
//    }
    /**
     * 登陆（正常）
     * @throws Exception
     */
    @Test
    public void loginTest()throws Exception{
        LoginVo loginVo=new LoginVo();
        loginVo.setUserName("xskxsk");
        loginVo.setPassword("000105");
        String requireJson = JacksonUtil.toJson(loginVo);
        String responseString=this.mvc.perform(post("/user/users/login")
                         .contentType("application/json;charset=UTF-8")
                         .content(requireJson)).andExpect(status().isOk())
                         .andExpect(status().isOk())
                         .andExpect(content().contentType("application/json;charset=UTF-8"))
                         .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
    /**
     * 登陆（密码不正确）
     * @throws Exception
     */
    @Test
    public void loginTest2()throws Exception{
        LoginVo loginVo=new LoginVo();
        loginVo.setUserName("xskxsk");
        loginVo.setPassword("123456");
        String requireJson = JacksonUtil.toJson(loginVo);
        String responseString=this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_INVALID_ACCOUNT.getCode()))
                .andExpect(jsonPath("$.errmsg").value("用户名不存在或者密码错误"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 登陆（用户被封禁）
     * @throws Exception
     */
    @Test
    public void loginTest3()throws Exception{
        LoginVo loginVo=new LoginVo();
        loginVo.setUserName("xskxsk");
        loginVo.setPassword("000105");
        String requireJson = JacksonUtil.toJson(loginVo);
        String responseString=this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_USER_FORBIDDEN.getCode()))
                .andExpect(jsonPath("$.errmsg").value("用户名被禁止登录"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 登陆（用户被逻辑删除） 需要插入bedeleted为1的用户数据
     * @throws Exception
     */
    @Test
    public void loginTest4()throws Exception{
        LoginVo loginVo=new LoginVo();
        loginVo.setUserName("xsk");
        loginVo.setPassword("000105");
        String requireJson = JacksonUtil.toJson(loginVo);
        String responseString=this.mvc.perform(post("/user/users/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(ResponseCode.AUTH_ID_NOTEXIST.getCode()))
                .andExpect(jsonPath("$.errmsg").value("登陆用户id不存在"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /*
    获取用户信息（正常）
     */
    @Test
    public void getUserSelfTest()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(get("/user/users").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"id\":1,\"userName\":\"59460469111\",\"name\":\"49741965112\",\"mobile\":\"13959288888\",\"email\":\"1309339909@qq.com\",\"gender\":0,\"birthday\":\"2020-12-24\",\"state\":1,\"gmtCreate\":\"2020-12-06T22:49:24\",\"gmtModified\":\"2020-12-06T22:49:24\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /*
    获取用户信息（用户id不存在）
     */
    @Test
    public void getUserSelfTest1()throws Exception{
        String token = creatTestToken(-1L, 0L, 100);
        String responseString=this.mvc.perform(get("/user/users").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_NOTEXIST.getCode()))
                .andExpect(jsonPath("$.errmsg").value("用户id不存在"))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /*
    管理员获取用户信息（正常）
     */
    @Test
    public void getCustomerByIdTest()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String responseString=this.mvc.perform(get("/user/users/1").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"data\":{\"id\":1,\"userName\":\"59460469111\",\"name\":\"49741965112\",\"mobile\":\"13959288888\",\"email\":\"1309339909@qq.com\",\"gender\":\"0\",\"birthday\":\"2020-12-24\",\"state\":1,\"gmtCreate\":\"2020-12-06T22:49:24\",\"gmtModified\":\"2020-12-06T22:49:24\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
/*
   管理员获取用户信息（没有权限）
 */
@Test
public void getCustomerByIdTest1()throws Exception{
    String token = creatTestToken(1L, -2L, 100);
    String responseString=this.mvc.perform(get("/user/users/1").header("authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.errno").value(ResponseCode.FIELD_NOTVALID.getCode()))
            .andExpect(jsonPath("$.errmsg").value("没有权限"))
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andReturn().getResponse().getContentAsString();
}

/*
用户更新信息（正常）
 */
    @Test
    public void updateCustomerInfo()throws Exception{
        String token = creatTestToken(1L, -2L, 100);
        String requireJson="{\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 1,\n" +
                "  \"birthday\": \"2020-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(put("/user/users").header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        String responseString1=this.mvc.perform(get("/user/users").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse1="{\"errno\":0,\"data\":{\"id\":1,\"name\":\"xsk\",\"gender\":1,\"birthday\":\"2020-01-05\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1,responseString1,false);
    }

    /*
用户更新信息（不改姓名）
 */
    @Test
    public void updateCustomerInfo1()throws Exception{
        String token = creatTestToken(1L, -2L, 100);
        String requireJson="{\n" +
                "  \"realName\": \"\",\n" +
                "  \"gender\": 0,\n" +
                "  \"birthday\": \"2020-02-05\"\n" +
                "}";
        String responseString=this.mvc.perform(put("/user/users").header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        String responseString1=this.mvc.perform(get("/user/users").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse1="{\"errno\":0,\"data\":{\"id\":1,\"name\":\"xsk\",\"gender\":0,\"birthday\":\"2020-02-05\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1,responseString1,false);
    }
    /*
用户更新信息（不改性别）
 */
    @Test
    public void updateCustomerInfo2()throws Exception{
        String token = creatTestToken(1L, -2L, 100);
        String requireJson="{\n" +
                "  \"realName\": \"xskx\",\n" +
                "  \"gender\": \"\",\n" +
                "  \"birthday\": \"2020-01-05\"\n" +
                "}";
        String responseString=this.mvc.perform(put("/user/users").header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        String responseString1=this.mvc.perform(get("/user/users").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse1="{\"errno\":0,\"data\":{\"id\":1,\"name\":\"xskx\",\"gender\":0,\"birthday\":\"2020-01-05\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1,responseString1,false);
    }
    /*
用户更新信息（不改生日）
 */
    @Test
    public void updateCustomerInfo3()throws Exception{
        String token = creatTestToken(1L, -2L, 100);
        String requireJson="{\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 1,\n" +
                "  \"birthday\": \"\"\n" +
                "}";
        String responseString=this.mvc.perform(put("/user/users").header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
        String responseString1=this.mvc.perform(get("/user/users").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse1="{\"errno\":0,\"data\":{\"id\":1,\"name\":\"xsk\",\"gender\":1,\"birthday\":\"2020-01-05\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1,responseString1,false);
    }

    /*
用户更新信息（生日日期格式错误）
*/
    @Test
    public void updateCustomerInfo4()throws Exception{
        String token = creatTestToken(1L, -2L, 100);
        String requireJson="{\n" +
                "  \"realName\": \"xsk\",\n" +
                "  \"gender\": 1,\n" +
                "  \"birthday\": \"2020-10-50\"\n" +
                "}";
        this.mvc.perform(put("/user/users").header("authorization", token)
                .contentType("application/json;charset=UTF-8")
                .content(requireJson))
                .andExpect(status().isBadRequest());

    }

    /*
    平台管理员获取用户列表
     */
    @Test
    public void getCustomerAllTest()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse=new String(Files.readAllBytes(Paths.get("src/test/java/resources/findAllCustomerSuccess.json")));
        String responseString = this.mvc.perform(get("/user/users/all?userName=&email=&mobile=&page=1&pageSize=2").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /*
    平台管理员获取用户列表（没查到）
     */
    @Test
    public void getCustomerAllTest1()throws Exception{
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse=new String(Files.readAllBytes(Paths.get("src/test/java/resources/findAllCustomerSuccess.json")));
        String responseString = this.mvc.perform(get("/user/users/all?userName=xskx&email=&mobile=&page=1&pageSize=").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
//    @Test
//    public void resetPwdTest()throws Exception{
//        ResetPwdVo vo=new ResetPwdVo();
//        vo.setUserName("jxljxljxl");
//        vo.setEmail("835736795@qq.com");
//        String requireJson = JacksonUtil.toJson(vo);
//        String responseString=this.mvc.perform(put("/user/users/password/reset")
//                .contentType("application/json;charset=UTF-8")
//                .content(requireJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,false);
//    }
//
//    @Test
//    public void modifyPwdTest()throws Exception{
//        ModifyPwdVo vo=new ModifyPwdVo();
//        vo.setCaptcha("jn7MuC");
//        vo.setNewPassword("jxl1234!");
//        String requireJson = JacksonUtil.toJson(vo);
//        String responseString=this.mvc.perform(put("/user/users/password")
//                .contentType("application/json;charset=UTF-8")
//                .content(requireJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,false);
//    }

//    @Test
//    public void logoutTest()throws Exception{
//        String token="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjEyMjEwMjE4NzJWIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjotMiwiZXhwIjoxNjA3NzgxNzM4LCJ1c2VySWQiOjEsImlhdCI6MTYwNzc3ODEzOH0.JlX_1MAYVsjHrkRYX-62jijPAK-2V4Rf0nfQAR_uP_o";
//       String responseString = this.mvc.perform(get("/user/users/logout").header("authorization", token))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString();
//        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse,responseString,false);
//    }
}
