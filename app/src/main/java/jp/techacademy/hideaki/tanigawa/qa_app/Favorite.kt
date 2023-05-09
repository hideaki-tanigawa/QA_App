package jp.techacademy.hideaki.tanigawa.qa_app

import java.io.Serializable

/**
 * 変数名	内容
 * userId　Firebaseから取得したお気に入りを押した人のUID
 * questionId	Firebaseから取得したお気に入りを押された質問ID
 */
class Favorite(val userId: String, val questionId2: String) : Serializable