package cn.edu.xmu.advertisement.model.vo;

import cn.edu.xmu.advertisement.model.bo.Advertisement;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@ApiModel
public class AdvertisementCreateVo {
    @ApiModelProperty(value = "广告内容")
    private String content;

    @ApiModelProperty(value = "权重")
    private Integer weight;

    @ApiModelProperty(value = "开始日期")
    private LocalDate beginDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    @ApiModelProperty(value = "是否重复")
    private Byte repeats;

    @ApiModelProperty(value = "链接")
    private String link;

    public Advertisement createAdvertisement(){
        Advertisement advertisement=new Advertisement();

        advertisement.setContent(content);
        advertisement.setWeight(weight);
        advertisement.setBeginDate(beginDate);
        advertisement.setEndDate(endDate);
        advertisement.setRepeats(repeats);
        advertisement.setLink(link);

        return advertisement;
    }

}
