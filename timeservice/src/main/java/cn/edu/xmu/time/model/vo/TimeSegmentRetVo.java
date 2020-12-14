package cn.edu.xmu.time.model.vo;


import cn.edu.xmu.time.model.bo.TimeSegment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeSegmentRetVo {
    @ApiModelProperty(value="时间段id")
    private Long id;

    @ApiModelProperty(value="开始时间")
    private LocalDateTime beginTime;

    @ApiModelProperty(value="结束时间")
    private LocalDateTime endTime;

//    @ApiModelProperty(value="时间段类型")
//    private Byte type;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    public TimeSegmentRetVo(TimeSegment timeSegment)
    {
        this.id= timeSegment.getId();
        this.beginTime=timeSegment.getBeginTime();
        this.endTime=timeSegment.getEndTime();
        //this.type=timeSegment.getType();
        this.gmtCreate=timeSegment.getGmtCreate();
        this.gmtModified=timeSegment.getGmtModified();
    }

}
