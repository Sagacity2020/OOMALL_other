package cn.edu.xmu.address.service;

import cn.edu.xmu.address.model.po.RegionPo;
import cn.edu.xmu.address.model.vo.RegionRetVo;
import cn.edu.xmu.address.model.vo.RegionVo;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.address.dao.RegionDao;
import cn.edu.xmu.address.model.bo.Region;
import cn.edu.xmu.other.service.RegionServiceInterface;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger.common.Version;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@DubboService(version = "0.0.1")
@Service
public class RegionService implements RegionServiceInterface {

    private Logger logger= LoggerFactory.getLogger(RegionService.class);

    @Autowired
    private RegionDao regionDao;

    /**
     * @Created at 12/7 1:03
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject queryPreRegion(Long id) {
        ReturnObject<Region> regionReturnObject=regionDao.queryPreRegion(id);
        List<Region> regions=new ArrayList<>();
        if(regionReturnObject.getData()==null) {
            return regionReturnObject;
        }
        regions.add(regionReturnObject.getData());
        ReturnObject<Region> regionReturnObject1=regionDao.queryPreRegion(regionReturnObject.getData().getPid());
        while(regionReturnObject1.getData()!=null){
            regions.add(regionReturnObject1.getData());
            regionReturnObject1=regionDao.queryPreRegion(regionReturnObject1.getData().getPid());
        }
        return new ReturnObject(regions);


    }


    /**
     * @Created at 12/18 1:49
     * @author zrh
     * @param id
     * @param vo
     * @return
     */
    public ReturnObject newSubRegion(Long id, RegionVo vo) {
        return regionDao.newSubRegion(id,vo);
    }

    /**
     * @Created at 12/9 19:40
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject<Boolean> isRegion(Long id){
        ReturnObject<Boolean> returnObject=regionDao.isRegion(id);
        return returnObject;
    }

    /**
     * @Created at 12/10 21:29
     * @author zrh
     * @param region
     * @return
     */
    public ReturnObject updateRegion(Region region) {
        ReturnObject returnObject=regionDao.updateRegion(region);
        return returnObject;
    }

    /**
     * @Created at 12/10 21:29
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject deleteRegion(Long id) {
//        Queue<Long> idQueue = new ArrayDeque<>();
        ReturnObject returnObject=regionDao.deleteRegion(id);
        List<Long> ids=regionDao.getChildRegion(id);
//        idQueue.addAll(ids);
//        if(!idQueue.isEmpty()){
//            Long childId=idQueue.poll();
//            regionDao.deleteChildRegionOnly(childId);
//            }
//
        return returnObject;
    }


    @Override
    public Long getParentRegionIdByChildId(Long regionId) {
        ReturnObject<Region> returnObject=regionDao.queryPreRegion(regionId);
        if(returnObject.getCode()== ResponseCode.REGION_OBSOLETE){
            return  null;
        }
        Region region=returnObject.getData();
        if(region!=null){
            return region.getId();
        }
        return 0L;
    }
}
