package com.medical.drugdelivery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 * 县域医共体药品配送管理系统的后端入口，直接跑main方法就能启动
 * MapperScan指定了Mapper接口所在的包，MyBatis-Plus才能扫到
 */
@SpringBootApplication
@MapperScan("com.medical.drugdelivery.mapper")
public class DrugDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrugDeliveryApplication.class, args);
        System.out.println("========== 医共体药品配送管理系统启动成功 ==========");
    }
}
