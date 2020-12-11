package cn.edu.xmu.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Created at 12/10 22:04
 * @author zrh
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.cart","cn.edu.xmu.ooad"})
@MapperScan("cn.edu.xmu.cart.mapper")
public class CartApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(CartApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
