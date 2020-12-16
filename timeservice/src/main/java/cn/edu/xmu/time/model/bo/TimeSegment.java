package cn.edu.xmu.time.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.time.model.po.TimeSegmentPo;
import cn.edu.xmu.time.model.vo.TimeSegmentRetVo;
import cn.edu.xmu.time.model.vo.TimeSegmentSimpleRetVo;
import cn.edu.xmu.time.model.vo.TimeSegmentVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class TimeSegment implements VoObject {
    private Long id;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Byte type;
    //private Long creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public TimeSegment(){

    }

    //构造Po
    public TimeSegment(TimeSegmentPo po)
    {
        this.id=po.getId();
        this.beginTime=po.getBeginTime();
        this.endTime=po.getEndTime();
        this.type=po.getType();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    //生成Vo对象 返回前端
    @Override
    public Object createVo(){return new TimeSegmentRetVo(this);
    }
    ////生成SimpleVo对象 返回前端
    @Override
    public Object createSimpleVo(){
        return new TimeSegmentSimpleRetVo(this);
    }

    /**
     * 用vo对象创建更新po对象
     * @author zwl
     * @param vo vo对象
     * @return po对象
     */
    public TimeSegmentPo createUpdatePo(TimeSegmentVo vo)
    {
        TimeSegmentPo po=new TimeSegmentPo();
        po.setId(this.getId());
        po.setBeginTime(LocalDateTime.parse(vo.getBeginTime()));
        po.setEndTime(LocalDateTime.parse(vo.getEndTime()));
        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());
        return po;
    }

    /**
     * 用bo对象创建更新po对象
     *
     * @author zwl
     * @return TimeSegmentPo
     *
     */
    public TimeSegmentPo gotTimeSegmentPo()
    {
        TimeSegmentPo po=new TimeSegmentPo();
        po.setId(this.getId());
        po.setBeginTime(this.getBeginTime());
        po.setEndTime(this.getEndTime());
        po.setType(this.getType());
        po.setGmtCreate(this.getGmtCreate());
        po.setGmtModified(this.getGmtModified());
        return po;
    }
}
