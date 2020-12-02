package cn.edu.xmu.other.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AftersaleDeliverVo {

    @ApiModelProperty(value = "店家运单号")
    private String shopLogSn;
}
