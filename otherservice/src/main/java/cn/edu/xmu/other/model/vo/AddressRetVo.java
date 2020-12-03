package cn.edu.xmu.other.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.bo.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;

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

    private byte  be_default;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public AddressRetVo(Address bo) {
        this.id=bo.getId();
        this.be_default=bo.getBe_default();
        this.consignee=bo.getConsignee();
        this.detail=bo.getDetail();
        this.regionId=bo.getRegionId();
        this.mobile=bo.getMobile();
        this.gmtCreate=bo.getGmtCreate();
        this.gmtModified=bo.getGmtModified();
    }
}
