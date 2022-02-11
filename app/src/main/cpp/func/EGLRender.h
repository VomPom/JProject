//
// Created by ByteFlow on 2019/7/17.
//

#ifndef NDK_OPENGLES_3_0_BGRENDER_H
#define NDK_OPENGLES_3_0_BGRENDER_H

#include "stdint.h"
#include "../util/ImageDef.h"
#include <GLES3/gl3.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#define EGL_FEATURE_NUM 8

class EGLRender {
public:
    EGLRender();

    ~EGLRender();

    void Init();

    int CreateGlesEnv();

    //void CreateProgramWithFeedback(const char *vShaderStr, const char *fShaderStr);

    void SetImageData(uint8_t *pData, int width, int height);

    void SetIntParams(int paramType, int param);

    void Draw();

    void DestroyGlesEnv();

    void UnInit();

    static EGLRender *GetInstance() {
        if (m_Instance == nullptr) {
            m_Instance = new EGLRender();
        }

        return m_Instance;
    }

    static void DestroyInstance() {
        if (m_Instance) {
            delete m_Instance;
            m_Instance = nullptr;
        }

    }

private:
    static EGLRender *m_Instance;
    GLuint m_ImageTextureId;
    GLuint m_FboTextureId;
    GLuint m_FboId;
    GLuint m_VaoIds[1] = {GL_NONE};;
    GLuint m_VboIds[3] = {GL_NONE};;
    GLint m_SamplerLoc;
    GLint m_TexSizeLoc;
    NativeImage m_RenderImage;
    GLuint m_ProgramObj;
    GLuint m_VertexShader;
    GLuint m_FragmentShader;

    EGLConfig m_eglConf;
    EGLSurface m_eglSurface;
    EGLContext m_eglCtx;
    EGLDisplay m_eglDisplay;
    bool m_IsGLContextReady;
    const char *m_fShaderStrs[EGL_FEATURE_NUM];
    int m_ShaderIndex;
};


//顶点坐标
const GLfloat vVertices[] = {
        -1.0f, -1.0f, 0.0f, // bottom left
        1.0f, -1.0f, 0.0f, // bottom right
        -1.0f, 1.0f, 0.0f, // top left
        1.0f, 1.0f, 0.0f, // top right
};

//正常纹理坐标
const GLfloat vTexCoors[] = {
        0.0f, 1.0f, // bottom left
        1.0f, 1.0f, // bottom right
        0.0f, 0.0f, // top left
        1.0f, 0.0f, // top right
};

//fbo 纹理坐标与正常纹理方向不同(上下镜像)
const GLfloat vFboTexCoors[] = {
        0.0f, 0.0f,  // bottom left
        1.0f, 0.0f,  // bottom right
        0.0f, 1.0f,  // top left
        1.0f, 1.0f,  // top right
};

const GLushort indices[] = {0, 1, 2, 1, 3, 2};

const char vShaderStr[] =
        "#version 300 es                            \n"
        "layout(location = 0) in vec4 a_position;   \n"
        "layout(location = 1) in vec2 a_texCoord;   \n"
        "out vec2 v_texCoord;                       \n"
        "void main()                                \n"
        "{                                          \n"
        "   gl_Position = a_position;               \n"
        "   v_texCoord = a_texCoord;                \n"
        "}                                          \n";

const char fShaderStr0[] =
        "#version 300 es\n"
        "precision mediump float;\n"
        "in vec2 v_texCoord;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "uniform sampler2D s_TextureMap;\n"
        "void main()\n"
        "{\n"
        "    outColor = texture(s_TextureMap, v_texCoord);\n"
        "}";
// 马赛克
const char fShaderStr1[] =
        "#version 300 es\n"
        "precision highp float;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "in vec2 v_texCoord;\n"
        "uniform sampler2D s_TextureMap;\n"
        "uniform vec2 u_texSize;\n"
        "\n"
        "vec4 CrossStitching(vec2 uv) {\n"
        "    float stitchSize = u_texSize.x / 35.0;\n"
        "    int invert = 0;\n"
        "    vec4 color = vec4(0.0);\n"
        "    float size = stitchSize;\n"
        "    vec2 cPos = uv * u_texSize.xy;\n"
        "    vec2 tlPos = floor(cPos / vec2(size, size));\n"
        "    tlPos *= size;\n"
        "    int remX = int(mod(cPos.x, size));\n"
        "    int remY = int(mod(cPos.y, size));\n"
        "    if (remX == 0 && remY == 0)\n"
        "    tlPos = cPos;\n"
        "    vec2 blPos = tlPos;\n"
        "    blPos.y += (size - 1.0);\n"
        "    if ((remX == remY) || (((int(cPos.x) - int(blPos.x)) == (int(blPos.y) - int(cPos.y))))) {\n"
        "        if (invert == 1)\n"
        "        color = vec4(0.2, 0.15, 0.05, 1.0);\n"
        "        else\n"
        "        color = texture(s_TextureMap, tlPos * vec2(1.0 / u_texSize.x, 1.0 / u_texSize.y)) * 1.4;\n"
        "    } else {\n"
        "        if (invert == 1)\n"
        "        color = texture(s_TextureMap, tlPos * vec2(1.0 / u_texSize.x, 1.0 / u_texSize.y)) * 1.4;\n"
        "        else\n"
        "        color = vec4(0.0, 0.0, 0.0, 1.0);\n"
        "    }\n"
        "    return color;\n"
        "}\n"
        "void main() {\n"
        "    outColor = CrossStitching(v_texCoord);\n"
        "}";
