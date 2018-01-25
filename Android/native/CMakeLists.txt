# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.


add_library( NdkGLRenderer
            SHARED
            libCRender/Matrix.cpp
            libCRender/org_crender_NDKGlRender_gles2.cpp
)
set_target_properties(NdkGLRenderer PROPERTIES LINKER_LANGUAGE CXX)

target_link_libraries( # Specifies the target library.
                       # 项目链接的.so库名称
                       NdkGLRenderer

                       # Links the target library to the log library
                       # included in the NDK.
                       android
                       log
                       EGL
                       GLESv2
                       GLESv1_CM
 )


# Specifies a path to native header files.
 include_directories(native/libCRender/)