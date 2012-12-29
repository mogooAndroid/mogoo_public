# 日志文件
# lin.xr add 2012.12.29
1. library project 中的资源文件尽量使用以"lib_"为前缀命名, 避免与主项目资源冲突;
2. library project 中/res/values/中的资源id定义须按以下定义, 否则在作为依赖项目在主项目编译
  会出现"Resource at xxx appears in overlay but not in the base package,use 
  <add-resource> to add."的异常.因为最终产生的应用R文件的完全限定名是主项目的packageName.
	<add-resource type="string" name="lib_app_name" />
    <string name="lib_app_name">DroidLibrary</string>
