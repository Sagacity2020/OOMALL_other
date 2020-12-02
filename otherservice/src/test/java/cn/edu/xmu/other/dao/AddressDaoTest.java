package cn.edu.xmu.other.dao;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.other.OtherServiceApplication;
import cn.edu.xmu.other.model.po.AddressPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(classes = OtherServiceApplication.class)
@Transactional
public class AddressDaoTest {


}