// 网格
const char fShaderStr2[] =
        "#version 300 es\n"
        "precision highp float;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "in vec2 v_texCoord;\n"
        "uniform sampler2D s_TextureMap;\n"
        "uniform vec2 u_texSize;\n"
        "void main() {\n"
        "    float size = u_texSize.x / 1.0;\n"
        "    float radius = size * 0.5;\n"
        "    vec2 fragCoord = v_texCoord * u_texSize.xy;\n"
        "    vec2 quadPos = floor(fragCoord.xy / size) * size;\n"
        "    vec2 quad = quadPos/u_texSize.xy;\n"
        "    vec2 quadCenter = (quadPos + size/2.0);\n"
        "    float dist = length(quadCenter - fragCoord.xy);\n"
        "\n"
        "    if (dist > radius) {\n"
        "        outColor = vec4(1.0,1.0,0.0,1.0);\n"
        "    } else {\n"
        "        outColor = texture(s_TextureMap, v_texCoord);\n"
        "    }\n"
        "}";

// 旋转
const char fShaderStr3[] =
        "#version 300 es\n"
        "precision highp float;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "in vec2 v_texCoord;\n"
        "uniform lowp sampler2D s_TextureMap;\n"
        "uniform vec2 u_texSize;\n"
        "void main() {\n"
        "    float radius = 400.0;\n"
        "    float angle = 0.2;\n"
        "    vec2 center = vec2(u_texSize.x / 2.0, u_texSize.y / 2.0);\n"
        "    vec2 tc = v_texCoord * u_texSize;\n"
        "    tc -= center;\n"
        "    float dist = length(tc);\n"
        "    if (dist < radius) {\n"
        "        float percent = (radius - dist) / radius;\n"
        "        float theta = percent * percent * angle * 8.0;\n"
        "        float s = sin(theta);\n"
        "        float c = cos(theta);\n"
        "        tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));\n"
        "    }\n"
        "    tc += center;\n"
        "    outColor = texture(s_TextureMap, tc / u_texSize);\n"
        "}";

// 边缘
const char fShaderStr4[] =
        "#version 300 es\n"
        "precision highp float;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "in vec2 v_texCoord;\n"
        "uniform lowp sampler2D s_TextureMap;\n"
        "uniform vec2 u_texSize;\n"
        "void main() {\n"
        "    vec2 pos = v_texCoord.xy;\n"
        "    vec2 onePixel = vec2(1, 1) / u_texSize;\n"
        "    vec4 color = vec4(0);\n"
        "    mat3 edgeDetectionKernel = mat3(\n"
        "    -1, -1, -1,\n"
        "    -1, 8, -1,\n"
        "    -1, -1, -1\n"
        "    );\n"
        "    for(int i = 0; i < 3; i++) {\n"
        "        for(int j = 0; j < 3; j++) {\n"
        "            vec2 samplePos = pos + vec2(i - 1 , j - 1) * onePixel;\n"
        "            vec4 sampleColor = texture(s_TextureMap, samplePos);\n"
        "            sampleColor *= edgeDetectionKernel[i][j];\n"
        "            color += sampleColor;\n"
        "        }\n"
        "    }\n"
        "    outColor = vec4(color.rgb, 1.0);\n"
        "}";
// 放大
const char fShaderStr5[] =
        "#version 300 es\n"
        "precision highp float;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "in vec2 v_texCoord;\n"
        "uniform sampler2D s_TextureMap;\n"
        "uniform vec2 u_texSize;\n"
        "\n"
        "vec2 warpPositionToUse(vec2 centerPostion, vec2 currentPosition, float radius, float scaleRatio, float aspectRatio)\n"
        "{\n"
        "    vec2 positionToUse = currentPosition;\n"
        "    vec2 currentPositionToUse = vec2(currentPosition.x, currentPosition.y * aspectRatio + 0.5 - 0.5 * aspectRatio);\n"
        "    vec2 centerPostionToUse = vec2(centerPostion.x, centerPostion.y * aspectRatio + 0.5 - 0.5 * aspectRatio);\n"
        "    //float r = distance(currentPositionToUse, centerPostionToUse);\n"
        "    float r = distance(currentPosition, centerPostion);\n"
        "    if(r < radius)\n"
        "    {\n"
        "        float alpha = 1.0 - scaleRatio * pow(r / radius - 1.0, 2.0);\n"
        "        positionToUse = centerPostion + alpha * (currentPosition - centerPostion);\n"
        "    }\n"
        "    return positionToUse;\n"
        "}\n"
        "\n"
        "void main() {\n"
        "    vec2 centerPostion = vec2(0.5, 0.5);\n"
        "    float radius = 0.34;\n"
        "    float scaleRatio = 1.0;\n"
        "    float aspectRatio = u_texSize.x / u_texSize.y;\n"
        "    outColor = texture(s_TextureMap, warpPositionToUse(centerPostion, v_texCoord, radius, scaleRatio, aspectRatio));\n"
        "}";
