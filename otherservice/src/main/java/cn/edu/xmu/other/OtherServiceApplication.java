package cn.edu.xmu.other;

import cn.edu.xmu.other.dao.CartDao;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Ming Qiu
 **/
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.other"})
@MapperScan("cn.edu.xmu.other.mapper")
public class OtherServiceApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(OtherServiceApplication.class);
    /**
     * 是否初始化，生成signature和加密
     */
//    @Value("${Otherservice.initialization}")
//    private Boolean initialization;

//    @Autowired
//    private CartDao cartDao;

    public static void main(String[] args) {
        SpringApplication.run(OtherServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        if (initialization){
//            logger.debug("Initialize......");
//            cartDao.initialize();
//
//        }
    }
}
