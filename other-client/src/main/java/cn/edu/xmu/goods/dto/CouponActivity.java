package cn.edu.xmu.goods.dto;

import lombok.Data;

import javax.naming.directory.SearchResult;
import java.io.Serializable;

@Data
public class CouponActivity implements Serializable {

    private Long id;
    private String name;
    private String beginTime;
    private String endTIme;

}
