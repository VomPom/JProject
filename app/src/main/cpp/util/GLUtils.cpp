#include "GLUtils.h"
#include "LogUtil.h"
#include <stdlib.h>
#include <cstring>
#include <GLES2/gl2ext.h>

//
// Created by julis.wang on 2022/2/9.
//
// Description :
// OpenGL ES 的具体绘图步骤
//
// 三角形在 onSurfaceCreated() 创建，并在 onDrawFrame() 调用 draw() 方法来进行绘制
//
//       1、定义顶点坐标，坐标为 3D 坐标，每一个维度数据采用一个 float(32bit) 表示
//       2、构建顶点 Buffer，注意采用 nativeOrder()
//       3、构建颜色，使用一个 4 长度的 float 数组表示一个颜色，顺序为 RGBA
//       4、构建顶点和片段着色器
//           4.1、使用 glCreateShader(type) 构建着色器，返回着色器的 id
//           4.2、使用 glShaderSource(id, shaderCode) 导入着色器代码
//           4.3、使用 glCompileShader(id) 编译着色器
//       5、构建、链接着色器程序
//           5.1、使用 glCreateProgram() 构建着色器程序，返回程序 id
//           5.2、使用 glAttachShader(programId, shaderId) 将着色器附加到程序上
//           5.3、使用 glLinkProgram(programId) 链接着色器程序
//           5.4、链接完毕后，使用 glDeleteShader(shaderId) 删除附加过的着色器，释放空间
//       6、使用 glUseProgram(programId) 使用着色器程序
//       7、使用 glGetAttributeLocation() 和 glGetUniformLocation() 获取顶点位置和颜色句柄
//       8、使用 glVertexAttribPointer(indx, size, type, normalized, stride, ptr) 设置顶点属性指针
//               index: 顶点属性的 Location，即上面获取的句柄
//               size: 顶点属性的大小（一个顶点有多少属性）
//               type: 顶点属性的数据类型，本任务中为 float
//               normalized：若标准化，则所有数据都会被映射到 [0-1] 之间
//               stride: 步长，连续顶点属性组之间的间隔，两个相同属性之间的间隔，单位为 byte
//               ptr: Java 层，该参数为一个 Buffer 引用；native 层为数据的指针
//       9、使用 glEnableVertexAttribArray(vertexLocation) 启用顶点属性
//       10、使用 glUniform4fv(location, count, value, offset) 设置颜色值
//               location: 颜色值的句柄
//               count: 如果颜色值不是数组，那么为 1，如果是数组则为需要设置的数量
//               value: Java 层为一个 float 数组，native 层为一个指针，为需要传入的数据
//               offset: 从 offset 个偏移量开始读取传入数据
//       11、使用 glDrawArrays(mode, first, count) 绘制图形
//               mode: 指代需要绘制的基本图形：点、线、三角形
//               first：要绘制的顶点的起始位置，由于本任务中只画一次，所以从 0 开始
//               count: 要绘制的顶点数量
//       12、使用 glDisableVertexAttribArray() 关掉顶点属性
//



/**
 * 构建着色器
 * @param shaderType GL_VERTEX_SHADER->顶点着色器 GL_FRAGMENT_SHADER片段着色器
 * @param pSource
 * @return 返回着色器的 id
 */
GLuint GLUtils::LoadShader(GLenum shaderType, const char *pSource) {
    GLuint shader = 0;
    FUN_BEGIN_TIME("GLUtils::LoadShader")
        //1、创建一个顶点/片段着色器对象 GL_VERTEX_SHADER->顶点着色器 GL_FRAGMENT_SHADER片段着色器
        shader = glCreateShader(shaderType);
        if (shader) {
            // 导入着色器代码 count:着色器只付出数量，可以由多个源字符串组成，但只能由一个main函数
            glShaderSource(shader, 1, &pSource, NULL);

            // 编译着色器
            glCompileShader(shader);

            GLint compiled = 0;
            //glGetShaderiv 查询着色器是否编译成功
            glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
            if (!compiled) {
                GLint infoLen = 0;
                glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
                if (infoLen) {
                    char *buf = (char *) malloc((size_t) infoLen);
                    if (buf) {
                        glGetShaderInfoLog(shader, infoLen, NULL, buf);
                        LOGCATE("GLUtils::LoadShader Could not compile shader %d:\n%s\n", shaderType, buf);
                        free(buf);
                    }

                    // 完成着色器对象时删除 note:如果一个着色器连接到一个program对象，调用 glDeleteShader 不会立刻删除着色器对象，
                    // 而是将着色器标记为删除，当着色器不再连接到任何program对象，内存将被释放
                    glDeleteShader(shader);
                    shader = 0;
                }
            }
        }
    FUN_END_TIME("GLUtils::LoadShader")
    return shader;
}

/**
 * 创建着色器程序
 * @param pVertexShaderSource 顶点着色器脚本
 * @param pFragShaderSource 片段着色器脚本
 * @param vertexShaderHandle 顶点着色器的 id
 * @param fragShaderHandle 片段着色器的 id
 * @return 着色器程序
 */
