package cn.edu.xmu.other.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.other.model.po.AddressPo;
import cn.edu.xmu.other.model.vo.AddressPageVo;

import java.time.LocalDateTime;

public class AddressPage implements VoObject {
    private Long id;
    private Long   customer_id;
    private Long  regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private byte  be_default;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public Long getCustomer_id() {
        return customer_id;
    }

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

    public byte getBe_default() {
        return be_default;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public AddressPage(AddressPo po) {
        this.id=po.getId();
        this.customer_id=po.getCustomerId();
        this.regionId=po.getRegionId();
        this.detail=po.getDetail();
        this.consignee=po.getConsignee();
        this.mobile=po.getMobile();
        this.be_default=po.getBeDefault();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new AddressPageVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
