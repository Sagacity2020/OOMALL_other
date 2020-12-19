package cn.edu.xmu.customer.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(description = "买家用户信息视图对象")
public class CustomerVo {
    private String realName;
    //private String mobile;
    //private String email;

    private Byte gender;


    private LocalDate birthday;

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getRealName() {
        return realName;
    }

    public Byte getGender() {
        return gender;
    }
}
