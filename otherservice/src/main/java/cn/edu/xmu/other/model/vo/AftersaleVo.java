package cn.edu.xmu.other.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "售后信息视图对象")
public class AftersaleVo {

    @ApiModelProperty(value = "商品数量")
    private Integer quantity;

    @ApiModelProperty(value = "申请原因")
    private String reason;

    @ApiModelProperty(value = "地区Id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String detail;

    @ApiModelProperty(value = "联系人")
    private String consignee;

    @ApiModelProperty(value = "手机号码")
    private String mobile;
}
