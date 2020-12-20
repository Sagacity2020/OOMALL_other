package cn.edu.xmu.advertisement.model.vo;

import cn.edu.xmu.advertisement.model.bo.Advertisement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@ApiModel
public class AdvertisementUpdateVo {
    @ApiModelProperty(value = "广告内容")
    @NotNull
    private String content;

    @ApiModelProperty(value = "权重")
    @NotNull
    private Integer weight;

    @Pattern(regexp ="^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$"
            ,message = "开始日期不正确")
    @NotNull
    @ApiModelProperty(value = "开始日期")
    private String beginDate;

    @Pattern(regexp ="^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$"
            ,message = "结束日期格式不正确")
    @NotNull
    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "链接")
    @NotNull
    private String link;

    @ApiModelProperty(value = "每日重复")
    @NotNull
    private Boolean repeat;

    public Advertisement createAdvertisement(){
        Advertisement advertisement=new Advertisement();

        advertisement.setContent(content);
        advertisement.setWeight(weight);
        advertisement.setLink(link);
        if(repeat!=null&&repeat==true)
        {advertisement.setRepeats((byte)1);}
        //else{advertisement.setRepeats((byte)0);}

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(beginDate!=null)
            advertisement.setBeginDate(LocalDate.parse(beginDate, fmt));
        if(endDate!=null)
            advertisement.setEndDate(LocalDate.parse(endDate, fmt));
        return advertisement;
    }
}
