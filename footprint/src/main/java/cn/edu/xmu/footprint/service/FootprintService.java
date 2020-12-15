package cn.edu.xmu.footprint.service;

import cn.edu.xmu.footprint.model.bo.FootprintDetail;
import cn.edu.xmu.footprint.model.vo.GoodsSkuDTO;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.footprint.dao.FootprintDao;
import cn.edu.xmu.footprint.model.bo.Footprint;
import cn.edu.xmu.footprint.model.po.FootPrintPo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 ** @author 向姝可
 **/
@Service
public class FootprintService {
    private Logger logger = LoggerFactory.getLogger(FootprintService.class);

    @Autowired
    private FootprintDao footprintDao;



//    public ReturnObject<VoObject> insertFootprint(Long userId, Long goodsSkuId) {
//        Footprint footprint=new Footprint();
//        footprint.setCustomerId(userId);
//        footprint.setGoodsSkuId(goodsSkuId);
//        footprint.setGmtCreate(LocalDateTime.now());
//        ReturnObject<Footprint> retObj=footprintDao.insertFootprint(footprint);
//        ReturnObject<VoObject> ret = null;
//        if (retObj.getCode().equals(ResponseCode.OK)) {
//            ret = new ReturnObject<>(retObj.getData());
//        } else {
//            ret = new ReturnObject<>(retObj.getCode(), retObj.getErrmsg());
//        }
//        return ret;
//    }

    public ReturnObject<PageInfo<VoObject>> getFootprint(Long userId,String beginTime,String endTime,Integer page, Integer pagesize){
        PageHelper.startPage(page, pagesize);
        PageInfo<FootPrintPo> footprintPos=footprintDao.getFootprint(userId,beginTime,endTime,page,pagesize);

       // List<Footprint> footprints=footprintPos.getList().stream().map(Footprint::new).collect(Collectors.toList());
        List<FootprintDetail> footprints=null;
        for (FootPrintPo po:footprintPos.getList()) {
            FootprintDetail footprintDetail=new FootprintDetail(po);
            GoodsSkuDTO goodsSku=iGoodsService.getSkuById(po.getGoodsSkuId());
            footprintDetail.setGoodsSku(goodsSku);
            footprints.add(footprintDetail);
        }

        PageInfo<VoObject> returnObject=new PageInfo<>(footprints);
        returnObject.setPages(footprintPos.getPages());
        returnObject.setPageNum(footprintPos.getPageNum());
        returnObject.setPageSize(footprintPos.getPageSize());
        returnObject.setTotal(footprintPos.getTotal());

        return new ReturnObject<>(returnObject);
    }
}
