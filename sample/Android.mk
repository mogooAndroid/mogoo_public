LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
# 以下两句定义library project 资源目录
# library project 与 main project在同级目录下
lib_dir := ../library/res
res_dir := $(lib_dir) res
LOCAL_MODULE_TAGS := optional

# List of static libraries to include in the package
LOCAL_STATIC_JAVA_LIBRARIES := supportv4 \
							 signpost-commonshttp \
							 signpost-core
# Build all java files in the java subdirectory
# 添加编译library project 源码
LOCAL_SRC_FILES := $(call all-java-files-under, src) \
                     $(call all-java-files-under, ../library/src)
# 添加编译library project 资源
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dir))

# Name of the APK to build
LOCAL_PACKAGE_NAME := Droidmi
# 打开以下两句启用混淆代码, 必须放在"include $(BUILD_PACKAGE)"之前
# LOCAL_PROGUARD_ENABLED := full
# LOCAL_PROGUARD_FLAG_FILES := proguard.flags

# Tell it to build an APK
include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := supportv4:../library/libs/android-support-v4.jar \
									signpost-commonshttp:../library/libs/signpost-commonshttp.jar \
									signpost-core:../library/libs/signpost-core.jar
include $(BUILD_MULTI_PREBUILT)
include $(call all-makefiles-under,$(LOCAL_PATH))