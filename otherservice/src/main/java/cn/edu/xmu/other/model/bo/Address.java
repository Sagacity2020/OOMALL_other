package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.AddressPo;
import cn.edu.xmu.other.model.vo.AddressVo;
import cn.edu.xmu.other.model.vo.NewAddressVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Address implements VoObject {
    private Long id;
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
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModified);
        return po;
    }
    public NewAddressVo retAddressPo(){
        NewAddressVo po=new NewAddressVo();
        po.setRegionId(this.regionId);
        po.setDetail(this.detail);
        po.setConsignee(this.consignee);
        po.setMobile(this.mobile);
        po.setBe_default(this.be_default);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModified);
        return po;
    }

    public Address(AddressPo po) {
        this.id = po.getId();
        this.customer_id = po.getCustomerId();
        this.regionId = po.getRegionId();
        this.detail = po.getDetail();
        this.consignee = po.getConsignee();
        this.mobile = po.getMobile();
        this.be_default = po.getBeDefault();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
    }
}
