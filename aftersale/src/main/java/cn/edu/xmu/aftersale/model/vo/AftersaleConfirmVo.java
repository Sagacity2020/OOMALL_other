package cn.edu.xmu.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AftersaleConfirmVo {

    @NotNull(message = "处理操作不能为空")
    private Boolean confirm;

    private Integer price;

    private Integer type;

    @NotBlank(message = "处理意见不能为空")
    @ApiModelProperty(value = "处理意见")
    private String conclusion;
}
