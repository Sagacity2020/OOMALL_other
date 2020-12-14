package cn.edu.xmu.other.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSegmentDTO implements Serializable {
    private Long segId;

    private LocalTime beginTime;

    private LocalTime endTime;
}
