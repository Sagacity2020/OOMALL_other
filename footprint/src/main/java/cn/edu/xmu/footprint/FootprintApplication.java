package cn.edu.xmu.footprint;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.footprint"})
@MapperScan(basePackages = {"cn.edu.xmu.footprint.mapper"})
@EnableDiscoveryClient
public class FootprintApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(FootprintApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
