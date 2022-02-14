//
// Created by ByteFlow on 2019/7/17.
//

#include "EGLRender.h"
#include "../util/ImageDef.h"
#include "../util/GLUtils.h"

#define VERTEX_POS_LOC  0
#define TEXTURE_POS_LOC 1


//什么是 EGL？
//EGL 是 OpenGL ES 和本地窗口系统（Native Window System）之间的通信接口，它的主要作用：
//
//  1、与设备的原生窗口系统通信；
//  2、查询绘图表面的可用类型和配置；
//  3、创建绘图表面；
//  4、在OpenGL ES 和其他图形渲染API之间同步渲染；
//  5、管理纹理贴图等渲染资源。

EGLRender *EGLRender::m_Instance = nullptr;
#define PARAM_TYPE_SHADER_INDEX    200

EGLRender::EGLRender() {
    m_ImageTextureId = GL_NONE;
    m_FboTextureId = GL_NONE;
    m_SamplerLoc = GL_NONE;
    m_TexSizeLoc = GL_NONE;
    m_FboId = GL_NONE;
    m_ProgramObj = GL_NONE;
    m_VertexShader = GL_NONE;
    m_FragmentShader = GL_NONE;

    m_IsGLContextReady = false;
    m_ShaderIndex = 0;
}

EGLRender::~EGLRender() {
    if (m_RenderImage.ppPlane[0]) {
        NativeImageUtil::FreeNativeImage(&m_RenderImage);
        m_RenderImage.ppPlane[0] = nullptr;
    }
}

void EGLRender::Init() {
    LOGCATE("EGLRender::Init");
    if (CreateGlesEnv() == 0) {
        m_IsGLContextReady = true;
    }

    if (!m_IsGLContextReady) return;
    m_fShaderStrs[0] = fShaderStr0;
    m_fShaderStrs[1] = fShaderStr1;
    m_fShaderStrs[2] = fShaderStr2;
    m_fShaderStrs[3] = fShaderStr3;
    m_fShaderStrs[4] = fShaderStr4;
    m_fShaderStrs[5] = fShaderStr5;
    m_fShaderStrs[6] = fShaderStr6;
    m_fShaderStrs[7] = fShaderStr7;


    glGenTextures(1, &m_ImageTextureId);
    glBindTexture(GL_TEXTURE_2D, m_ImageTextureId);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glBindTexture(GL_TEXTURE_2D, GL_NONE);

    glGenTextures(1, &m_FboTextureId);
    glBindTexture(GL_TEXTURE_2D, m_FboTextureId);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glBindTexture(GL_TEXTURE_2D, GL_NONE);


    m_ProgramObj = GLUtils::CreateProgram(vShaderStr, m_fShaderStrs[m_ShaderIndex], m_VertexShader, m_FragmentShader);
    if (!m_ProgramObj) {
        GLUtils::CheckGLError("Create Program");
        LOGCATE("EGLRender::Init Could not create program.");
        return;
    }

    m_SamplerLoc = glGetUniformLocation(m_ProgramObj, "s_TextureMap");
    m_TexSizeLoc = glGetUniformLocation(m_ProgramObj, "u_texSize");

    // Generate VBO Ids and load the VBOs with data
    glGenBuffers(3, m_VboIds);
    glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[0]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vVertices), vVertices, GL_STATIC_DRAW);

    glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[1]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vFboTexCoors), vTexCoors, GL_STATIC_DRAW);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_VboIds[2]);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    GO_CHECK_GL_ERROR();

    // Generate VAO Ids
    glGenVertexArrays(1, m_VaoIds);

    // FBO off screen rendering VAO
    glBindVertexArray(m_VaoIds[0]);

    glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[0]);
    glEnableVertexAttribArray(VERTEX_POS_LOC);
    glVertexAttribPointer(VERTEX_POS_LOC, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), (const void *) 0);
    glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

    glBindBuffer(GL_ARRAY_BUFFER, m_VboIds[1]);
    glEnableVertexAttribArray(TEXTURE_POS_LOC);
    glVertexAttribPointer(TEXTURE_POS_LOC, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(GLfloat), (const void *) 0);
    glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_VboIds[2]);
    GO_CHECK_GL_ERROR();
    glBindVertexArray(GL_NONE);
}

