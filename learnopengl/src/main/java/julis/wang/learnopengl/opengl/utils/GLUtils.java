package julis.wang.learnopengl.opengl.utils;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Copyright (C) @2023 THL A29 Limited, a Tencent company. All rights reserved.
 * <p>
 * Created by juliswang on 2023/2/23 20:18
 *
 * @Description
 */
public class GLUtils {
    public static Bitmap saveBitmap(int textureID, int width, int height) {
        int[] fbo = new int[1];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        // 创建帧缓冲对象引用
        GLES20.glGenFramebuffers(1, fbo, 0);
        // 绑定帧缓冲，可以理解为 分配内存
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);
        // 将textureID对应的纹理对象，附着在 帧缓存的 颜色附着点上
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureID, 0);

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
        pixelBuffer.order(ByteOrder.LITTLE_ENDIAN);
        pixelBuffer.rewind();

        // 从帧缓冲区 读处理后的图像像素
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        pixelBuffer.rewind();
        bitmap.copyPixelsFromBuffer(pixelBuffer);

        // 解绑 FBO和纹理id
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteFramebuffers(1, fbo, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return bitmap;
    }


}
