package cn.edu.xmu.customer.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.customer.model.po.CustomerPo;
import cn.edu.xmu.customer.model.vo.CustomerRetVo;
import cn.edu.xmu.customer.model.vo.CustomerSimpleRetVo;
import cn.edu.xmu.customer.model.vo.CustomerVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Customer implements VoObject, Serializable {
    public void setState(State state) {
        this.state=state;
    }

    public void setId(Long id) {
        this.id=id;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getRealName() {
        return realName;
    }

    public Byte getGender() {
        return gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public Byte getState() {
        int state=this.state.getCode();
        return (byte)state;
    }

    //public static String AESPASS = "OOAD2020-11-01";

    public String getPassword() {
        return password;
    }

    public Integer getPoint() {
        return point;
    }

    public enum State {
        NEW(0, "后台"),
        NORM(4, "正常"),
        FORBID(6, "封禁");

        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Integer code) {
            State st=stateMap.get(code);
            return st;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;
    private String userName;
    private String password;
    private String realName;
    private Byte gender;
    private LocalDate birthday;
    private Integer point;
    private String email;
    private String mobile;
    private Byte beDeleted;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private State state;

    public Customer(){

    }
    public Customer(CustomerPo po){
        this.id=po.getId();
        this.userName=po.getUserName();
        this.password=po.getPassword();
        this.realName=po.getRealName();
        this.gender=po.getGender();
        this.birthday=po.getBirthday();
        this.point=po.getPoint();
        this.email=po.getEmail();
        this.mobile=po.getMobile();
        this.beDeleted=po.getBeDeleted();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
        if (null != po.getState()) {
            this.state = State.getTypeByCode(po.getState().intValue());
        }

    }
    public CustomerPo createUpdatePo(CustomerVo vo){
        //String nameEnc = vo.getName() == null ? null : AES.encrypt(vo.getName(), User.AESPASS);
        //String mobEnc = vo.getMobile() == null ? null : AES.encrypt(vo.getMobile(), User.AESPASS);
        //String emlEnc = vo.getEmail() == null ? null : AES.encrypt(vo.getEmail(), User.AESPASS);

       CustomerPo customerPo=new CustomerPo();
       Byte state=(byte)this.state.code;

       customerPo.setId(id);
       customerPo.setBirthday(vo.getBirthday());
       customerPo.setRealName(vo.getRealName());
       customerPo.setGender(vo.getGender());
       customerPo.setState(state);
       customerPo.setGmtModified(LocalDateTime.now());
       return customerPo;
    }

    public CustomerPo createUpdatePointPo(Integer point){
        CustomerPo customerPo=new CustomerPo();
        Byte state=(byte)this.state.code;

        customerPo.setId(id);
        customerPo.setPoint(point);
        customerPo.setGmtModified(LocalDateTime.now());
        return customerPo;
    }










































/*
bo->vo返回前端
 */
    @Override
    public CustomerRetVo createVo() {
        return new CustomerRetVo(this);
        /*retVo.setId(id);
        retVo.setUserName(userName);
        retVo.setRealName(realName);
        retVo.setMobile(mobile);
        retVo.setGender(gender);
        retVo.setBirthday(birthday);
        retVo.setGmtCreate(gmtCreate);
        retVo.setGmtModified(gmtModified);*/
    }

    @Override
    public CustomerSimpleRetVo createSimpleVo() {
        return null;
    }
}
