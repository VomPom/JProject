package wang.julis.jproject.example.media.opengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/12 15:55
 *
 * Description :
 * OpenGL ES 的具体绘图步骤
 *
 * 三角形在 onSurfaceCreated() 创建，并在 onDrawFrame() 调用 draw() 方法来进行绘制
 *
 *          1、定义顶点坐标，坐标为 3D 坐标，每一个维度数据采用一个 float(32bit) 表示
 *          2、构建顶点 Buffer，注意采用 nativeOrder()
 *          3、构建颜色，使用一个 4 长度的 float 数组表示一个颜色，顺序为 RGBA
 *          4、构建顶点和片段着色器
 *              4.1、使用 glCreateShader(type) 构建着色器，返回着色器的 id
 *              4.2、使用 glShaderSource(id, shaderCode) 导入着色器代码
 *              4.3、使用 glCompileShader(id) 编译着色器
 *          5、构建、链接着色器程序
 *              5.1、使用 glCreateProgram() 构建着色器程序，返回程序 id
 *              5.2、使用 glAttachShader(programId, shaderId) 将着色器附加到程序上
 *              5.3、使用 glLinkProgram(programId) 链接着色器程序
 *              5.4、链接完毕后，使用 glDeleteShader(shaderId) 删除附加过的着色器，释放空间
 *          6、使用 glUseProgram(programId) 使用着色器程序
 *          7、使用 glGetAttributeLocation() 和 glGetUniformLocation() 获取顶点位置和颜色句柄
 *          8、使用 glVertexAttribPointer(indx, size, type, normalized, stride, ptr) 设置顶点属性指针
 *                  index: 顶点属性的 Location，即上面获取的句柄
 *                  size: 顶点属性的大小（一个顶点有多少属性）
 *                  type: 顶点属性的数据类型，本任务中为 float
 *                  normalized：若标准化，则所有数据都会被映射到 [0-1] 之间
 *                  stride: 步长，连续顶点属性组之间的间隔，两个相同属性之间的间隔，单位为 byte
 *                  ptr: Java 层，该参数为一个 Buffer 引用；native 层为数据的指针
 *          9、使用 glEnableVertexAttrbArray(vertexLocation) 启用顶点属性
 *          10、使用 glUniform4fv(location, count, value, offset) 设置颜色值
 *                  location: 颜色值的句柄
 *                  count: 如果颜色值不是数组，那么为 1，如果是数组则为需要设置的数量
 *                  value: Java 层为一个 float 数组，native 层为一个指针，为需要传入的数据
 *                  offset: 从 offset 个偏移量开始读取传入数据
 *          11、使用 glDrawArrays(mode, first, count) 绘制图形
 *                  mode: 指代需要绘制的基本图形：点、线、三角形
 *                  first：要绘制的顶点的起始位置，由于本任务中只画一次，所以从 0 开始
 *                  count: 要绘制的顶点数量
 *          12、使用 glDisableVertexAttribArray() 关掉顶点属性
 *
 * History   :
 *
 *******************************************************/


public class GLTriangle extends BaseGLSL {
    // 简单的顶点着色器
    public static final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    " void main() {\n" +
                    "     gl_Position   = vPosition;\n" +
                    " }";

    // 简单的片元着色器
    public static final String fragmentShaderCode =
            " precision mediump float;\n" +
                    " uniform vec4 vColor;\n" +
                    " void main() {\n" +
                    "     gl_FragColor = vColor;\n" +
                    " }";

    // 定义三角形的坐标
    public static float triangleCoords[] = {
            0.0f, 0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // 定义三角形的颜色——白色
    public static float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    // 顶点个数
    public static final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    FloatBuffer vertexBuffer;

    int mProgram;

    public GLTriangle() {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); // 申请底色空间
        //为存放形状的坐标，初始化顶点字节缓冲
        ByteBuffer bb = ByteBuffer.allocateDirect(GLTriangle.triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(GLTriangle.triangleCoords);
        vertexBuffer.position(0);

        mProgram = createOpenGLProgram(vertexShaderCode, fragmentShaderCode);
    }

    public void draw() {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram);
        //获取顶点着色器的vPosition成员句柄
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, GLTriangle.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, GLTriangle.vertexStride, vertexBuffer);
        //获取片元着色器的vColor成员的句柄
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, GLTriangle.color, 0);
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, GLTriangle.vertexCount);
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
