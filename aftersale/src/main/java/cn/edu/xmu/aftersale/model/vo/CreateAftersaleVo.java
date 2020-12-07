package cn.edu.xmu.aftersale.model.vo;

import cn.edu.xmu.aftersale.model.bo.Aftersale;
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

    public Aftersale createAftersale(){
        Aftersale aftersale=new Aftersale();

        aftersale.setType(type);
        aftersale.setQuantity(quantity);
        aftersale.setReason(reason);
        aftersale.setRegionId(regionId);
        aftersale.setDetail(detail);
        aftersale.setConsignee(consignee);
        aftersale.setMobile(mobile);

        return aftersale;
    }
}
