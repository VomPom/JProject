# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Declares and names the project.

project("learncpp")
include_directories(${CMAKE_SOURCE_DIR}/common/)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        learncpp

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        bitmap_operation.cpp
        cache_filed_and_method.cpp
        #        thread_operation.cpp
        native/design_ops.cpp
        android/hello-jni.cpp
        android/jni_callback.cpp
        )

add_library(
        common
        SHARED
        ${CMAKE_SOURCE_DIR}/common/commonutil.cpp
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        learncpp
        common
        -ljnigraphics
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
############################## NATIVE ACTIVITY COMMON START ##################################
# build native_app_glue as a static lib
set(${CMAKE_C_FLAGS}, "${CMAKE_C_FLAGS}")
add_library(native_app_glue STATIC
        ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)

# Export ANativeActivity_onCreate(),
# Refer to: https://github.com/android-ndk/ndk/issues/381.
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")
############################## NATIVE ACTIVITY COMMON END ##################################

############################## NATIVE ACTIVITY START ##################################


add_library(native-activity
        SHARED
        android/nativity_acitivity.cpp)
target_include_directories(native-activity PRIVATE
        ${ANDROID_NDK}/sources/android/native_app_glue)

# add lib dependencies
target_link_libraries(native-activity
        android
        native_app_glue
        EGL
        GLESv1_CM
        log)

############################## NATIVE ACTIVITY END ##################################


########################## NATIVE MEDIACODEC ACTIVITY START ############################

add_library(native-codec-jni SHARED
        android/mediacodec/looper.cpp
        android/mediacodec/native-codec-jni.cpp)

# Include libraries needed for native-codec-jni lib
target_link_libraries(native-codec-jni
        android
        log
        mediandk
        OpenMAXAL)

########################## NATIVE MEDIACODEC ACTIVITY END ############################