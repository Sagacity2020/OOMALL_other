package cn.edu.xmu.address;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zrh
 * @Created at 12/7 23:51
 */
@EnableDubbo(scanBasePackages = "cn.edu.xmu.address,service")
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.address","cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.address.mapper")
@EnableDiscoveryClient
public class AddressApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(AddressApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AddressApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
