# 基于拦截器计算记录业务日志

&emsp;&emsp;项目基于spring aop 注解拦截实现,定义抽象基类对各个VO、DO等类进行控制，从而抽取自定义日志内容进行拼装日志实体（包括日志实体中前缀、后缀等），通过线程池异步构建日志体，不为主线程增加多余耗时处理，待主线程以及构建线程完成后，将日志实体发送到rocketMQ，再由日志消费端消费入库。

## V1.0-SNAPSHOT

### maven依赖引用
```xml

        <!-- bizLog -->
        <dependency>
            <groupId>com.coco.framework</groupId>
            <artifactId>cos-biz-log</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

```

### yaml参数配置
#### rocketMq配置
```yaml

# rocketMq 配置信息
cos:
  bizlog:
    rocketMq:
      producer:
        groupName: cos-log-producer
        namesrvAddr: 10.213.3.125:9876
        topic: cos-biz-log
#        retryTimes: 2

```

#### 线程池可选配置
&emsp;&emsp;若项目中已经配置线程池，该组件将不会自动生成线程池。若项目中没有配置，则会生成一个默认线程池，配置如下，也可以自己在yaml中进行配置修改参数。
```yaml
# 默认线程池 配置信息
cos:
  bizlog:
    executor:
      core-pool-size: 10
      max-pool-size: 30
      keep-alive-time: 60000
      queue-capacity: 1000
```

## V1.1-SNAPSHOT
## V1.1.0

### maven依赖引用
```xml

        <!-- bizLog -->
        <dependency>
            <groupId>com.coco.framework</groupId>
            <artifactId>cos-biz-log</artifactId>
            <version>1.1-SNAPSHOT</version>
        </dependency>

```

### feign 依赖引用

@EnableFeignClients(basePackages = "com.coco.framework.cosbizlog.mesh")


## 注解
### BizLog
&emsp;&emsp;该注解即Aop 拦截切点，定义在业务服务实现方法上即可
```java
    @Override
    @BizLog(logEvent = LogEventEnum.ADD, logModule = LogModuleEnum.DICT)
    public DictItem addDictItem(DictItem dictItem) throws Exception {
        return dictItem;
    }
```
### BizLogVsClass
&emsp;&emsp;该注解使用在业务实体VO类上，添加该注解，该类下面的所有属性会进行比较，有不同即会记录到日志实体中

### BizLogVsField
&emsp;&emsp;该注解使用在业务实体VO类属性上，添加该注解，类比较时，该属性一定会比较（即大多使用在BizLogVsClass注解没有的情况下，进行的若干属性比较），fieldNameStr 定义校验不同时日志显示 别名 ，若没有，即取属性名称为显示别名，strMethodName 定义校验不同时取值来源，若没有，取属性值进行显示。

```java
    @BizLogVsField(fieldNameStr = "渠道",strMethodName = "getCustChannelName")
    private String channelType;
    // 校验结果： 渠道： 东 -> 西
```

### 日志服务 实体 功能类 BizLogStr
&emsp;&emsp; BizLogStr 抽象日志服务实体基类，为Aop服务，为自定义日志体服务，为校验服务，该类提供获取数据库主键功能，获取日志前缀、后缀的功能，VO与DO的转化等功能，所有需要记录日志的实体都必须继承这个类

### CosBizLog 基础服务接口 
&emsp;&emsp; BizLogBaseService 接口定义两个必须实现的方法，一个是获取原数据，一个是获取当前登录人，各项目实现类需要实现这两个方法

## 改进
&emsp;&emsp;当前版本还不支持自定义模块，待改进

