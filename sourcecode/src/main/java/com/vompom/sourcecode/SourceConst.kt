package com.vompom.sourcecode

/**
 *
 * Created by @juliswang on 2025/03/20 10:32
 *
 * @Description
 */
object SourceConst {
    object Image {
        val data = listOf(
            "https://i.loli.net/2021/04/14/nNly8EdXJ2aHYTe.jpg",
            "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg",
            "https://fuss10.elemecdn.com/8/27/f01c15bb73e1ef3793e64e6b7bbccjpeg.jpeg",
            "https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg",
        )

        fun random(): String = data.random()

    }

    object Video {

    }
}