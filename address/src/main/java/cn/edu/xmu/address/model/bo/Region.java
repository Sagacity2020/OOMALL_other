package cn.edu.xmu.address.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.address.model.po.RegionPo;
import cn.edu.xmu.address.model.vo.RegionRetVo;
import cn.edu.xmu.address.model.vo.RegionVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zrh
 * @Created at 12/8 8:08
 */
@Data
public class Region implements VoObject {
    private Long id;

    private Long pid;

    private String  name;

    private Long postalCode;

    private Byte state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;


    @Override
    public RegionRetVo createVo() {
        return new RegionRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public Region(RegionPo po)
    {
        this.id=po.getId();
        this.pid=po.getPid();
        this.name=po.getName();
        this.postalCode=po.getPostalCode();
        this.state=po.getState();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }
    public Region(RegionVo vo)
    {
        this.name=vo.getName();
        this.postalCode=vo.getPostalCode();
    }

    public Region() {
    }
    public RegionPo getRegionPo(){
        RegionPo po=new RegionPo();
        po.setId(this.getId());
        po.setName(this.getName());
        po.setPostalCode(this.getPostalCode());
        po.setState(this.getState());
        po.setPid(this.getId());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }
}
