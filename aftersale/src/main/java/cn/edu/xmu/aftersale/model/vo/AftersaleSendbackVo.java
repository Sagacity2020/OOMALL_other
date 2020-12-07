package cn.edu.xmu.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AftersaleSendbackVo {

    @ApiModelProperty(value = "买家运单号")
    private String customerLogSn;
}
