package cn.edu.xmu.aftersale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.aftersale"},exclude={DataSourceAutoConfiguration.class})
@MapperScan("cn.edu.xmu.aftersale.mapper")
public class AftersaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AftersaleApplication.class, args);
    }

}
