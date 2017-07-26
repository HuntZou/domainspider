# domainspider
根据网站名称查找域名<br/>
调用方式：
```java
  String url = new DomainSpider().getUrlByName("人人网");
  //或者调用其重载方法，可选择快速查询和准确查询（但都不能保证百分百的准确）
  String url = new DomainSpider().getUrlByName("人人网 url:",DomainSpider.SPEED_EXACT);
```
展示效果:<br/>
![image](https://github.com/Jhinwins/domainspider/blob/master/imgs/show.PNG)
