package cn.edu.xmu.address.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "接受地区信息")
public class RegionVo {
    private String name;
    private Long postalCode;
}
