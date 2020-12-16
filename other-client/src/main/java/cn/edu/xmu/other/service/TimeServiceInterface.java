package cn.edu.xmu.other.service;


import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dto.TimeSegmentDTO;

import java.util.List;

/**
 * 时段服务调用接口
 * @author wwc
 * @date 2020/11/26 10:20
 * @version 1.0
 */
public interface TimeServiceInterface {

    public ReturnObject<TimeSegmentDTO> getTimesegmentById(Long id);
    List<Long> getCurrentFlashSaleTimeSegs();
    Boolean timeSegIsFlashSale(Long id);
}
