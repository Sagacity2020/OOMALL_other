package cn.edu.xmu.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsertRefundDTO implements Serializable {
    Long aftersaleId;

    Long orderItemId;

    Long amount;
}

