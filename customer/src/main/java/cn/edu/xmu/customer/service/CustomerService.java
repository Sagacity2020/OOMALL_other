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
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    @Value("${privilegeservice.lockerExpireTime}")
    private long lockerExpireTime;

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
        redisTemplate.opsForSet().remove("up_" + userId,jwt);
        banJwt(jwt);
        }
        catch(Exception e){
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_JWT,String.format("token不合法或已失效"));
        }
        return new ReturnObject<>(true);
    }

    private void banJwt(String jwt){
        String[] banSetName = {"BanJwt_0", "BanJwt_1"};
        long bannIndex = 0;
        if (!redisTemplate.hasKey("banIndex")){
            redisTemplate.opsForValue().set("banIndex", Long.valueOf(0));
        } else {
            logger.debug("banJwt: banIndex = " +redisTemplate.opsForValue().get("banIndex"));
            bannIndex = Long.parseLong(redisTemplate.opsForValue().get("banIndex").toString());
        }
        logger.debug("banJwt: banIndex = " + bannIndex);
        String currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
        logger.debug("banJwt: currentSetName = " + currentSetName);
        if(!redisTemplate.hasKey(currentSetName)) {
            // 新建
            logger.debug("banJwt: create ban set" + currentSetName);
            redisTemplate.opsForSet().add(currentSetName, jwt);
            redisTemplate.expire(currentSetName,jwtExpireTime * 2, TimeUnit.SECONDS);
        }else{
            //准备向其中添加元素
            if(redisTemplate.getExpire(currentSetName, TimeUnit.SECONDS) > jwtExpireTime) {
                // 有效期还长，直接加入
                logger.debug("banJwt: add to exist ban set" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
            } else {
                // 有效期不够JWT的过期时间，准备用第二集合，让第一个集合自然过期
                // 分步式加锁
                logger.debug("banJwt: switch to next ban set" + currentSetName);
                long newBanIndex = bannIndex;
                while (newBanIndex == bannIndex &&
                        !redisTemplate.opsForValue().setIfAbsent("banIndexLocker","nouse", lockerExpireTime, TimeUnit.SECONDS)){
                    //如果BanIndex没被其他线程改变，且锁获取不到
                    try {
                        Thread.sleep(10);
                        //重新获得新的BanIndex
                        newBanIndex = (Long) redisTemplate.opsForValue().get("banIndex");
                    }catch (InterruptedException e){
                        logger.error("banJwt: 锁等待被打断");
                    }
                    catch (IllegalArgumentException e){

                    }
                }
                if (newBanIndex == bannIndex) {
                    //切换ban set
                    bannIndex = redisTemplate.opsForValue().increment("banIndex");
                }else{
                    //已经被其他线程改变
                    bannIndex = newBanIndex;
                }

                currentSetName = banSetName[(int) (bannIndex % banSetName.length)];
                //启用之前，不管有没有，先删除一下，应该是没有，保险起见
                redisTemplate.delete(currentSetName);
                logger.debug("banJwt: next ban set =" + currentSetName);
                redisTemplate.opsForSet().add(currentSetName, jwt);
                redisTemplate.expire(currentSetName,jwtExpireTime * 2,TimeUnit.SECONDS);
                // 解锁
                redisTemplate.delete("banIndexLocker");
            }
        }
    }

   @Transactional
    public ReturnObject<Object> banCustomer(Long id){
        if(redisTemplate.hasKey("up_"+id)){
            Set<Serializable> resultSet =redisTemplate.opsForSet().members("up_"+id);
            for(Serializable jwt:resultSet){
                banJwt(jwt.toString());
            }
            redisTemplate.delete("up_"+id);
        }
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
