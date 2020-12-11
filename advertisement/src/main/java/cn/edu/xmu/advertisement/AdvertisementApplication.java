package cn.edu.xmu.advertisement;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.advertisement"},exclude={DataSourceAutoConfiguration.class})
@MapperScan("cn.edu.xmu.advertisement.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.oomall.other.service")
public class AdvertisementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvertisementApplication.class, args);
    }

}
