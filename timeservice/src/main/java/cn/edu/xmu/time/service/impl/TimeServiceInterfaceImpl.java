package cn.edu.xmu.time.service.impl;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.dto.TimeSegmentDTO;
import cn.edu.xmu.other.service.TimeServiceInterface;
import cn.edu.xmu.time.dao.TimeSegmentDao;
import cn.edu.xmu.time.model.po.TimeSegmentPo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@DubboService(version = "0.0.1")
public class TimeServiceInterfaceImpl implements TimeServiceInterface {

    @Autowired
    TimeSegmentDao timeSegmentDao;

    @Override
    public ReturnObject<TimeSegmentDTO> getTimesegmentById(Long id) {
        ReturnObject returnObject= timeSegmentDao.getTimesegmentById(id);
        TimeSegmentPo po=(TimeSegmentPo)returnObject.getData();

        if(po==null){
            return returnObject;
        }

        TimeSegmentDTO timeSegmentDTO=new TimeSegmentDTO();

        timeSegmentDTO.setSegId(po.getId());
        LocalDateTime beginTime=po.getBeginTime();
        LocalDateTime endTime=po.getEndTime();

        LocalTime begin=LocalTime.of(beginTime.getHour(),beginTime.getMinute(),beginTime.getSecond());
        LocalTime end=LocalTime.of(endTime.getHour(),endTime.getMinute(),endTime.getSecond());

        timeSegmentDTO.setBeginTime(begin);
        timeSegmentDTO.setEndTime(end);

        return new ReturnObject<>(timeSegmentDTO);
    }

    @Override
    public List<Long> getCurrentFlashSaleTimeSegs(){
        return timeSegmentDao.getCurrentFlashSaleTimeSegs();
    }
    @Override
    public Boolean timeSegIsFlashSale(Long id){
        return timeSegmentDao.timeSegIsFlashSale(id);
    }

}
