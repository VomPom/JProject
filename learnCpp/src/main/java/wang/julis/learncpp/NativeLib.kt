package wang.julis.learncpp

class NativeLib {

    /**
     * A native method that is implemented by the 'learncpp' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'learncpp' library on application startup.
        init {
            System.loadLibrary("learncpp")
        }
    }
}