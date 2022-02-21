cmake_minimum_required(VERSION 3.4.1)

# 设置生成的so动态库最后输出的路径
# set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

# 支持C++11标准和GNU扩展特性
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")


set(jnilibs "${CMAKE_SOURCE_DIR}/../jniLibs")

include_directories(
        glm
        include
#        include/freetype_2_9_1
)

# 查找代码中使用到的系统库
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

#add_library(freetype STATIC IMPORTED)
#set_target_properties(freetype PROPERTIES IMPORTED_LOCATION ${jnilibs}/${ANDROID_ABI}/libfreetype.a)

file(GLOB src-files
        ${CMAKE_SOURCE_DIR}/*.cpp
        ${CMAKE_SOURCE_DIR}/sample/*.cpp
        ${CMAKE_SOURCE_DIR}/util/*.cpp
        ${CMAKE_SOURCE_DIR}/func/*.cpp
        ${CMAKE_SOURCE_DIR}/base/*.cpp)


# 配置目标so库编译信息
add_library( # Sets the name of the library.
        native-lib
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${src-files})

set(third-party-libs
        freetype)


# 指定编译目标库时，cmake要链接的库
target_link_libraries(
        native-lib

        # 指定目标库，native-lib 是在上面 add_library 中配置的目标库
#        ${third-party-libs}
        EGL
        GLESv3
        android
        ${log-lib})