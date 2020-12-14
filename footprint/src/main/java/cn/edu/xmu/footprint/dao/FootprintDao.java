package cn.edu.xmu.footprint.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.footprint.mapper.FootPrintPoMapper;
import cn.edu.xmu.footprint.model.bo.Footprint;
import cn.edu.xmu.footprint.model.po.FootPrintPo;
import cn.edu.xmu.footprint.model.po.FootPrintPoExample;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 ** @author 向姝可
 **/
@Repository
public class FootprintDao {

    private static final Logger logger = LoggerFactory.getLogger(FootprintDao.class);
    @Autowired
    private FootPrintPoMapper footprintpoMapper;

    public ReturnObject<Footprint> insertFootprint(Footprint footprint) {
        FootPrintPo footprintpo=footprint.createpo();
        ReturnObject<Footprint> retObj = null;
        try{
            int ret = footprintpoMapper.insertSelective(footprintpo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertFootprint: insert footprint fail " + footprintpo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + footprintpo.getCustomerId()));
            } else {
                //插入成功
                logger.debug("insertFootprint: insert footprint = " + footprintpo.toString());
                footprint.setId(footprintpo.getId());
                retObj = new ReturnObject<>(footprint);
            }
        }
        catch (DataAccessException e) {
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

    public PageInfo<FootPrintPo> getFootprint(Long userId,String beginTime,String endTime,Integer page,Integer pagesize){
        FootPrintPoExample example=new FootPrintPoExample();
        FootPrintPoExample.Criteria criteria=example.createCriteria();
        if(userId==-1){
             criteria.andCustomerIdEqualTo(userId);}
        if(!beginTime.isBlank()){
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime beginLdt = LocalDateTime.parse(beginTime,df);
            criteria.andGmtCreateGreaterThanOrEqualTo(beginLdt);}
        if(!endTime.isBlank()){
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime endLdt = LocalDateTime.parse(endTime,df);
            criteria.andGmtCreateLessThanOrEqualTo(endLdt);}
        List<FootPrintPo> footprints=footprintpoMapper.selectByExample(example);

        return new PageInfo<>(footprints);
        }
    }

