package cn.edu.xmu.address.model.vo;

import cn.edu.xmu.address.model.bo.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "新建返回地址对象")
public class AddressRetVo{
    private Long id;
    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String  detail;

    @ApiModelProperty(value = "联系人")
    private String  consignee;

    @ApiModelProperty(value = "联系方式")
    private String  mobile;

    private Boolean beDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public AddressRetVo(Address bo) {
        this.id=bo.getId();
        if(bo.getBe_default()==1){
            this.beDefault=true;
        }
        else {
            this.beDefault=false;
        }
        this.consignee=bo.getConsignee();
        this.detail=bo.getDetail();
        this.regionId=bo.getRegionId();
        this.mobile=bo.getMobile();
        this.gmtCreate=bo.getGmtCreate();
        this.gmtModified=bo.getGmtModified();
    }
}
