# UpdateCheckerLib
一个简单小巧的Android检查更新库，通过爬网页的方式查询应用在市场上的最新版本信息
目前支持Google Play，酷安网和豌豆荚的检查。
# 开始
## 添加依赖
### Gradle
```groovy
compile 'kh.android:updatecheckerlib:1.0'
```
### Maven
```maven
<dependency>
  <groupId>kh.android</groupId>
  <artifactId>updatecheckerlib</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```
### 添加权限
检查更新需要添加网络权限，如果没有，需要添加：
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
# 检查更新
同步检查更新：
```java
UpdateChecker.check(UpdateChecker.Market 市场，String 包名)
```
返回UpdateInfo:
```java
getChangeLog() //获取更新日志
getMarket() //获取市场
getPackageName() //获取包名
getVersionName() //获取最新版本名称
```
同时，也可以异步检查：
```java
UpdateChecker.checkSync(UpdateChecker.Market 市场，String 包名，Context，UpdateChecker.OnCheckListener);
```
## UpdateChecker.OnCheckListener:
UpdateChecker.OnCheckListener是异步查询接口。它有两个方法：
```java
onStartCheck() //当开始查询时触发
done(UpdateInfo,Exception) //查询完毕触发
```
done()触发时，参数1是查询的结果，参数2是发生的异常。如果查询成功，会返回结果，异常为空。如果查询失败，会返回异常。
# Proguard
使用了Jsoup解析HTML，需要在proguard-rules.pro加入：
```
-keep class org.jsoup.**
```
# Licence
```
Copyright (C) 2016 liangyuteng0927

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```