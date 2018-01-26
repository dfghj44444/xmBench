#include <jni.h>
#include <android/log.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include "org_crender_NDKGlRender.h"
#include "Matrix.h"
 #define  LOG_TAG    "libgljni"
 #define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
 #define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

 #define POSITION_DATA_SIZE 3
 #define COLOR_DATA_SIZE 4

 GLfloat gMVPMatrix[16] = {0.0f};
 GLfloat gViewMatrix[16] = {0.0f};
 GLfloat gModelMatrix[16] = {0.0f};
 GLfloat gProjectionMatrix[16] = {0.0f};

GLuint gMMatrixHandle = 0;
GLuint gVMatrixHandle = 0;
GLuint gPMatrixHandle = 0;
 GLuint gPositionHandle = 0;
 GLuint gColorHandle = 0;
 GLuint gProgram = 0;

 const GLfloat cubePosition[] =
 {
     -1.0f, 1.0f, 1.0f,
     -1.0f, -1.0f, 1.0f,
     1.0f, 1.0f, 1.0f,
     -1.0f, -1.0f, 1.0f,
     1.0f, -1.0f, 1.0f,
     1.0f, 1.0f, 1.0f,

     1.0f, 1.0f, 1.0f,
     1.0f, -1.0f, 1.0f,
     1.0f, 1.0f, -1.0f,
     1.0f, -1.0f, 1.0f,
     1.0f, -1.0f, -1.0f,
     1.0f, 1.0f, -1.0f,

     1.0f, 1.0f, -1.0f,
     1.0f, -1.0f, -1.0f,
     -1.0f, 1.0f, -1.0f,
     1.0f, -1.0f, -1.0f,
     -1.0f, -1.0f, -1.0f,
     -1.0f, 1.0f, -1.0f,

     -1.0f, 1.0f, -1.0f,
     -1.0f, -1.0f, -1.0f,
     -1.0f, 1.0f, 1.0f,
     -1.0f, -1.0f, -1.0f,
     -1.0f, -1.0f, 1.0f,
     -1.0f, 1.0f, 1.0f,

     -1.0f, 1.0f, -1.0f,
     -1.0f, 1.0f, 1.0f,
     1.0f, 1.0f, -1.0f,
     -1.0f, 1.0f, 1.0f,
     1.0f, 1.0f, 1.0f,
     1.0f, 1.0f, -1.0f,

     1.0f, -1.0f, -1.0f,
     1.0f, -1.0f, 1.0f,
     -1.0f, -1.0f, -1.0f,
     1.0f, -1.0f, 1.0f,
     -1.0f, -1.0f, 1.0f,
     -1.0f, -1.0f, -1.0f
 };

 const GLfloat cubeColor[] =
 {
     1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,

    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,

    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,

    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,

    0.0f, 1.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f, 1.0f,
    0.0f, 1.0f, 1.0f, 1.0f,

    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f
};

static const char gVertexShader[] =
{
    "uniform mat4 u_MMatrix;  uniform mat4 u_VMatrix;uniform mat4 u_PMatrix;    \n"
    "attribute vec4 a_Position;     \n"
    "attribute vec4 a_Color;        \n"

    "varying vec4 v_Color;          \n"

    "void main()                    \n"
    "{                              \n"
    "   v_Color = a_Color;          \n"
    "   gl_Position =   u_PMatrix * u_VMatrix * u_MMatrix*a_Position ;   \n"
    "}                              \n"
};

static const char gFragmentShader[] =
{
    "precision mediump float;       \n"
    "varying vec4 v_Color;          \n"
    "void main()                    \n"
    "{                              \n"
    "   gl_FragColor = v_Color;     \n"
    "}                              \n"
};

GLuint loadShader(GLenum type, const char* source)
{
    GLuint shader = glCreateShader(type);
    if(shader)
    {
        glShaderSource(shader, 1, &source, NULL);
        glCompileShader(shader);
        GLint compileStatus = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compileStatus);
        if(!compileStatus)
        {
            GLint info_length = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &info_length);
            if(info_length)
            {
                char* buf = (char*)malloc(info_length * sizeof(char));
                if(buf)
                {
                    glGetShaderInfoLog(shader, info_length, NULL, buf);
                    LOGE("Create shader %d failed\n%s\n", type, buf);
                }
            }
            glDeleteShader(shader);
            shader = 0;
        }
    }
    return shader;
}

