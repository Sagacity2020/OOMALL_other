package cn.edu.xmu.customer.model.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class NewCustomerVo {
    @Pattern(regexp="[+]?[0-9*#]+",message="手机号格式不正确")
    private String mobile;

    @Email(message = "email格式不正确")
    private String email;

    @Length(min=6,message = "用户名长度过短")
    private String userName;

    //@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "密码格式不正确，请包含大小写字母数字及特殊符号")
    private String password;

    private String realName;
    private Byte gender;
    private LocalDate birthday;


    public Byte getGender() {
        //byte gen = Byte.parseByte(gender);
        return gender;
    }

    public String getRealName() {
        return realName;
    }

    public String getUserName() {
        return userName;
    }

    public String getMobile() {
        return mobile;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
