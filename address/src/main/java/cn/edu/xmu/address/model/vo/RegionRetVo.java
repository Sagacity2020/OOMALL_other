package cn.edu.xmu.address.model.vo;

import cn.edu.xmu.address.model.bo.Region;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "返回地区信息")
public class RegionRetVo {
    private Long id;

    private Long pid;

    private String  name;

    private Long postalCode;

    private Byte state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public RegionRetVo(Region bo)
    {
        this.id=bo.getId();
        this.pid=bo.getPid();
        this.name=bo.getName();
        this.postalCode=bo.getPostalCode();
        this.state=bo.getState();
        this.gmtCreate=bo.getGmtCreate();
        this.gmtModified=bo.getGmtModified();
    }
}
