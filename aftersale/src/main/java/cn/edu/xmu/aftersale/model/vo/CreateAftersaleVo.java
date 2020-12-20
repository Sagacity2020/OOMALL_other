package cn.edu.xmu.aftersale.model.vo;

import cn.edu.xmu.aftersale.model.bo.Aftersale;
import io.lettuce.core.StrAlgoArgs;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@ApiModel
public class CreateAftersaleVo {

    @NotNull(message = "售后类型不能为空")
    @ApiModelProperty(value = "售后类型")
    private Byte type;

    @NotNull(message = "数量不能为空")
    @ApiModelProperty(value = "商品数量")
    private Integer quantity;

    @ApiModelProperty(value = "申请原因")
    private String reason;

    @NotNull(message = "地区id不能为空")
    @ApiModelProperty(value = "地区Id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String detail;

    @NotBlank(message = "联系人不能为空")
    @ApiModelProperty(value = "联系人")
    private String consignee;

    @Pattern(regexp="^1\\d{10}$",message="手机号格式不正确")
    @NotBlank(message = "手机号码不能为空")
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
