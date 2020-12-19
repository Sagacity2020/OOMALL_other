package cn.edu.xmu.other.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonDTO implements Serializable {
    Boolean result;

    Integer errno;
}
