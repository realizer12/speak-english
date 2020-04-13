# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

set(pathPROJECT C:/Users/leedonghun/Desktop/andorid/speakenglish)
set(pathOPENCV ${pathPROJECT}/opencv)
set(pathLIBOPENCV_JAVA ${pathOPENCV}/native/libs/${CMAKE_ANDROID_ARCH_ABI}/libopencv_java4.so)
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${pathOPENCV}/native/jni/include)




# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.


add_library( # Specifies the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp

        )

add_library(# Specifies the name of the library.
        face-detect

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        face-detect.cpp)

add_library(# Specifies the name of the library.
        face-detect-album

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        face-detect-album.cpp)

add_library( # Specifies the name of the library.
        Image-filtering

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        Image-filtering.cpp

        )


include_directories(src/main/cpp/include/)


add_library(lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV_JAVA})

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

find_library( # Sets the name of the path variable.
        log-lib1

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

find_library( # Sets the name of the path variable.
        log-lib2

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


target_link_libraries( # Specifies the target library.
        face-detect

        lib_opencv

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

target_link_libraries( # Specifies the target library.
        face-detect-album

        lib_opencv

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib1})

target_link_libraries( # Specifies the target library.
        Image-filtering

        lib_opencv

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib2})