// 形变1
const char fShaderStr6[] =
        "#version 300 es\n"
        "precision highp float;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "in vec2 v_texCoord;\n"
        "uniform sampler2D s_TextureMap;\n"
        "uniform vec2 u_texSize;\n"
        "\n"
        "vec2 reshape(vec2 src, vec2 dst, vec2 curPos, float radius)\n"
        "{\n"
        "    vec2 pos = curPos;\n"
        "\n"
        "    float r = distance(curPos, src);\n"
        "\n"
        "    if (r < radius)\n"
        "    {\n"
        "        float alpha = 1.0 -  r / radius;\n"
        "        vec2 displacementVec = (dst - src) * pow(alpha, 2.0);\n"
        "        pos = curPos - displacementVec;\n"
        "\n"
        "    }\n"
        "    return pos;\n"
        "}\n"
        "\n"
        "void main() {\n"
        "    vec2 srcPos = vec2(0.5, 0.5);\n"
        "    vec2 dstPos = vec2(0.6, 0.5);\n"
        "    float radius = 0.18;\n"
        "    float scaleRatio = 1.0;\n"
        "    float aspectRatio = u_texSize.x / u_texSize.y;\n"
        "    \n"
        "    if(radius <= distance(v_texCoord, srcPos) && distance(v_texCoord, srcPos) <= radius + 0.008)\n"
        "    {\n"
        "        outColor = vec4(1.0, 1.0, 1.0, 1.0);\n"
        "    } \n"
        "    else\n"
        "    {\n"
        "        outColor = texture(s_TextureMap, reshape(srcPos, dstPos, v_texCoord, radius));\n"
        "    }\n"
        "}";

// 形变2
const char fShaderStr7[] =
        "#version 300 es\n"
        "precision highp float;\n"
        "layout(location = 0) out vec4 outColor;\n"
        "in vec2 v_texCoord;\n"
        "uniform sampler2D s_TextureMap;\n"
        "uniform vec2 u_texSize;\n"
        "\n"
        "float distanceTex(vec2 pos0, vec2 pos1, float aspectRatio)\n"
        "{\n"
        "    vec2 newPos0 = vec2(pos0.x, pos0.y * aspectRatio + 0.5 - 0.5 * aspectRatio);\n"
        "    vec2 newPos1 = vec2(pos1.x, pos1.y * aspectRatio + 0.5 - 0.5 * aspectRatio);\n"
        "    return distance(newPos0, newPos1);\n"
        "}\n"
        "\n"
        "vec2 reshape(vec2 src, vec2 dst, vec2 curPos, float radius, float aspectRatio)\n"
        "{\n"
        "    vec2 pos = curPos;\n"
        "\n"
        "    vec2 newSrc = vec2(src.x, src.y * aspectRatio + 0.5 - 0.5 * aspectRatio);\n"
        "    vec2 newDst = vec2(dst.x, dst.y * aspectRatio + 0.5 - 0.5 * aspectRatio);\n"
        "    vec2 newCur = vec2(curPos.x, curPos.y * aspectRatio + 0.5 - 0.5 * aspectRatio);\n"
        "\n"
        "\n"
        "    float r = distance(newSrc, newCur);\n"
        "\n"
        "    if (r < radius)\n"
        "    {\n"
        "        float alpha = 1.0 -  r / radius;\n"
        "        vec2 displacementVec = (dst - src) * pow(alpha, 1.7);\n"
        "        pos = curPos - displacementVec;\n"
        "\n"
        "    }\n"
        "    return pos;\n"
        "}\n"
        "\n"
        "void main() {\n"
        "    vec2 srcPos = vec2(0.5, 0.5);\n"
        "    vec2 dstPos = vec2(0.55, 0.55);\n"
        "    float radius = 0.30;\n"
        "    float scaleRatio = 1.0;\n"
        "    float aspectRatio = u_texSize.y/u_texSize.x;\n"
        "\n"
        "    if(radius <= distanceTex(v_texCoord, srcPos, aspectRatio) && distanceTex(v_texCoord, srcPos, aspectRatio) <= radius + 0.008)\n"
        "    {\n"
        "        outColor = vec4(1.0, 1.0, 1.0, 1.0);\n"
        "    }\n"
        "    else\n"
        "    {\n"
        "        outColor = texture(s_TextureMap, reshape(srcPos, dstPos, v_texCoord, radius, aspectRatio));\n"
        "    }\n"
        "}";
#endif //NDK_OPENGLES_3_0_BGRENDER_H
