package cn.edu.xmu.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AftersaleSendbackVo {

    @NotBlank(message = "运单号不能为空")
    @ApiModelProperty(value = "买家运单号")
    private String logSn;
}