GLuint createProgram(const char* pVertexSource, const char* pFragmentSource)
{
    GLuint vshader = loadShader(GL_VERTEX_SHADER, pVertexSource);
    if(!vshader)
    {
        return 0;
    }
    GLuint fshader = loadShader(GL_FRAGMENT_SHADER, pFragmentSource);
    if(!fshader)
    {
        return 0;
    }
    GLuint program = glCreateProgram();
    if(program)
    {
        glAttachShader(program, vshader);
        glAttachShader(program, fshader);
        glBindAttribLocation(program, 0, "a_Position");
        glBindAttribLocation(program, 1, "a_Color");

        glLinkProgram(program);

        GLint status = 0;
        glGetProgramiv(program, GL_LINK_STATUS, &status);

        if(!status)
        {
            GLint info_length = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &info_length);
            if(info_length)
            {
                char* buf = (char*)malloc(info_length * sizeof(char));
                glGetProgramInfoLog(program, info_length, NULL, buf);
                LOGE("create program failed\n%s\n", buf);
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
}

static GLfloat angleInDegrees = 0.1;

void drawCube(const GLfloat* positions, const GLfloat* colors)
{
    glVertexAttribPointer(gPositionHandle, POSITION_DATA_SIZE, GL_FLOAT, GL_FALSE, 0, positions);
    glEnableVertexAttribArray(gPositionHandle);
    glVertexAttribPointer(gColorHandle, COLOR_DATA_SIZE, GL_FLOAT, GL_FALSE, 0, colors);
    glEnableVertexAttribArray(gColorHandle);
    Matrix::multiplyMM(gMVPMatrix, 0, gViewMatrix, 0, gModelMatrix, 0);
    Matrix::multiplyMM(gMVPMatrix, 0, gProjectionMatrix, 0, gMVPMatrix, 0);
    //float sample[] ={1,0,0,0 ,0,1,0,0 ,0 ,0,1,0,1,1,1,1};
    //memcpy(gMVPMatrix,gProjectionMatrix,16*sizeof(float));
    glUniformMatrix4fv(gMMatrixHandle, 1, GL_FALSE, gModelMatrix);
    glUniformMatrix4fv(gVMatrixHandle, 1, GL_FALSE, gViewMatrix);
    glUniformMatrix4fv(gPMatrixHandle, 1, GL_FALSE, gProjectionMatrix);
    glDrawArrays(GL_TRIANGLES, 0, 36);
}

void renderFrame()
{
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glUseProgram(gProgram);
    gMMatrixHandle = glGetUniformLocation(gProgram, "u_MMatrix");
    gVMatrixHandle = glGetUniformLocation(gProgram, "u_VMatrix");
    gPMatrixHandle = glGetUniformLocation(gProgram, "u_PMatrix");
    gPositionHandle = glGetAttribLocation(gProgram, "a_Position");
    gColorHandle = glGetAttribLocation(gProgram, "a_Color");

    Matrix::setIdentityM(gModelMatrix, 0);
    Matrix::translateM(gModelMatrix, 0, 0.0f, 0.0f, -5.0f);
    if(359.0 <= angleInDegrees)
    {
        angleInDegrees = 0.1;
    }
    else
    {
        angleInDegrees = angleInDegrees + 1.0;
    }

    Matrix::rotateM(gModelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);

    drawCube(cubePosition, cubeColor);
}



JNIEXPORT void JNICALL Java_org_crender_NDKGlRender_onNdkSurfaceCreated(JNIEnv * env, jobject object)
{
    LOGI("create");
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);
    const GLfloat eyeX = 0.0f;
    const GLfloat eyeY = 0.0f;
    const GLfloat eyeZ = -0.5f;

    const GLfloat lookX = 0.0f;
    const GLfloat lookY = 0.0f;
    const GLfloat lookZ = -5.0f;

    const GLfloat upX = 0.0f;
    const GLfloat upY = 1.0f;
    const GLfloat upZ = 0.0f;

    Matrix::setLookAtM(gViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    gProgram = createProgram(gVertexShader, gFragmentShader);
}

JNIEXPORT void JNICALL Java_org_crender_NDKGlRender_onNdkSurfaceChanged(JNIEnv * env, jobject object, jint width, jint height)
{
    LOGI("init");
    glViewport(0, 0, width, height);
    const GLfloat ratio = (GLfloat) width / height;
    const GLfloat left = -ratio;
    const GLfloat right = ratio;
    const GLfloat bottom = -1.0f;
    const GLfloat top = 1.0f;
    const GLfloat near = 1.0f;
    const GLfloat far = 10.0f;

    Matrix::frustumM(gProjectionMatrix, 0, left, right, bottom, top, near, far);
}

JNIEXPORT void JNICALL Java_org_crender_NDKGlRender_onNdkDrawFrame(JNIEnv * env, jobject object)
{
    LOGI("step");
    renderFrame();
}