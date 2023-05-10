package jp.techacademy.hideaki.tanigawa.qa_app

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.techacademy.hideaki.tanigawa.qa_app.databinding.ActivityQuestionDetailBinding
import jp.techacademy.taro.kirameki.qa_app.Answer

class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionDetailBinding

    private lateinit var question: Question
    private lateinit var adapter: QuestionDetailListAdapter
    private lateinit var answerRef: DatabaseReference
    private lateinit var favoRef: DatabaseReference
    private var userId:String = ""

    private val eventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<*, *>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in question.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val uid = map["uid"] as? String ?: ""

            val answer = Answer(body, name, uid, answerUid)
            question.answers.add(answer)
            adapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
        override fun onCancelled(databaseError: DatabaseError) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 渡ってきたQuestionのオブジェクトを保持する
        // API33以上でgetSerializableExtra(key)が非推奨となったため処理を分岐
        @Suppress("UNCHECKED_CAST", "DEPRECATION", "DEPRECATED_SYNTAX_WITH_DEFINITELY_NOT_NULL")
        question = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra("question", Question::class.java)!!
        else
            intent.getSerializableExtra("question") as? Question!!

        title = question.title

        // ListViewの準備
        adapter = QuestionDetailListAdapter(this, question)
        binding.listView.adapter = adapter
        adapter.notifyDataSetChanged()

        binding.fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                // --- ここから ---
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", question)
                startActivity(intent)
                // --- ここまで ---
            }
        }

        // ログイン済みのユーザーを取得する
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            userId = FirebaseAuth.getInstance().currentUser!!.uid
        }

        // 星の処理
        binding.favo.apply {
            if (user == null) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                val dataBaseReference = FirebaseDatabase.getInstance().reference
                val favoRef = dataBaseReference.child(FavoritePATH).child(userId).child(question.questionUid)

                // お気に入りボタンが押された場合
                setOnClickListener {
                    Log.d("これって通るの？", "通ってくれ（願望）")
                    favoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val data = snapshot.value as Map<*, *>?
                            if (data == null) {
                                val data = HashMap<String, String>()
                                data["genre"] = question.genre.toString()
                                favoRef.setValue(data)
                                // 星の色を色塗りにする
                                binding.favo.setImageResource(R.drawable.ic_star)
                            } else {
                                favoRef.removeValue()
                                // 星の色をボーダーにする
                                binding.favo.setImageResource(R.drawable.ic_star_border)
                            }
                        }

                        override fun onCancelled(firebaseError: DatabaseError) {}
                    })

                }
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        favoriteButtonCheck(dataBaseReference)

        answerRef = dataBaseReference.child(ContentsPATH).child(question.genre.toString())
            .child(question.questionUid).child(AnswersPATH)
        answerRef.addChildEventListener(eventListener)
    }

    /**
     * お気に入りされているかどうかで星の色を変更する
     * @param databaseReference Firebaseを操作する仕様書
     */
    private fun favoriteButtonCheck(databaseReference: DatabaseReference) {
        favoRef = databaseReference.child(FavoritePATH).child(userId).child(question.questionUid)

        favoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value as Map<*, *>?
                if (data == null) {
                    binding.favo.setImageResource(R.drawable.ic_star_border)
                } else {
                    binding.favo.setImageResource(R.drawable.ic_star)
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {}
        })
    }
}
