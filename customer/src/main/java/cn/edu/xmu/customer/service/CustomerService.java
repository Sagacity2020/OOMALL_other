package cn.edu.xmu.customer.service;

import cn.edu.xmu.customer.model.vo.CustomerVo;
import cn.edu.xmu.customer.model.vo.ModifyPwdVo;
import cn.edu.xmu.customer.model.vo.NewCustomerVo;
import cn.edu.xmu.customer.model.vo.ResetPwdVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.customer.dao.CustomerDao;
import cn.edu.xmu.customer.model.bo.Customer;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 ** @author 向姝可
 **/
@Service
public class CustomerService {
    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Value("${privilegeservice.login.jwtExpire}")
    private Integer jwtExpireTime;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Transactional
    public ReturnObject<VoObject> register(NewCustomerVo vo)
    {
        return customerDao.createNewCustomer(vo);
    }

    @Transactional
    public ReturnObject updateCustomerInfo(Long userId, CustomerVo vo){
        return customerDao.updateCustomerInfo(userId,vo);
}
    @Transactional
    public ReturnObject getCustomerById(Long userId){
        return customerDao.getCustomerById(userId);
    }

    @Transactional
    public ReturnObject<String> login(String userName,String password){
        ReturnObject retObj = customerDao.getCustomerByName(userName);
        if (retObj.getCode() != ResponseCode.OK){
            return retObj;
        }
        Customer customer=(Customer) retObj.getData();
        if(customer == null || !password.equals(customer.getPassword())){
            retObj = new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
            return retObj;
        }
        int state=Customer.State.NORM.getCode();
        if(customer.getState()!=(byte)state){
            retObj = new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
            return retObj;
        }
        JwtHelper jwtHelper = new JwtHelper();
        Long did=(long)-2;
        String jwt = jwtHelper.createToken(customer.getId(),-2L, jwtExpireTime);
        logger.debug("login: Jwt = "+ jwt);
        /*if(redisTemplate.hasKey("up_"+customer.getId())){
            logger.info("不可重复登录");
            retObj=new ReturnObject<>();
        }else{
        redisTemplate.opsForSet().add("up_"+customer.getId(), jwt);
        retObj = new ReturnObject<>(jwt);}*/
        redisTemplate.opsForSet().add("up_"+customer.getId(), jwt);
        retObj = new ReturnObject<>(jwt);
        return retObj;
    }

  @Transactional
    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip){
        return customerDao.resetPassword(vo,ip);
    }

   @Transactional
    public ReturnObject<Object> modifyPassword(ModifyPwdVo vo){
        return customerDao.modifyPassword(vo);
    }

    @Transactional
    public ReturnObject<Boolean> logout(Long userId,String jwt){
        try{
        redisTemplate.opsForSet().remove("up_" + userId,jwt);}
        catch(Exception e){
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_JWT,String.format("token不合法或已失效"));
        }
        return new ReturnObject<>(true);
    }
   @Transactional
    public ReturnObject<Object> banCustomer(Long id){
        return customerDao.changeCustomerState(id,Customer.State.FORBID);
    }

@Transactional
    public ReturnObject<Object> releaseCustomer(Long id){
        return customerDao.changeCustomerState(id,Customer.State.NORM);
    }

    @Transactional
    public ReturnObject<PageInfo<VoObject>> getCustomerAll(String userName, String email, String mobile, Integer page, Integer pagesize){
        return customerDao.getCustomerAll(userName,email,mobile,page,pagesize);
    }
}
