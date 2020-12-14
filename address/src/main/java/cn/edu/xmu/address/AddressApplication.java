package cn.edu.xmu.address;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zrh
 * @Created at 12/7 23:51
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.address","cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.address.mapper")
public class AddressApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(AddressApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AddressApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
