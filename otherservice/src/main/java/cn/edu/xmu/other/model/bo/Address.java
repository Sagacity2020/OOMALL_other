package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.AddressPo;
import cn.edu.xmu.other.model.vo.AddressVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address implements VoObject {
    private Integer id;
    private Long   customer_id;
    private Long  regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private byte  be_default;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Long getRegionId() {
        return regionId;
    }

    public String getDetail() {
        return detail;
    }

    public String getConsignee() {
        return consignee;
    }

    public String getMobile() {
        return mobile;
    }
    @Override
    public AddressVo createVo() {
        return new AddressVo(this);
    }

    public AddressPo getAddressPo(){
        AddressPo po=new AddressPo();
        po.setCustomerId(this.customer_id);
        po.setConsignee(this.consignee);
        po.setDetail(this.detail);
        po.setMobile(this.mobile);
        po.setRegionId(this.regionId);
        po.setBeDefault(this.be_default);
        po.setGmtCreated(this.gmtCreate);
        po.setGmtModified(this.gmtModified);
        return po;
    }
}
