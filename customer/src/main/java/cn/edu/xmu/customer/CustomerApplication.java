package cn.edu.xmu.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.customer"})
@MapperScan(basePackages = {"cn.edu.xmu.customer.mapper"})
@EnableDiscoveryClient
public class CustomerApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
