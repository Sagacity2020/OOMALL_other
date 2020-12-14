package cn.edu.xmu.customer.model.vo;

import cn.edu.xmu.customer.model.bo.SimpleCustomer;
import lombok.Data;

@Data
public class CustomerSimpleRetVo {
    private Long id;
    private String userName;
    private String name;

    public CustomerSimpleRetVo(SimpleCustomer bo){
        id=bo.getId();
        userName=bo.getUserName();
        name=bo.getName();
    }
}
