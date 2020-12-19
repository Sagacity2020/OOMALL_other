package cn.edu.xmu.customer.dao;

import cn.edu.xmu.customer.model.vo.ModifyPwdVo;
import cn.edu.xmu.customer.model.vo.ResetPwdVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.RandomCaptcha;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.customer.mapper.CustomerPoMapper;
import cn.edu.xmu.customer.model.bo.Customer;
import cn.edu.xmu.customer.model.bo.SimpleCustomer;
import cn.edu.xmu.customer.model.po.CustomerPo;
import cn.edu.xmu.customer.model.po.CustomerPoExample;
import cn.edu.xmu.customer.model.vo.CustomerVo;
import cn.edu.xmu.customer.model.vo.NewCustomerVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 ** @author 向姝可
 **/
@Repository
public class CustomerDao {

    @Autowired
    private CustomerPoMapper customerPoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

   /* @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;*/

    private static final Logger logger = LoggerFactory.getLogger(CustomerDao.class);

    public ReturnObject<VoObject> createNewCustomer(NewCustomerVo vo){
        CustomerPo customer=new CustomerPo();
        //customer.setUserName(AES.encrypt(vo.getUserName(),Customer.AESPASS));
        //customer.setRealName(AES.encrypt(vo.getRealName(),Customer.AESPASS));
        customer.setRealName(vo.getRealName());
        customer.setUserName(vo.getUserName());
        if(isEmailExist(vo.getEmail())){
            logger.debug("email exists: email"+vo.getEmail());
            return new ReturnObject(ResponseCode.EMAIL_REGISTERED);
        }
        if(isUserNameExist(customer.getUserName())){
            logger.debug("userName exists: userName"+vo.getUserName());
            return new ReturnObject(ResponseCode.USER_NAME_REGISTERED);
        }
        if(isMobileExist(vo.getMobile())){
            logger.debug("mobile exists: mobile"+vo.getMobile());
            return new ReturnObject(ResponseCode.MOBILE_REGISTERED);
        }

        int state=Customer.State.NORM.getCode();
        customer.setState((byte)state);
        customer.setPoint(0);
        customer.setBeDeleted((byte) 0);
        customer.setGender(vo.getGender());

        //DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //LocalDate birthdayLDT = LocalDate.parse(vo.getBirthday(),df);
        customer.setBirthday(vo.getBirthday());
        customer.setEmail(vo.getEmail());
        customer.setMobile(vo.getMobile());
        customer.setPassword(vo.getPassword());
        customer.setGmtCreate(LocalDateTime.now());

        ReturnObject<VoObject> retObj = null;
        try{
            int ret = customerPoMapper.insertSelective(customer);
            if (ret == 0) {
                //插入失败

                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败" ));
            } else {
                //插入成功
                Customer bo=new Customer(customer);
                retObj = new ReturnObject<>(bo);
            }
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    public ReturnObject<Customer> getCustomerById(Long userId){
        logger.info("id"+userId);
        CustomerPo customer=new CustomerPo();
        ReturnObject retObj=null;
        try{
            customer=customerPoMapper.selectByPrimaryKey(userId);
        }catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));

        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        if(customer==null){
            logger.info("getCustomerInfo failed:customerId is not Exist");
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("用户id不存在"));
        }
        else{
            Customer bo=new Customer(customer);
            retObj=new ReturnObject<>(bo);
        }
        return retObj;
    }

    public boolean isEmailExist(String email){
        logger.debug("is checking email in user table");
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        criteria.andEmailEqualTo(email);
        List<CustomerPo> userPos=customerPoMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    public boolean isMobileExist(String mobile){
        logger.debug("is checking mobile in user table");
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<CustomerPo> userPos=customerPoMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    public boolean isUserNameExist(String userName){
        logger.debug("is checking mobile in user table");
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<CustomerPo> userPos=customerPoMapper.selectByExample(example);
        return !userPos.isEmpty();
    }

    public CustomerPo createCustomerStateModifyPo(Long id,Customer.State state){
        CustomerPo po=customerPoMapper.selectByPrimaryKey(id);
        if(po==null||po.getBeDeleted().intValue()==1) {return null;}
        CustomerVo customerVo=new CustomerVo();
        Customer customer=new Customer(po);
        customer.setState(state);
        return customer.createUpdatePo(customerVo);
    }

    public ReturnObject getCustomerByName(String userName){
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<CustomerPo> customerPos = null;
        try{
            customerPos=customerPoMapper.selectByExample(example);
        }catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        if(customerPos==null||customerPos.isEmpty()){
            return new ReturnObject<>();
        }
        else{
            Customer customer=new Customer(customerPos.get(0));
            return new ReturnObject<>(customer);
        }
    }
    public ReturnObject<Object> changeCustomerState(Long id, Customer.State state){
        CustomerPo po=createCustomerStateModifyPo(id,state);

        if (po == null) {
            logger.info("用户不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = customerPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                logger.info("用户不存在或已被删除：id = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("用户 id = " + id + " 的状态修改为 " + state.getDescription());
                retObj = new ReturnObject<>();
            }
        } catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        return retObj;
    }

    public ReturnObject updateCustomerInfo(Long id,CustomerVo vo){
        ReturnObject<Object> retObj;
        CustomerPo customerPo = null;
        try {
            customerPo = customerPoMapper.selectByPrimaryKey(id);
        }catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        if(customerPo==null){
            logger.info("用户不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Customer bo=new Customer(customerPo);
        CustomerPo po=bo.createUpdatePo(vo);
        try {
           int ret=customerPoMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                //插入失败
                logger.debug("updateCustomerInfo: update customer fail ");
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("修改失败" ));
            } else {
                //插入成功
                logger.debug("updateCustomerInfo: update customer = " + vo.toString());
                //bo.setId(customer.getId());
                retObj = new ReturnObject<>(ResponseCode.OK);
            }
        }catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }

        return retObj;
    }

    public ReturnObject<PageInfo<VoObject>> getCustomerAll(String userName, String email, String mobile, Integer page, Integer pageSize){
        PageHelper.startPage(page, pageSize);
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        if(!userName.isBlank()){
            criteria.andUserNameEqualTo(userName);}
        if(!email.isBlank()){
            criteria.andEmailEqualTo(email);}
        if(!mobile.isBlank()){
            criteria.andMobileEqualTo(mobile);}
        List<CustomerPo> customers;
        try{
        customers=customerPoMapper.selectByExample(example);
        }catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
       // List<VoObject> customerBos=new ArrayList<>();
//        if(customers.isEmpty()){
//            logger.error("无符合条件用户" );
////            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID,
////                    String.format("无符合条件用户"));
//
//        }else {
            PageInfo<CustomerPo> customerPos=new PageInfo<>(customers);
           // logger.debug(pagesize.toString());
        List<VoObject> customerBos=customerPos.getList().stream().map(SimpleCustomer::new).collect(Collectors.toList());
        //}
        PageInfo<VoObject> returnObject=new PageInfo<>(customerBos);
        returnObject.setPages(customerPos.getPages());
        returnObject.setPageNum(customerPos.getPageNum());
        returnObject.setPageSize(customerPos.getPageSize());
        returnObject.setTotal(customerPos.getTotal());
        return new ReturnObject<>(returnObject);
    }
    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip){
        if(redisTemplate.hasKey("ip_"+ip)){
            return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN,String.format("1分钟内不能重复请求"));}
        else {
            //1 min中内不能重复请求
            redisTemplate.opsForValue().set("ip_"+ip,ip);
            redisTemplate.expire("ip_" + ip, 60*1000, TimeUnit.MILLISECONDS);
        }
        CustomerPoExample customerPoExample= new CustomerPoExample();
        CustomerPoExample.Criteria criteria = customerPoExample.createCriteria();
        criteria.andUserNameEqualTo(vo.getUserName());
        List<CustomerPo> customerPos = null;
        try {
            customerPos = customerPoMapper.selectByExample(customerPoExample);
        } catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        if(customerPos.isEmpty()){
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT,String.format("用户名不存在"));
        }
        else if(!customerPos.get(0).getEmail().equals(vo.getEmail())){
            return new ReturnObject<>(ResponseCode.EMAIL_WRONG);
        }

        //随机生成验证码
        String captcha = RandomCaptcha.getRandomString(6);
        while(redisTemplate.hasKey(captcha)){
            captcha = RandomCaptcha.getRandomString(6);}

        String id = customerPos.get(0).getId().toString();
        String key = "cp_" + captcha;
        //key:验证码,value:id存入redis
        redisTemplate.opsForValue().set(key,id);
        //五分钟后过期
        redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);

        //        //发送邮件(请在配置文件application.properties填写密钥)
       SimpleMailMessage msg = new SimpleMailMessage();
        msg.setSubject("【oomall】密码重置通知");
        msg.setSentDate(new Date());
       msg.setText("您的验证码是：" + captcha + "，5分钟内有效。如非本人操作，请忽略！谢谢");
        msg.setFrom("1309339909@qq.com");
        msg.setTo(vo.getEmail());
        try {
           mailSender.send(msg);
        } catch (MailException e) {
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    public ReturnObject<Object> modifyPassword(ModifyPwdVo modifyPwdVo){
        if(!redisTemplate.hasKey("cp_"+modifyPwdVo.getCaptcha())){
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT,String.format("验证码不正确或已失效"));}
        String id= redisTemplate.opsForValue().get("cp_"+modifyPwdVo.getCaptcha()).toString();
        CustomerPo customerPo=null;
        try{
            customerPo=customerPoMapper.selectByPrimaryKey(Long.parseLong(id));
        }catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        if(modifyPwdVo.getNewPassword().equals(customerPo.getPassword())){
            return new ReturnObject<>(ResponseCode.PASSWORD_SAME);
        }
        CustomerPo po=new CustomerPo();
        po.setId(customerPo.getId());
        po.setPassword(modifyPwdVo.getNewPassword());
        try{
            customerPoMapper.updateByPrimaryKeySelective(po);
        }catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    public Boolean getPointFromRefund(Long userId,Integer point){
        CustomerPo po=null;
         try{
             po=customerPoMapper.selectByPrimaryKey(userId);
         }catch (DataAccessException e) {
             // 数据库错误
             logger.error("数据库错误：" + e.getMessage());
             return false;
         } catch (Exception e) {
             // 属未知错误
             logger.error("严重错误：" + e.getMessage());
             return false;
         }
         if(po==null) {
             logger.debug("用户不存在");
             return false;
         }else{
             Customer bo=new Customer(po);
             CustomerPo newPo=bo.createUpdatePointPo(bo.getPoint()+point);
             try{
                 int ret=customerPoMapper.updateByPrimaryKeySelective(newPo);
                 if(ret==0) {
                     logger.debug("更新返点失败");
                     return false;}
             }catch (DataAccessException e) {
                 // 数据库错误
                 logger.error("数据库错误：" + e.getMessage());
                 return false;
             } catch (Exception e) {
                 // 属未知错误
                 logger.error("严重错误：" + e.getMessage());
                 return false;
             }
         }
         return true;
    }

    public Boolean minusPoint(Long userId,Integer point){
        CustomerPo po=null;
        try{
            po=customerPoMapper.selectByPrimaryKey(userId);
        }catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return false;
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return false;
        }
        if(po==null) {
            logger.debug("用户不存在");
            return false;
        }else{
            if(po.getPoint()<point){
                logger.debug("返点不足");
                return false;
            }
            Customer bo=new Customer(po);
            CustomerPo newPo=bo.createUpdatePointPo(bo.getPoint()-point);
            try{
                int ret=customerPoMapper.updateByPrimaryKeySelective(newPo);
                if(ret==0) {
                    logger.debug("更新返点失败");
                    return false;}
            }catch (DataAccessException e) {
                // 数据库错误
                logger.error("数据库错误：" + e.getMessage());
                return false;
            } catch (Exception e) {
                // 属未知错误
                logger.error("严重错误：" + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
