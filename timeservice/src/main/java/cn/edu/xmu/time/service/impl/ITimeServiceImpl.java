package cn.edu.xmu.time.service.impl;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.other.model.TimeSegmentDTO;
import cn.edu.xmu.other.service.ITimeService;
import cn.edu.xmu.time.dao.TimeSegmentDao;
import cn.edu.xmu.time.model.po.TimeSegmentPo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.LocalTime;

@DubboService(version = "0.0.1-SNAPSHOT")
public class ITimeServiceImpl implements ITimeService {

    @Autowired
    TimeSegmentDao timeSegmentDao;

    @Override
    public ReturnObject<TimeSegmentDTO> getTimesegmentById(Long id) {
        ReturnObject returnObject= timeSegmentDao.getTimesegmentById(id);
        TimeSegmentPo po=(TimeSegmentPo)returnObject.getData();

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
}
