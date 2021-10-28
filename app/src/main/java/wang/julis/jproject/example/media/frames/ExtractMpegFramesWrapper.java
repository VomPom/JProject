package wang.julis.jproject.example.media.frames;

/*******************************************************
 *
 * Created by juliswang on 2021/08/20 14:43 
 *
 * Description : 
 *
 *
 *******************************************************/

/**
 * Wraps extractMpegFrames().  This is necessary because SurfaceTexture will try to use
 * the looper in the current thread if one exists, and the CTS tests create one on the
 * test thread.
 * <p>
 * The wrapper propagates exceptions thrown by the worker thread back to the caller.
 */
public class ExtractMpegFramesWrapper implements Runnable {
    private Throwable mThrowable;
    private ExtractMpegFramesCore mTest;

    private ExtractMpegFramesWrapper(ExtractMpegFramesCore test) {
        mTest = test;
    }

    @Override
    public void run() {
        try {
            mTest.extractMpegFrames();
        } catch (Throwable th) {
            mThrowable = th;
        }
    }

    /**
     * Entry point.
     */
    public static void runTest(ExtractMpegFramesCore obj) throws Throwable {
        ExtractMpegFramesWrapper wrapper = new ExtractMpegFramesWrapper(obj);
        Thread th = new Thread(wrapper, "codec test");
        th.start();
        th.join();
        if (wrapper.mThrowable != null) {
            throw wrapper.mThrowable;
        }
    }
}