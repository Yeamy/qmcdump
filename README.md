QQ Music Copyright Protection File Dump
===========

## 简介
此项目为[qmcdump](https://github.com/MegrezZhu/qmcdump)的Java移植版本，核心代码在`QmcDump.java`里面，
界面使用javafx开发。

## 代码
界面相关类：MainUI, TaskBean TaskStatus
核心类: QmcmDump
直接调用`QmcDump.dump(qmcFile, outDirectory)`执行破解

## 使用
- 程序已经打包好[(这里下载)](https://github.com/Yeamy/qmcdump/releases)，使用前请先安装JRE（废话）

- 生成文件默认与源文件同目录，点击`输出目录`按钮即可修改，再次点击会恢复成与源文件同目录，输出目录在转换期间不能修改。

- 拖动qmc文件至界面会直接执行转换，相同名字文件会被新文件覆盖，转换期间不能再添加文件。

## 说明
- 目前移植的版本已经参考最新版更新，以后会不定时更新。

- 仅为个人爱好学习，请勿用于非法途径。