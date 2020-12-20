package cn.edu.xmu.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleDTO implements Serializable {

    String consignee;

    Long shopId;

    Long regionId;

    String address;

    Integer quantity;

    String mobile;

    String message;

    Long orderItemId;

    Long customerId;

}
