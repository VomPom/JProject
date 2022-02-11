//
// Created by julis.wang on 2022/2/11.
//

#include "TriangleSample.h"
#include "../util/GLUtils.h"
#include "../util/LogUtil.h"

TriangleSample::TriangleSample() = default;

TriangleSample::~TriangleSample() = default;


void TriangleSample::LoadImage(NativeImage *pImage) {
    //null implement
}

void TriangleSample::Init() {
    if (m_ProgramObj != 0)
        return;
    char vShaderStr[] =
            "#version 300 es                          \n"
            "layout(location = 0) in vec4 vPosition;  \n"
            "void main()                              \n"
            "{                                        \n"
            "   gl_Position = vPosition;              \n"
            "}                                        \n";

    char fShaderStr[] =
            "#version 300 es                              \n"
            "precision mediump float;                     \n"
            "out vec4 fragColor;                          \n"
            "void main()                                  \n"
            "{                                            \n"
            "   fragColor = vec4 ( 1.0, 0.0, 0.0, 0.0 );  \n"
            "}                                            \n";

    m_ProgramObj = GLUtils::CreateProgram(vShaderStr, fShaderStr, m_VertexShader, m_FragmentShader);

}

void TriangleSample::Draw(int screenW, int screenH) {
    LOGCATE("TriangleSample::Draw");
    GLfloat vVertices[] = {
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };

    if (m_ProgramObj == 0)
        return;

    glClear(GL_STENCIL_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glClearColor(1.0, 1.0, 1.0, 1.0);

    // Use the program object
    glUseProgram(m_ProgramObj);

    // Load the vertex data
    //       8、使用 glVertexAttribPointer(indx, size, type, normalized, stride, ptr) 设置顶点属性指针
    //          index: 顶点属性的 Location，即上面获取的句柄
    //          size: 顶点属性的大小（一个顶点有多少属性）
    //          type: 顶点属性的数据类型，本任务中为 float
    //          normalized：若标准化，则所有数据都会被映射到 [0-1] 之间
    //          stride: 步长，连续顶点属性组之间的间隔，两个相同属性之间的间隔，单位为 byte
    //          ptr: Java 层，该参数为一个 Buffer 引用；native 层为数据的指针
    //
    //  这里的 index =0 与上面 "layout(location = 0) in vec4 vPosition" 的location=0 对应，或者用下面的方式：
    //
    //    int location = glGetAttribLocation(m_ProgramObj, "vPosition");
    //    glVertexAttribPointer(location, 3, GL_FLOAT, GL_FALSE, 0, vVertices);
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vVertices);


    // 启用顶点属性
    glEnableVertexAttribArray(0);

    //       11、使用 glDrawArrays(mode, first, count) 绘制图形
    //           mode: 指代需要绘制的基本图形：点、线、三角形
    //           first：要绘制的顶点的起始位置，由于本任务中只画一次，所以从 0 开始
    //           count: 要绘制的顶点数量
    //       12、使用 glDisableVertexAttribArray() 关掉顶点属性
    glDrawArrays(GL_TRIANGLES, 0, 6);
    glUseProgram(GL_NONE);
    //    glDisableVertexAttribArray(0);


}

void TriangleSample::Destroy() {
    if (m_ProgramObj) {
        glDeleteProgram(m_ProgramObj);
        m_ProgramObj = GL_NONE;
    }

}
