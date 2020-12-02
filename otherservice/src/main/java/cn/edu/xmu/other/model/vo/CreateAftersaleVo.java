package cn.edu.xmu.other.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CreateAftersaleVo {

    @ApiModelProperty(value = "售后类型")
    private Byte type;

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

    @ApiModelProperty(value = "商品价格")
    private Long refund;

    @ApiModelProperty(value = "顾客Id")
    private Long customerId;

    @ApiModelProperty(value = "商店Id")
    private Long shopId;

    @ApiModelProperty(value = "订单明细Id")
    private Long orderItemId;
}
