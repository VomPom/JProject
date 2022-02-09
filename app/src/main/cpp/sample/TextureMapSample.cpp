//
// Created by ByteFlow on 2019/7/10.
//


#include "TextureMapSample.h"
#include "../util/GLUtils.h"

//    纹理绘制步骤
//    1、生成纹理 glGenTextures(1, textures, 0)
//    2、载入图片
//        Android 中采用 Bitmap 载入图片，然后使用 copyPixelsToBuffer() 复制图片数据到缓冲区
//    3、激活纹理 glActivateTexture(GL_TEXTUREn)
//        OpenGL 允许绑定十六种纹理，同时 GL_TEXTURE0 是默认激活的，如果只使用一种纹理，那可以省略这个的调用
//    4、绑定纹理 glBindTexture(target, texture)
//        target: GL_TEXTURE_1D GL_TEXTURE_2D GL_TEXTURE_3D
//        texture：上面生成的纹理的索引值
//    5、设置纹理属性
//        包括环绕方式和过滤方式
//        环绕方式可以在几个轴分别设置
//        过滤方式可以在放大和缩小的时候分别设置
//    6、导入图片数据 glTexImage2D(target, level, internalFormat, width, height, border, format, type, buffer)
//        target: GL_TEXTURE_1D GL_TEXTURE_2D GL_TEXTURE_3D
//        level: 多级渐远的等级
//        internalformat: 纹理的存储格式，这里采用 GL_RGBA
//        border: 总为 0
//        format：源图的存储格式，bitmap 使用 GL_RGBA
//        type：源图数据的数据类型，这里采用 GL_UNSIGNED_BYTE
//        buffer：图片数据的 Buffer
//        、导入完毕之后调用 bitmap.recycle() 回收资源
//    7、设置 uniform sampler2D ourTexture
//        使用 glUnifrom1i() 对 ourTexture 进行设定，其值应该和你绑定的 GL_TEXTUREn 的 n 相同
//        由于默认值为 0，为我们默认激活的 GL_TEXTURE0，因此如果只使用一种纹理，那么可以跳过这一步
//    8、调用 glDrawxxx() 方法进行绘制，这里采用了 EBO 的 glDrawElements() 进行绘制

TextureMapSample::TextureMapSample() {
    m_TextureId = 0;
}

TextureMapSample::~TextureMapSample() {
    NativeImageUtil::FreeNativeImage(&m_RenderImage);
}

void TextureMapSample::Init() {
    //create RGBA texture
    glGenTextures(1, &m_TextureId);

    //将纹理 m_TextureId 绑定到类型 GL_TEXTURE_2D 纹理
    glBindTexture(GL_TEXTURE_2D, m_TextureId);

    //设置纹理 S 轴（横轴）的拉伸方式为截取
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    //设置纹理 T 轴（纵轴）的拉伸方式为截取
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    //设置纹理采样方式
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glBindTexture(GL_TEXTURE_2D, GL_NONE);

    char vShaderStr[] =
            "#version 300 es                            \n"
            "layout(location = 0) in vec4 a_position;   \n"
            "layout(location = 1) in vec2 a_texCoord;   \n"
            "out vec2 v_texCoord;                       \n"
            "void main()                                \n"
            "{                                          \n"
            "   gl_Position = a_position;               \n"
            "   v_texCoord = a_texCoord;                \n"
            "}                                          \n";

    char fShaderStr[] =
            "#version 300 es                                     \n"
            "precision mediump float;                            \n"
            "in vec2 v_texCoord;                                 \n"
            "layout(location = 0) out vec4 outColor;             \n"
            "uniform sampler2D s_TextureMap;                     \n"
            "void main()                                         \n"
            "{                                                   \n"
            "  outColor = texture(s_TextureMap, v_texCoord);     \n"
            "  //outColor = texelFetch(s_TextureMap,  ivec2(int(v_texCoord.x * 404.0), int(v_texCoord.y * 336.0)), 0);\n"
            "}                                                   \n";

    m_ProgramObj = GLUtils::CreateProgram(vShaderStr, fShaderStr, m_VertexShader, m_FragmentShader);
    if (m_ProgramObj) {
        m_SamplerLoc = glGetUniformLocation(m_ProgramObj, "s_TextureMap");
    } else {
        LOGCATE("TextureMapSample::Init create program fail");
    }

}

void TextureMapSample::Draw(int screenW, int screenH) {
    LOGCATE("TextureMapSample::Draw()");

    if (m_ProgramObj == GL_NONE || m_TextureId == GL_NONE) return;

    glClear(GL_STENCIL_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glClearColor(1.0, 1.0, 1.0, 1.0);

    GLfloat verticesCoords[] = {
            -1.0f, 0.5f, 0.0f,  // Position 0
            -1.0f, -0.5f, 0.0f,  // Position 1
            1.0f, -0.5f, 0.0f,   // Position 2
            1.0f, 0.5f, 0.0f,   // Position 3
    };

    GLfloat textureCoords[] = {
            0.0f, 0.0f,        // TexCoord 0
            0.0f, 1.0f,        // TexCoord 1
            1.0f, 1.0f,        // TexCoord 2
            1.0f, 0.0f         // TexCoord 3
    };

    GLushort indices[] = {0, 1, 2, 0, 2, 3};

    //upload RGBA image data
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, m_TextureId);
    //加载 RGBA 格式的图像数据
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, m_RenderImage.width, m_RenderImage.height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                 m_RenderImage.ppPlane[0]);
    glBindTexture(GL_TEXTURE_2D, GL_NONE);

    // Use the program object
    glUseProgram(m_ProgramObj);

    // Load the vertex position
    glVertexAttribPointer(0, 3, GL_FLOAT,
                          GL_FALSE, 3 * sizeof(GLfloat), verticesCoords);
    // Load the texture coordinate
    glVertexAttribPointer(1, 2, GL_FLOAT,
                          GL_FALSE, 2 * sizeof(GLfloat), textureCoords);

    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    // Bind the RGBA map
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, m_TextureId);

    // Set the RGBA map sampler to texture unit to 0
    glUniform1i(m_SamplerLoc, 0);

    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indices);
}

void TextureMapSample::LoadImage(NativeImage *pImage) {
    LOGCATE("TextureMapSample::LoadImage pImage = %p pImage->width:%d pImage->height:%d", pImage->ppPlane[0],
            pImage->width, pImage->height);
    if (pImage) {
        m_RenderImage.width = pImage->width;
        m_RenderImage.height = pImage->height;
        m_RenderImage.format = pImage->format;
        NativeImageUtil::CopyNativeImage(pImage, &m_RenderImage);
    }
}

void TextureMapSample::Destroy() {
    if (m_ProgramObj) {
        glDeleteProgram(m_ProgramObj);
        glDeleteTextures(1, &m_TextureId);
        m_ProgramObj = GL_NONE;
    }

}
