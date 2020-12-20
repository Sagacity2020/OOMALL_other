package cn.edu.xmu.favorite;
import cn.edu.xmu.favorite.dao.FavouriteGoodsDao;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zwl
 **/
@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.favorite", "cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.favorite.mapper")
public class FavoriteServiceApplication implements ApplicationRunner{
    private  static  final Logger logger = LoggerFactory.getLogger(FavoriteServiceApplication.class);
    /**
     * 是否初始化，生成signature和加密
     */
    @Value("${Otherservice.initialization}")
    private Boolean initialization;

    @Autowired
    private FavouriteGoodsDao favouriteGoodsDao;

//    @Autowired
//    private TimeSegmentDao timeSegmentDao;

    public static void main(String[] args) {
        SpringApplication.run(FavoriteServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        if (initialization){
//            logger.debug("Initialize......");
//            //cartDao.initialize();
//
//        }
    }
}
