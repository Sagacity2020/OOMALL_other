package cn.edu.xmu.address.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.address.model.po.AddressPo;
import cn.edu.xmu.address.model.vo.AddressRetVo;
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
    public AddressRetVo createVo() {
        return new AddressRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
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

    public Address() {
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
