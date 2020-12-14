package cn.edu.xmu.customer.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.customer.model.po.CustomerPo;
import cn.edu.xmu.customer.model.vo.CustomerSimpleRetVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class SimpleCustomer implements VoObject, Serializable {
    private Long id;
    private String userName;
    private String name;

    public SimpleCustomer(CustomerPo po){
        id=po.getId();
        userName=po.getUserName();
        name=po.getRealName();
    }

    @Override
    public CustomerSimpleRetVo createVo() {
        return new CustomerSimpleRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }
}
