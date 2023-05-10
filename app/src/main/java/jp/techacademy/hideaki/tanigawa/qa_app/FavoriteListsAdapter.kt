package jp.techacademy.hideaki.tanigawa.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import jp.techacademy.hideaki.tanigawa.qa_app.databinding.ListFavoriteBinding

class FavoriteListsAdapter(context: Context) : BaseAdapter() {
    private var layoutInflater: LayoutInflater
    private var favoriteArrayList = ArrayList<Question>()

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return favoriteArrayList.size
    }

    override fun getItem(position: Int): Any {
        return favoriteArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // ViewBindingを使うための設定
        val binding = if (convertView == null) {
            ListFavoriteBinding.inflate(layoutInflater, parent, false)
        } else {
            ListFavoriteBinding.bind(convertView)
        }
        val view: View = convertView ?: binding.root

        binding.titleTextView.text = favoriteArrayList[position].title
        binding.nameTextView.text = favoriteArrayList[position].name
        binding.resTextView.text = favoriteArrayList[position].answers.size.toString()

        val bytes = favoriteArrayList[position].imageBytes
        if (bytes.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                .copy(Bitmap.Config.ARGB_8888, true)
            binding.imageView.setImageBitmap(image)
        }

        return view
    }

    // リスト更新
    fun setQuestionArrayList(questionArrayList: ArrayList<Question>) {
        this.favoriteArrayList = questionArrayList
    }

}