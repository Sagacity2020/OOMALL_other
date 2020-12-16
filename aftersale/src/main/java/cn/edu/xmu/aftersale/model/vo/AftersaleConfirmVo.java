package cn.edu.xmu.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AftersaleConfirmVo {

    private Boolean confrim;

    private Integer price;

    private Integer type;

    @ApiModelProperty(value = "处理意见")
    private String conclusion;
}
