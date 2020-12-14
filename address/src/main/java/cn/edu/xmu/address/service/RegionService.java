package cn.edu.xmu.address.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.address.dao.RegionDao;
import cn.edu.xmu.address.model.bo.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RegionService {

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
        return regionReturnObject;
    }

    /**
     * @Created at 12/8 23:08
     * @author zrh
     * @param region
     * @return
     */
    public ReturnObject newSubRegion(Region region) {
        return regionDao.newSubRegion(region);
    }

    /**
     * @Created at 12/9 19:40
     * @author zrh
     * @param id
     * @return
     */
    public ReturnObject<Integer> isRegion(Long id){
        ReturnObject<Integer> returnObject=regionDao.isRegion(id);
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
        ReturnObject returnObject=regionDao.deleteRegion(id);
        return returnObject;
    }
}
