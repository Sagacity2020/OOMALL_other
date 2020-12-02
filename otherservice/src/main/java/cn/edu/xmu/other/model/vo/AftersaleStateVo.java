package cn.edu.xmu.other.model.vo;

import cn.edu.xmu.other.model.bo.Aftersale;
import lombok.Data;

@Data
public class AftersaleStateVo {
    private Long Code;

    private String name;
    public AftersaleStateVo(Aftersale.State state){
        Code=Long.valueOf(state.getCode());
        name=state.getDescription();
    }
}