int EGLRender::CreateGlesEnv() {
    // EGL config attributes
    const EGLint confAttr[] = {
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT_KHR,
            EGL_SURFACE_TYPE,
            EGL_PBUFFER_BIT,//EGL_WINDOW_BIT EGL_PBUFFER_BIT we will create a pixelbuffer surface
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,// if you need the alpha channel
            EGL_DEPTH_SIZE, 16,// if you need the depth buffer
            EGL_STENCIL_SIZE, 8,
            EGL_NONE
    };

    // EGL context attributes
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };

    // surface attributes
    // the surface size is set to the input frame size
    const EGLint surfaceAttr[] = {
            EGL_WIDTH, 1,
            EGL_HEIGHT, 1,
            EGL_NONE
    };
    EGLint eglMajVers, eglMinVers;
    EGLint numConfigs;

    int resultCode = 0;
    do {
        //1. 获取 EGLDisplay 对象，建立与本地窗口系统的连接
        m_eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (m_eglDisplay == EGL_NO_DISPLAY) {
            //Unable to open connection to local windowing system
            LOGCATE("EGLRender::CreateGlesEnv Unable to open connection to local windowing system");
            resultCode = -1;
            break;
        }

        //2. 初始化 EGL 方法
        if (!eglInitialize(m_eglDisplay, &eglMajVers, &eglMinVers)) {
            // Unable to initialize EGL. Handle and recover
            LOGCATE("EGLRender::CreateGlesEnv Unable to initialize EGL");
            resultCode = -1;
            break;
        }

        LOGCATE("EGLRender::CreateGlesEnv EGL init with version %d.%d", eglMajVers, eglMinVers);

        //3. 获取 EGLConfig 对象，确定渲染表面的配置信息
        if (!eglChooseConfig(m_eglDisplay, confAttr, &m_eglConf, 1, &numConfigs)) {
            LOGCATE("EGLRender::CreateGlesEnv some config is wrong");
            resultCode = -1;
            break;
        }
        //4. 创建渲染表面 EGLSurface, 使用 eglCreatePbufferSurface 创建屏幕外渲染区域
        m_eglSurface = eglCreatePbufferSurface(m_eglDisplay, m_eglConf, surfaceAttr);
        if (m_eglSurface == EGL_NO_SURFACE) {
            switch (eglGetError()) {
                case EGL_BAD_ALLOC:
                    // Not enough resources available. Handle and recover
                    LOGCATE("EGLRender::CreateGlesEnv Not enough resources available");
                    break;
                case EGL_BAD_CONFIG:
                    // Verify that provided EGLConfig is valid
                    LOGCATE("EGLRender::CreateGlesEnv provided EGLConfig is invalid");
                    break;
                case EGL_BAD_PARAMETER:
                    // Verify that the EGL_WIDTH and EGL_HEIGHT are
                    // non-negative values
                    LOGCATE("EGLRender::CreateGlesEnv provided EGL_WIDTH and EGL_HEIGHT is invalid");
                    break;
                case EGL_BAD_MATCH:
                    // Check window and EGLConfig attributes to determine
                    // compatibility and pbuffer-texture parameters
                    LOGCATE("EGLRender::CreateGlesEnv Check window and EGLConfig attributes");
                    break;
            }
        }

        //5. 创建渲染上下文 EGLContext
        m_eglCtx = eglCreateContext(m_eglDisplay, m_eglConf, EGL_NO_CONTEXT, ctxAttr);
        if (m_eglCtx == EGL_NO_CONTEXT) {
            EGLint error = eglGetError();
            if (error == EGL_BAD_CONFIG) {
                // Handle error and recover
                LOGCATE("EGLRender::CreateGlesEnv EGL_BAD_CONFIG");
                resultCode = -1;
                break;
            }
        }

        //6. 绑定上下文
        if (!eglMakeCurrent(m_eglDisplay, m_eglSurface, m_eglSurface, m_eglCtx)) {
            LOGCATE("EGLRender::CreateGlesEnv MakeCurrent failed");
            resultCode = -1;
            break;
        }
        LOGCATE("EGLRender::CreateGlesEnv initialize success!");
    } while (false);

    if (resultCode != 0) {
        LOGCATE("EGLRender::CreateGlesEnv fail");
    }

    return resultCode;
}

void EGLRender::SetImageData(uint8_t *pData, int width, int height) {
    LOGCATE("EGLRender::SetImageData pData = %p, [w,h] = [%d, %d]", pData, width, height);

    if (pData && m_IsGLContextReady) {
        if (m_RenderImage.ppPlane[0]) {
            NativeImageUtil::FreeNativeImage(&m_RenderImage);
            m_RenderImage.ppPlane[0] = nullptr;
        }

        m_RenderImage.width = width;
        m_RenderImage.height = height;
        m_RenderImage.format = IMAGE_FORMAT_RGBA;
        NativeImageUtil::AllocNativeImage(&m_RenderImage);
        memcpy(m_RenderImage.ppPlane[0], pData, static_cast<size_t>(width * height * 4));

        glBindTexture(GL_TEXTURE_2D, m_ImageTextureId);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, m_RenderImage.width, m_RenderImage.height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                     m_RenderImage.ppPlane[0]);
        glBindTexture(GL_TEXTURE_2D, GL_NONE);

        if (m_FboId == GL_NONE) {
            // Create FBO
            glGenFramebuffers(1, &m_FboId);
            glBindFramebuffer(GL_FRAMEBUFFER, m_FboId);
            glBindTexture(GL_TEXTURE_2D, m_FboTextureId);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, m_FboTextureId, 0);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, m_RenderImage.width, m_RenderImage.height, 0, GL_RGBA,
                         GL_UNSIGNED_BYTE, nullptr);
            if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
                LOGCATE("EGLRender::SetImageData glCheckFramebufferStatus status != GL_FRAMEBUFFER_COMPLETE");
            }
            glBindTexture(GL_TEXTURE_2D, GL_NONE);
            glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);
        }

    }

}

