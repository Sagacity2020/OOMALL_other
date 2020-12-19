package cn.edu.xmu.advertisement;

import cn.edu.xmu.advertisement.dao.AdvertisementDao;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.advertisement"},exclude={DataSourceAutoConfiguration.class})
@MapperScan("cn.edu.xmu.advertisement.mapper")
//@EnableDubbo(scanBasePackages = "cn.edu.xmu.oomall.other.service")
public class AdvertisementApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(AdvertisementApplication.class);
    /**
     * 是否初始化，生成signature和加密
     */
   // @Value("${Otherservice.initialization}")
    //private Boolean initialization;

    @Autowired
    private AdvertisementDao advertisementDao;

    public static void main(String[] args) {
        SpringApplication.run(AdvertisementApplication.class, args);
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
