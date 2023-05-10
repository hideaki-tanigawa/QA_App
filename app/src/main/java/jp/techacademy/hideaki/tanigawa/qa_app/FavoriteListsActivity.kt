package jp.techacademy.hideaki.tanigawa.qa_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.techacademy.hideaki.tanigawa.qa_app.databinding.ActivityFavoriteListsBinding
import jp.techacademy.hideaki.tanigawa.qa_app.databinding.ActivityMainBinding
import jp.techacademy.taro.kirameki.qa_app.Answer

class FavoriteListsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteListsBinding
    private lateinit var question: Question
    private lateinit var answerRef: DatabaseReference
    private lateinit var favoRef: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var questionArrayList: ArrayList<Question>
    private lateinit var adapter: FavoriteListsAdapter

    private val eventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val map = snapshot.value as Map<*, *>
            val questionUid = snapshot.key
            var genre = map["genre"]
            genre = genre.toString()
            genre = genre.toInt()

            databaseReference = FirebaseDatabase.getInstance().reference
            answerRef = databaseReference.child(ContentsPATH).child(genre.toString()).child(questionUid.toString())
            answerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value as Map<*,*>
                    val title = map["title"] as? String ?: ""
                    val body = map["body"] as? String ?: ""
                    val name = map["name"] as? String ?: ""
                    val uid = map["uid"] as? String ?: ""
                    val imageString = map["image"] as? String ?: ""
                    val bytes =
                        if (imageString.isNotEmpty()) {
                            Base64.decode(imageString, Base64.DEFAULT)
                        } else {
                            byteArrayOf()
                        }

                    val answerArrayList = ArrayList<Answer>()
                    val answerMap = map["answers"] as Map<*, *>?
                    if (answerMap != null) {
                        for (key in answerMap.keys) {
                            val map1 = answerMap[key] as Map<*, *>
                            val map1Body = map1["body"] as? String ?: ""
                            val map1Name = map1["name"] as? String ?: ""
                            val map1Uid = map1["uid"] as? String ?: ""
                            val map1AnswerUid = key as? String ?: ""
                            val answer = Answer(map1Body, map1Name, map1Uid, map1AnswerUid)
                            answerArrayList.add(answer)
                        }
                    }

                    val question = Question(
                        title, body, name, uid, questionUid ?: "",
                        genre, bytes, answerArrayList
                    )
                    questionArrayList.add(question)
                    adapter.notifyDataSetChanged()

                    // ----- 追加:ここから -----
                    binding.listView.setOnItemClickListener { _, _, position, _ ->
                        // Questionのインスタンスを渡して質問詳細画面を起動する
                        val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
                        intent.putExtra("question", questionArrayList[position])
                        startActivity(intent)
                    }
                    // ----- 追加:ここまで -----
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteListsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_favorite_lists)
        setContentView(binding.root)

        title = getString(R.string.menu_favorite_list_label)

        // ListViewの準備
        adapter = FavoriteListsAdapter(this)
        binding.listView.adapter = adapter
        questionArrayList = ArrayList()
        adapter.setQuestionArrayList(questionArrayList)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        val userId = intent.getStringExtra("uid")

        questionArrayList.clear()
        adapter.setQuestionArrayList(questionArrayList)
        binding.listView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().reference
        favoRef = databaseReference.child(FavoritePATH).child(userId.toString())
        favoRef.addChildEventListener(eventListener)
    }
}