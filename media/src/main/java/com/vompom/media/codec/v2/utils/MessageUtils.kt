package com.vompom.media.codec.v2.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.MessageQueue
import com.vompom.media.codec.v2.docode.model.PlayerMessage

/**
 *
 * Created by @juliswang on 2025/10/15 14:38
 *
 * @Description
 */

object MessageUtils {

    @SuppressLint("DiscouragedPrivateApi")
    fun getMessageByWhat(what: Int, handler: Handler): List<Message> {
        var message = getMessage(handler)

        val targetMessageList = ArrayList<Message>()

        while (message != null) {
            if (message.what == what && (PlayerMessage::class.java.isInstance(message.obj))) {
                targetMessageList.add(message)
            } else if (message.target === handler) {
                // 如果有其他事件导致seek事件非连续，中断本次查询
                break
            }
            val f = Message::class.java.getDeclaredField("next")
            f.isAccessible = true
            message = (f.get(message) as Message?)
        }
        return targetMessageList.toList()
    }

    /**
     * 获取消队列中的第一个 Message
     */
    private fun getMessage(handler: Handler): Message? {
        val queue = handler.looper.queue
        val messageField = MessageQueue::class.java.getDeclaredField("mMessages")
        messageField.isAccessible = true
        return messageField.get(queue) as Message?
    }
}