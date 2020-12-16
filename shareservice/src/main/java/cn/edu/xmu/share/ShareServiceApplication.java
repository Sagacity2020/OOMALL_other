package cn.edu.xmu.share;

import cn.edu.xmu.share.dao.ShareActivityDao;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Ming Qiu
 **/
@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.share","cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.share.mapper")
public class ShareServiceApplication implements ApplicationRunner {


    private  static  final Logger logger = LoggerFactory.getLogger(ShareServiceApplication.class);

    @Autowired
    private ShareActivityDao shareActivityDao;
    /**
     * 是否初始化，生成signature和加密
     */

    public static void main(String[] args) {
        SpringApplication.run(ShareServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        shareActivityDao.initialize();
    }
}
