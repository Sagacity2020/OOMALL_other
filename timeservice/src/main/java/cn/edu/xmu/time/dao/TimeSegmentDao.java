package cn.edu.xmu.time.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.time.mapper.TimeSegmentPoMapper;
import cn.edu.xmu.time.model.bo.TimeSegment;
import cn.edu.xmu.time.model.po.TimeSegmentPo;
import cn.edu.xmu.time.model.po.TimeSegmentPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TimeSegmentDao {
    private static final Logger logger = LoggerFactory.getLogger(TimeSegmentDao.class);

    @Autowired
    private TimeSegmentPoMapper timeSegmentPoMapper;

    /**
     * 查看广告时间段
     * @author zwl
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return
     * @Date:  2020/12/6 21:50
     */

    public ReturnObject<PageInfo<VoObject>> selectAdTimeSegments(Integer pageNum, Integer pageSize)
    {
        TimeSegmentPoExample example = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo((byte)0);
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<TimeSegmentPo> timeSegmentPoList =null;
        try {
            timeSegmentPoList = timeSegmentPoMapper.selectByExample(example);
        }catch (DataAccessException e){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        //logger.info("getUserRoles: userId = "+ id + "roleNum = "+ userRolePoList.size());
        if(timeSegmentPoList.isEmpty())
        {logger.info("广告时间段列表为空");}

        List<VoObject> ret = new ArrayList<>(timeSegmentPoList.size());
        for (TimeSegmentPo po : timeSegmentPoList) {
            TimeSegment timeSegment = new TimeSegment(po);
            ret.add(timeSegment);
        }
        PageInfo<VoObject> timeSegmentPage = PageInfo.of(ret);
        return new ReturnObject<>(timeSegmentPage);
    }

    /**
     * 增加一个广告时间段
     * @author zwl
     * @param timeSegment 时间段bo
     * @return ReturnObject<TimeSegment> 新增结果
     */
    public ReturnObject<TimeSegment> insertAdTimeSegment(TimeSegment timeSegment)
    {
        ///判断开始时间 结束时间是不是空
        if(timeSegment.getBeginTime()==null)
        {return new ReturnObject<>(ResponseCode.Log_BEGIN_NULL);}
        if(timeSegment.getEndTime()==null)
        {return new ReturnObject<>(ResponseCode.Log_END_NULL);}

        //开始时间大于结束时间
        LocalTime beginTime = null;
        beginTime=LocalTime.of(timeSegment.getBeginTime().getHour(),timeSegment.getBeginTime().getMinute(),timeSegment.getBeginTime().getSecond());
        LocalTime endTime = null;
        endTime=LocalTime.of(timeSegment.getEndTime().getHour(),timeSegment.getEndTime().getMinute(),timeSegment.getEndTime().getSecond());
        if(beginTime.isAfter(endTime)||beginTime.equals(endTime))
        {return new ReturnObject<>(ResponseCode.Log_Bigger);}

        TimeSegmentPo timeSegmentPo=timeSegment.gotTimeSegmentPo();
        ReturnObject<TimeSegment> retObj = null;
        try{
            //判断时间段是否重复，冲突
            TimeSegmentPoExample example = new TimeSegmentPoExample();
            TimeSegmentPoExample.Criteria criteria = example.createCriteria();
            criteria.andTypeEqualTo((byte)0);
            List<TimeSegmentPo> timeSegmentPoList =null;
            timeSegmentPoList = timeSegmentPoMapper.selectByExample(example);

            LocalTime poBeginTime = null;
            LocalTime poEndTime = null;

            for (TimeSegmentPo po : timeSegmentPoList) {
                poBeginTime=LocalTime.of(po.getBeginTime().getHour(),po.getBeginTime().getMinute(),po.getBeginTime().getSecond());
                poEndTime=LocalTime.of(po.getEndTime().getHour(),po.getEndTime().getMinute(),po.getEndTime().getSecond());
                //时间段重复
                if(beginTime.equals(poBeginTime)&&endTime.equals(poEndTime))
                {return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT,String.format("时间段重复：" + po.getBeginTime()+"  "+po.getEndTime()));}

                //冲突
                if((beginTime.isAfter(poBeginTime)&&beginTime.isBefore(poEndTime))||(endTime.isAfter(poBeginTime)&& endTime.isBefore(poEndTime))||(beginTime.isBefore(poBeginTime)&& endTime.isAfter(poEndTime)))
                { return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT, String.format("时间段冲突：" + po.getBeginTime()+"  "+po.getEndTime()));}
            }

            int ret = timeSegmentPoMapper.insertSelective(timeSegmentPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertAdTimeSegment: insert timeSegment fail " + timeSegmentPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增时间段失败：" + timeSegmentPo.getBeginTime()+timeSegmentPo.getEndTime()));
            } else {
                //插入成功
                logger.debug("insertAdTimeSegment:" + timeSegmentPo.toString());
                timeSegment.setId(timeSegmentPo.getId());
                retObj = new ReturnObject<>(timeSegment);
            }
        }
        catch (DataAccessException e) {
//            if (Objects.requireNonNull(e.getMessage()).contains("time_segment.timeSegment_beginTime_endTime_uindex")) {
//                //若有重复的时间段则新增失败
//                logger.debug("updateTimeSegment: have same timeSegment = " + timeSegmentPo.getBeginTime()+timeSegmentPo.getEndTime());
//                retObj = new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT, String.format("时间段重复：" + timeSegmentPo.getBeginTime()+timeSegmentPo.getEndTime()));
//            } else {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     *  * 删除时间段
     * @author zwl
     *  @param id 时间段id
     *  @return ReturnObject<Object> 返回视图
     */
    @Transactional
    public ReturnObject<Object> deleteAdTimeSegment(Long id) {
        ReturnObject<Object> retObj = null;

        try {
            //System.out.println("删除时间段");
            logger.debug("deleteAdTimeSegment: " + id);
            int ret = timeSegmentPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                logger.debug("deleteAdTimeSegment: id not exist = " + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("时间段id不存在：" + id));
            }
            else{
                //iAdService.updateAdSegId(id);
                return new ReturnObject(ResponseCode.OK);
            }
        }catch (DataAccessException e){
            logger.debug("other sql exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

    }

    public ReturnObject<Object> getTimesegmentById(Long id){
        TimeSegmentPo po=new TimeSegmentPo();
        ReturnObject retObj;
        try{
            po=timeSegmentPoMapper.selectByPrimaryKey(id);
        } catch (DataAccessException e){
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }

        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return new ReturnObject<>(po);
    }

}
