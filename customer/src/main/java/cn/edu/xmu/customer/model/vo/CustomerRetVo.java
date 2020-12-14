package cn.edu.xmu.customer.model.vo;

import cn.edu.xmu.customer.model.bo.Customer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "用户视图对象")
public class CustomerRetVo {
    private Long id;
    private String userName;
    private String name;
    private String mobile;
    private String email;
    private String gender;
    private LocalDate birthday;
    private Byte state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public CustomerRetVo(Customer bo) {
        id=bo.getId();
        userName=bo.getUserName();
        name=bo.getRealName();
        mobile=bo.getMobile();
        email=bo.getEmail();
        gender=bo.getGender().toString();
        state=bo.getState();
        birthday=bo.getBirthday();
        gmtCreate=bo.getGmtCreate();
        gmtModified=bo.getGmtModified();
    }
}


