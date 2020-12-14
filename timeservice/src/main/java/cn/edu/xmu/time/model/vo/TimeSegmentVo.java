package cn.edu.xmu.time.model.vo;


import cn.edu.xmu.time.model.bo.TimeSegment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class TimeSegmentVo {
    @Pattern(regexp ="^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"
            ,message = "开始时间格式不正确")
    @ApiModelProperty(value="开始时间")
    private String beginTime;

    @Pattern(regexp ="^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$"
            ,message = "结束时间格式不正确")
    @ApiModelProperty(value="结束时间")

    private String endTime;
    
    public TimeSegment creatTimeSegment(){
        TimeSegment timeSegment=new TimeSegment();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timeSegment.setBeginTime(LocalDateTime.parse(this.beginTime,formatter));
        timeSegment.setEndTime(LocalDateTime.parse(this.endTime,formatter));
        //timeSegment.setType(this.type);
        return timeSegment;
    }
}
