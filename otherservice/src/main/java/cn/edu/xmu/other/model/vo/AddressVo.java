package cn.edu.xmu.other.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.edu.xmu.other.model.bo.Address;


@Data
@ApiModel(description = "地址视图对象")
public class AddressVo {

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String  detail;

    @ApiModelProperty(value = "联系人")
    private String  consignee;

    @ApiModelProperty(value = "联系方式")
    private String  mobile;

    public AddressVo(Address address)
    {
        this.regionId=address.getRegionId();
        this.consignee=address.getConsignee();
        this.detail=address.getDetail();
        this.mobile=address.getMobile();
    }
    public Address createAddress()
    {
        Address address= new Address();
        address.setConsignee(this.consignee);
        address.setDetail(this.detail);
        address.setMobile(this.mobile);
        address.setRegionId(this.regionId);
        return address;
    }
}
