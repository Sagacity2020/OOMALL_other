package cn.edu.xmu.advertisement.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class AuditAdVo {

    @ApiModelProperty(value = "提交")
    private boolean conclusion;

    @ApiModelProperty(value = "审核附言")
    private String message;
}
