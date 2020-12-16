package cn.edu.xmu.time;

import cn.edu.xmu.time.dao.TimeSegmentDao;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author zwl
 **/
@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.time"})
@MapperScan("cn.edu.xmu.time.mapper")
//@EnableDubbo(scanBasePackages = "cn.edu.xmu.oomall.other.service")
public class TimeServiceApplication implements ApplicationRunner{
    private  static  final Logger logger = LoggerFactory.getLogger(TimeServiceApplication.class);
    /**
     * 是否初始化，生成signature和加密
     */
    @Value("${Otherservice.initialization}")
    private Boolean initialization;

    @Autowired
    private TimeSegmentDao timeSegmentDao;

    public static void main(String[] args) {
        SpringApplication.run(TimeServiceApplication.class, args);
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