void EGLRender::SetIntParams(int paramType, int param) {
    LOGCATE("EGLRender::SetIntParams paramType = %d, param = %d", paramType, param);
    switch (paramType) {
        case PARAM_TYPE_SHADER_INDEX: {
            if (param >= 0) {
                m_ShaderIndex = param % EGL_FEATURE_NUM;

                if (m_ProgramObj) {
                    glDeleteProgram(m_ProgramObj);
                    m_ProgramObj = GL_NONE;
                }

                m_ProgramObj = GLUtils::CreateProgram(vShaderStr, m_fShaderStrs[m_ShaderIndex], m_VertexShader,
                                                      m_FragmentShader);
                if (!m_ProgramObj) {
                    GLUtils::CheckGLError("Create Program");
                    LOGCATE("EGLRender::SetIntParams Could not create program.");
                    return;
                }

                m_SamplerLoc = glGetUniformLocation(m_ProgramObj, "s_TextureMap");
                GO_CHECK_GL_ERROR();
                m_TexSizeLoc = glGetUniformLocation(m_ProgramObj, "u_texSize");
                GO_CHECK_GL_ERROR();
            }

        }
            break;
        default:
            break;
    }
}

void EGLRender::Draw() {
    LOGCATE("EGLRender::Draw");
    if (m_ProgramObj == GL_NONE) return;
    glViewport(0, 0, m_RenderImage.width, m_RenderImage.height);

    // Do FBO off screen rendering
    glUseProgram(m_ProgramObj);
    glBindFramebuffer(GL_FRAMEBUFFER, m_FboId);

    glBindVertexArray(m_VaoIds[0]);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, m_ImageTextureId);
    glUniform1i(m_SamplerLoc, 0);

    if (m_TexSizeLoc > -1) {
        GLfloat size[2];
        size[0] = m_RenderImage.width;
        size[1] = m_RenderImage.height;
        glUniform2f(m_TexSizeLoc, size[0], size[1]);
    }


    //7. 渲染
    GO_CHECK_GL_ERROR();
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, (const void *) 0);
    GO_CHECK_GL_ERROR();
    glBindVertexArray(GL_NONE);
    glBindTexture(GL_TEXTURE_2D, GL_NONE);

    //一旦解绑 FBO 后面就不能调用 readPixels
    //glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);

}

void EGLRender::UnInit() {
    LOGCATE("EGLRender::UnInit");
    if (m_ProgramObj) {
        glDeleteProgram(m_ProgramObj);
        m_ProgramObj = GL_NONE;
    }

    if (m_ImageTextureId) {
        glDeleteTextures(1, &m_ImageTextureId);
        m_ImageTextureId = GL_NONE;
    }

    if (m_FboTextureId) {
        glDeleteTextures(1, &m_FboTextureId);
        m_FboTextureId = GL_NONE;
    }

    if (m_VboIds[0]) {
        glDeleteBuffers(3, m_VboIds);
        m_VboIds[0] = GL_NONE;
        m_VboIds[1] = GL_NONE;
        m_VboIds[2] = GL_NONE;

    }

    if (m_VaoIds[0]) {
        glDeleteVertexArrays(1, m_VaoIds);
        m_VaoIds[0] = GL_NONE;
    }

    if (m_FboId) {
        glDeleteFramebuffers(1, &m_FboId);
        m_FboId = GL_NONE;
    }


    if (m_IsGLContextReady) {
        DestroyGlesEnv();
        m_IsGLContextReady = false;
    }

}

void EGLRender::DestroyGlesEnv() {
    //8. 释放 EGL 环境
    if (m_eglDisplay != EGL_NO_DISPLAY) {
        eglMakeCurrent(m_eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        eglDestroyContext(m_eglDisplay, m_eglCtx);
        eglDestroySurface(m_eglDisplay, m_eglSurface);
        eglReleaseThread();
        eglTerminate(m_eglDisplay);
    }

    m_eglDisplay = EGL_NO_DISPLAY;
    m_eglSurface = EGL_NO_SURFACE;
    m_eglCtx = EGL_NO_CONTEXT;

}


