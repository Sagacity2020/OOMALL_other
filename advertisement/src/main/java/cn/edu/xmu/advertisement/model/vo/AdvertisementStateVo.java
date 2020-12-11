package cn.edu.xmu.advertisement.model.vo;

import cn.edu.xmu.advertisement.model.bo.Advertisement;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdvertisementStateVo {
    private Long code;

    private String name;

    public AdvertisementStateVo(Advertisement.State state){
        code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
