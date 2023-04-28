package jp.techacademy.hideaki.tanigawa.qa_app

import jp.techacademy.taro.kirameki.qa_app.Answer
import java.io.Serializable
import java.util.ArrayList

class Question(
    val title: String,
    val body: String,
    val name: String,
    val uid: String,
    val questionUid: String,
    val genre: Int,
    bytes: ByteArray,
    val answers: ArrayList<Answer>
) : Serializable {
    val imageBytes: ByteArray

    init {
        imageBytes = bytes.clone()
    }
}