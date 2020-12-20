package cn.edu.xmu.time.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;

import cn.edu.xmu.other.dto.FlashSaleTimeSegmentDTO;
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
        List<TimeSegmentPo> timeSegmentPoList =new ArrayList<>() ;
        try {
            timeSegmentPoList = timeSegmentPoMapper.selectByExample(example);
        }catch (DataAccessException e){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        //logger.info("getUserRoles: userId = "+ id + "roleNum = "+ userRolePoList.size());
        if(timeSegmentPoList.isEmpty())
        {logger.info("广告时间段列表为空");}

        List<VoObject> ret = new ArrayList<>(timeSegmentPoList.size());
        for (TimeSegmentPo po : timeSegmentPoList) {
            TimeSegment timeSegment = new TimeSegment(po);
            ret.add(timeSegment);
        }
        PageInfo<TimeSegmentPo> timeSegmentPoPage = PageInfo.of(timeSegmentPoList);
        PageInfo<VoObject> timeSegmentPage = PageInfo.of(ret);
        timeSegmentPage.setPageNum(timeSegmentPoPage.getPageNum());
        timeSegmentPage.setPageSize(timeSegmentPoPage.getPageSize());
        timeSegmentPage.setPages(timeSegmentPoPage.getPages());
        timeSegmentPage.setTotal(timeSegmentPoPage.getTotal());
        //最后一页没设置

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
            List<TimeSegmentPo> timeSegmentPoList = timeSegmentPoMapper.selectByExample(example);

            LocalTime poBeginTime = null;
            LocalTime poEndTime = null;

            for (TimeSegmentPo po : timeSegmentPoList) {
                poBeginTime=LocalTime.of(po.getBeginTime().getHour(),po.getBeginTime().getMinute(),po.getBeginTime().getSecond());
                poEndTime=LocalTime.of(po.getEndTime().getHour(),po.getEndTime().getMinute(),po.getEndTime().getSecond());
                //时间段重复
                if(beginTime.equals(poBeginTime)&&endTime.equals(poEndTime))
                {return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT,String.format("时间段重复：" + po.getBeginTime()+"  "+po.getEndTime()));}

                //冲突
                if(!((beginTime.isBefore(poBeginTime)&&(endTime.isBefore(poEndTime)||endTime.equals(poBeginTime)))||((beginTime.isAfter(poEndTime)||beginTime.equals(poEndTime))&& endTime.isAfter(poEndTime))))
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
            System.out.println("删除时间段");
            TimeSegmentPo po =timeSegmentPoMapper.selectByPrimaryKey(id);
            if(po==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("时间段id不存在：" + id));
            }
            if(!po.getType().equals((byte)0))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("该时间段不是广告时段"));
            }
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


    /**
     * 查看秒杀时间段
     * @author zwl
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return
     * @Date:  2020/12/6 21:50
     */

    public ReturnObject<PageInfo<VoObject>> selectFlTimeSegments(Integer pageNum, Integer pageSize)
    {
        TimeSegmentPoExample example = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo((byte)1);
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<TimeSegmentPo> timeSegmentPoList =new ArrayList<>() ;
        try {
            timeSegmentPoList = timeSegmentPoMapper.selectByExample(example);
        }catch (DataAccessException e){
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }

        if(timeSegmentPoList.isEmpty())
        {logger.info("秒杀时间段列表为空");}

        List<VoObject> ret = new ArrayList<>(timeSegmentPoList.size());
        for (TimeSegmentPo po : timeSegmentPoList) {
            TimeSegment timeSegment = new TimeSegment(po);
            ret.add(timeSegment);
        }
        PageInfo<VoObject> timeSegmentPage = PageInfo.of(ret);
        PageInfo<TimeSegmentPo> timeSegmentPoPage = PageInfo.of(timeSegmentPoList);
        timeSegmentPage.setPageNum(timeSegmentPoPage.getPageNum());
        timeSegmentPage.setPageSize(timeSegmentPoPage.getPageSize());
        timeSegmentPage.setPages(timeSegmentPoPage.getPages());
        timeSegmentPage.setTotal(timeSegmentPoPage.getTotal());
        return new ReturnObject<>(timeSegmentPage);
    }

    /**
     * 增加一个秒杀时间段
     * @author zwl
     * @param timeSegment 时间段bo
     * @return ReturnObject<TimeSegment> 新增结果
     */
    public ReturnObject<TimeSegment> insertFlTimeSegment(TimeSegment timeSegment)
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
            criteria.andTypeEqualTo((byte)1);
            List<TimeSegmentPo> timeSegmentPoList = timeSegmentPoMapper.selectByExample(example);

            LocalTime poBeginTime = null;
            LocalTime poEndTime = null;

            for (TimeSegmentPo po : timeSegmentPoList) {
                poBeginTime=LocalTime.of(po.getBeginTime().getHour(),po.getBeginTime().getMinute(),po.getBeginTime().getSecond());
                poEndTime=LocalTime.of(po.getEndTime().getHour(),po.getEndTime().getMinute(),po.getEndTime().getSecond());
                //时间段重复
                if(beginTime.equals(poBeginTime)&&endTime.equals(poEndTime))
                {return new ReturnObject<>(ResponseCode.TIMESEG_CONFLICT,String.format("时间段重复：" + po.getBeginTime()+"  "+po.getEndTime()));}

                //冲突
                //if((beginTime.isAfter(poBeginTime)&&beginTime.isBefore(poEndTime))||(endTime.isAfter(poBeginTime)&& endTime.isBefore(poEndTime))||(beginTime.isBefore(poBeginTime)&& endTime.isAfter(poEndTime)))
                if(!((beginTime.isBefore(poBeginTime)&&endTime.isBefore(poEndTime))||(beginTime.isAfter(poEndTime)&& endTime.isAfter(poEndTime))))
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
    public ReturnObject<Object> deleteFlTimeSegment(Long id) {
        ReturnObject<Object> retObj = null;
        System.out.println("删除时间段");
        try {
            TimeSegmentPo po =timeSegmentPoMapper.selectByPrimaryKey(id);
            if(po==null)
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("时间段id不存在：" + id));
            }
            if(!po.getType().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE,String.format("该时间段不是秒杀时段"));
            }
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

    /**
     * 获得当前秒杀时段ID
     * @author zwl
     * @param
     * @return 根据当前时间，返回所有已开始且未结束且类型是秒杀时段的时段ID列表
     * 若无则返回长度为0的列表而不是null
     * @Date:  2020/12/15 8:43
     */
    public ArrayList<Long> getCurrentFlashSaleTimeSegs(){
        ArrayList<Long> flashSaleTimeSegs= new ArrayList<>();
        TimeSegmentPoExample example = new TimeSegmentPoExample();
        TimeSegmentPoExample.Criteria criteria = example.createCriteria();
        criteria.andTypeEqualTo((byte)1);
        List<TimeSegmentPo> timeSegmentPoList =null;
        timeSegmentPoList = timeSegmentPoMapper.selectByExample(example);
        for (TimeSegmentPo po : timeSegmentPoList) {
            LocalTime beginTime = null;
            beginTime=LocalTime.of(po.getBeginTime().getHour(),po.getBeginTime().getMinute(),po.getBeginTime().getSecond());
            LocalTime endTime = null;
            endTime=LocalTime.of(po.getEndTime().getHour(),po.getEndTime().getMinute(),po.getEndTime().getSecond());
            if((LocalTime.now().isAfter(beginTime)||LocalTime.now().equals(beginTime))&&LocalTime.now().isBefore(endTime))
            {
                System.out.println("now"+po.getId());
                // flashSaleTimeSegs.add(Long.toString(po.getId()));
                flashSaleTimeSegs.add(po.getId());
            }
        }
        //if(flashSaleTimeSegs!=null)
        // String flashSaleTimeSegIds=String.join("+", flashSaleTimeSegs);
        return flashSaleTimeSegs;
    }
    /**
     * 根据时间段ID判断是否为秒杀时段
     * Boolean timeSegIsFlashSale(Long id);
     * 参数：时间段ID
     * 返回：ID不存在或时段类型不是秒杀则返回false，否则为true
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/15 9:19
     */
    public Boolean timeSegIsFlashSale(Long id){
        TimeSegmentPo po =timeSegmentPoMapper.selectByPrimaryKey(id);
        if(po.getType().equals((byte)1))
        {
            return true;
        }
        else {return false;}
    }

    /**
     * 根据时间段ID获得秒杀时段信息
     * @author zwl
     * @param
     * @return
     * @Date:  2020/12/18 17:08
     */

    public FlashSaleTimeSegmentDTO getFlashSaleTimeSegmentById(Long id){
        TimeSegmentPo po =timeSegmentPoMapper.selectByPrimaryKey(id);
        if(po.getType().equals((byte)1))
        {
            FlashSaleTimeSegmentDTO ret = new FlashSaleTimeSegmentDTO();
            ret.setId(po.getId());
            ret.setBeginTime(po.getBeginTime());
            ret.setEndTime(po.getEndTime());
            ret.setGmtCreate(po.getGmtCreate());
            ret.setGmtModified(po.getGmtModified());
            return ret;
        }
        return null;
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
        if(po==null || po.getType().intValue()==1){
            retObj=new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        else{
            retObj=new ReturnObject<>(po);
        }
        return retObj;
    }

}
