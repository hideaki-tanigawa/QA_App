package jp.techacademy.hideaki.tanigawa.qa_app

import jp.techacademy.taro.kirameki.qa_app.Answer
import java.util.*

class FireStoreQuestion {
    var id = UUID.randomUUID().toString()
    var title = ""
    var body = ""
    var name = ""
    var uid = ""
    var image = ""
    var genre = 0
    var answers: ArrayList<Answer> = arrayListOf()
}