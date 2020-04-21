# mysql

```sql
CREATE TABLE IF NOT EXISTS STUDENT (
    SNO VARCHAR2(30) NOT NULL ,
    NAME VARCHAR2(80) NOT NULL ,
    SEX CHAR(20) NOT NULL 
);

INSERT INTO STUDENT VALUES ('001', 'Wangkan', 'M');
INSERT INTO STUDENT VALUES ('002', 'Mike', 'M');
INSERT INTO STUDENT VALUES ('003', 'Jane', 'F');

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userName` varchar(32) DEFAULT NULL COMMENT '用户名',
  `passWord` varchar(32) DEFAULT NULL COMMENT '密码',
  `user_sex` varchar(32) DEFAULT NULL COMMENT '性别',
  `nick_name` varchar(32) DEFAULT NULL COMMENT '别名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

```

# Test

启动项目.

访问 http://localhost:8080/web/querystudent?sno=001.

```test
{"sno":"001","name":"KangKang","sex":"M"}
```

login druid
访问 http://localhost:8080/web/druid.

https://www.cnblogs.com/leeSmall/p/8719455.html

# 获取 Druid 的监控数据

Druid 的监控数据可以在开启 StatFilter 后通过 DruidStatManagerFacade 进行获取，获取到监控数据之后我们便可以将其暴露给我们自己的监控系统进行使用。Druid 默认的监控系统数据也来源于此。

（1）下面是一个简单的演示样例，在 Spring Boot 中通过 HTTP 接口将 Druid 监控数据以 JSON 的形式暴露出去（实际使用中我们可以根据实际的需要自由地对监控数据、暴露方式进行扩展）

```java
@RestController
public class DruidStatController {
    @GetMapping("/druid-stat")
    public Object druidStat(){
        // DruidStatManagerFacade#getDataSourceStatDataList 该方法可以获取所有数据源的监控数据
        // 除此之外 DruidStatManagerFacade 还提供了一些其他方法，我们可以按需选择使用。
        return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
    }
}
```

（2）通过 /druid-stat 接口获取到相应统计数据。

# 去除广告

在 SpringBoot 项目中编写一个 RemoveDruidAdConfig 配置类即可，代码如下：


原理说明：之所以底部有广告，是因为其引入的 druid jar 包的 common.js 中的内容（里面有一段是在 footer 添加广告），在 RemoveDruidAdConfig 配置类中使用过滤器过滤 common.js 的请求，重新处理后用正则替换相关的广告代码片段.