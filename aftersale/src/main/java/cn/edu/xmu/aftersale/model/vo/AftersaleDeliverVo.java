package cn.edu.xmu.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AftersaleDeliverVo {

    @NotBlank(message = "运单号不能为空")
    @ApiModelProperty(value = "店家运单号")
    private String shopLogSn;
}
