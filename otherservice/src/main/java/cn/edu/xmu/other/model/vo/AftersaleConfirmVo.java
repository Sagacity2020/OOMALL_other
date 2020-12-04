package cn.edu.xmu.other.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AftersaleConfirmVo {

    Boolean confrim;

    @ApiModelProperty(value = "处理意见")
    String conclusion;
}
