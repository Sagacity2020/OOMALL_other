package cn.edu.xmu.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {
    private Long orderId;
    private Integer quantity;
    private Integer price;
    private Long beShareId;
}
