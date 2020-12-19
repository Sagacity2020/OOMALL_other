package cn.edu.xmu.goods.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Created at 12/11 8:50
 * @author zrh
 */
@Data
public class GoodsSkuInfo implements Serializable {

    private Long price;
    private String skuName;


    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }


}