GLuint
GLUtils::CreateProgram(const char *pVertexShaderSource, const char *pFragShaderSource,
                       GLuint &vertexShaderHandle, GLuint &fragShaderHandle) {
    GLuint program = 0;
    FUN_BEGIN_TIME("GLUtils::CreateProgram")

        vertexShaderHandle = LoadShader(GL_VERTEX_SHADER, pVertexShaderSource);
        if (!vertexShaderHandle) return program;
        fragShaderHandle = LoadShader(GL_FRAGMENT_SHADER, pFragShaderSource);
        if (!fragShaderHandle) return program;

        // 构建着色器程序，返回程序 id
        program = glCreateProgram();

        if (program) {
            // glAttachShader 连接着色器和程序
            glAttachShader(program, vertexShaderHandle);
            CheckGLError("glAttachShader");
            glAttachShader(program, fragShaderHandle);
            CheckGLError("glAttachShader");

            //链接着色器程序
            glLinkProgram(program);

            GLint linkStatus = GL_FALSE;
            glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
            glDetachShader(program, vertexShaderHandle);

            //断开附加过的着色器，释放空间
            glDeleteShader(vertexShaderHandle);
            vertexShaderHandle = 0;
            glDetachShader(program, fragShaderHandle);
            glDeleteShader(fragShaderHandle);
            fragShaderHandle = 0;

            if (linkStatus != GL_TRUE) {
                GLint bufLength = 0;

                //glGetProgramiv 查询是否programl连接成功
                glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
                if (bufLength) {
                    char *buf = (char *) malloc((size_t) bufLength);
                    if (buf) {
                        glGetProgramInfoLog(program, bufLength, NULL, buf);
                        LOGCATE("GLUtils::CreateProgram Could not link program:\n%s\n", buf);
                        free(buf);
                    }
                }
                glDeleteProgram(program);
                program = 0;
            }
        }
    FUN_END_TIME("GLUtils::CreateProgram")
    LOGCATE("GLUtils::CreateProgram program = %d", program);
    return program;
}

GLuint GLUtils::CreateProgramWithFeedback(const char *pVertexShaderSource, const char *pFragShaderSource,
                                          GLuint &vertexShaderHandle, GLuint &fragShaderHandle, GLchar const **varying,
                                          int varyingCount) {
    GLuint program = 0;
    FUN_BEGIN_TIME("GLUtils::CreateProgramWithFeedback")
        vertexShaderHandle = LoadShader(GL_VERTEX_SHADER, pVertexShaderSource);
        if (!vertexShaderHandle) return program;

        fragShaderHandle = LoadShader(GL_FRAGMENT_SHADER, pFragShaderSource);
        if (!fragShaderHandle) return program;

        program = glCreateProgram();
        if (program) {
            glAttachShader(program, vertexShaderHandle);
            CheckGLError("glAttachShader");
            glAttachShader(program, fragShaderHandle);
            CheckGLError("glAttachShader");

            //transform feedback
            glTransformFeedbackVaryings(program, varyingCount, varying, GL_INTERLEAVED_ATTRIBS);
            GO_CHECK_GL_ERROR();

            glLinkProgram(program);
            GLint linkStatus = GL_FALSE;
            glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);

            glDetachShader(program, vertexShaderHandle);
            glDeleteShader(vertexShaderHandle);
            vertexShaderHandle = 0;
            glDetachShader(program, fragShaderHandle);
            glDeleteShader(fragShaderHandle);
            fragShaderHandle = 0;
            if (linkStatus != GL_TRUE) {
                GLint bufLength = 0;
                glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
                if (bufLength) {
                    char *buf = (char *) malloc((size_t) bufLength);
                    if (buf) {
                        glGetProgramInfoLog(program, bufLength, NULL, buf);
                        LOGCATE("GLUtils::CreateProgramWithFeedback Could not link program:\n%s\n", buf);
                        free(buf);
                    }
                }
                glDeleteProgram(program);
                program = 0;
            }
        }
    FUN_END_TIME("GLUtils::CreateProgramWithFeedback")
    LOGCATE("GLUtils::CreateProgramWithFeedback program = %d", program);
    return program;
}

void GLUtils::DeleteProgram(GLuint &program) {
    LOGCATE("GLUtils::DeleteProgram");
    if (program) {
        glUseProgram(0);
        glDeleteProgram(program);
        program = 0;
    }
}

void GLUtils::CheckGLError(const char *pGLOperation) {
    for (GLint error = glGetError(); error; error = glGetError()) {
        LOGCATE("GLUtils::CheckGLError GL Operation %s() glError (0x%x)\n", pGLOperation, error);
    }

}

GLuint GLUtils::CreateProgram(const char *pVertexShaderSource, const char *pFragShaderSource) {
    GLuint vertexShaderHandle, fragShaderHandle;
    return CreateProgram(pVertexShaderSource, pFragShaderSource, vertexShaderHandle, fragShaderHandle);
}
