package cn.edu.xmu.address.model.vo;

import cn.edu.xmu.address.model.bo.AddressPage;

import java.time.LocalDateTime;

public class AddressPageVo{
    private Long id;
    private Long regionId;
    private String detail;
    private String consignee;
    private  String mobile;
    private byte be_default;
    private LocalDateTime gmtCreate;
    private Long state;

    public AddressPageVo(AddressPage bo) {
        this.id=bo.getId();
        this.regionId=bo.getRegionId();
        this.detail=bo.getDetail();
        this.consignee=bo.getConsignee();
        this.be_default=bo.getBe_default();
        this.mobile=bo.getMobile();
        this.gmtCreate=bo.getGmtCreate();
        this.state=0L;

    }

}
