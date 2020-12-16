package cn.edu.xmu.customer.model.vo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public class NewCustomerVo {
    @Pattern(regexp="[+]?[0-9*#]+",message="手机号格式不正确")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Email(message = "email格式不正确")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    //@Length(min=6,message = "用户名长度过短")
    @NotBlank(message = "用户名不能为空")
    private String userName;

    //@Pattern(regexp = "^(?=.*?[AZ])(?=(.*[az]){1,})(?=(.*[\\\\d]){1,})(?=(.*[\\\\W]){1,})(?!.*\\\\s).{8,}$",message = "密码格式不正确，请包含大小写字母数字及特殊符号")
    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @NotNull(message = "姓名不能为空")
    private String realName;

    @NotNull(message = "性别不能为空")
    private Byte gender;

    @NotNull(message = "生日不能为空")
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
