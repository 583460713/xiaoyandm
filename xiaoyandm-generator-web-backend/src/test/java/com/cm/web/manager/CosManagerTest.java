package com.cm.web.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;


/**
 * @author 语仄无言
 */
@SpringBootTest
public class CosManagerTest {

    @Resource
    private CosManager cosManager;
    @Test
    public void deleteObject() {
        cosManager.deleteObject("/generator_make_template/1/a.zip");
    }

    @Test
    public void deleteObjects() {
        cosManager.deleteObjects(Arrays.asList("generator_make_template/1/a.zip",
                "generator_make_template/1/b.zip"));
    }

    @Test
    public void deleteDir() {
        cosManager.deleteDir("/generator_picture/1/");
    }
}