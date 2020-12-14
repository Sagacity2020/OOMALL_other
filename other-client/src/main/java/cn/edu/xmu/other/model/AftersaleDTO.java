package cn.edu.xmu.other.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AftersaleDTO implements Serializable {
    private Long shopId;
    private Long customerId;
}
