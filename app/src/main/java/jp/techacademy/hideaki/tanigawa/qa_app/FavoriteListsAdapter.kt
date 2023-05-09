package jp.techacademy.hideaki.tanigawa.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import jp.techacademy.hideaki.tanigawa.qa_app.databinding.ListQuestionsBinding

class FavoriteListsAdapter(context: Context) : BaseAdapter() {
    private var layoutInflater: LayoutInflater
    private var favoriteArrayList = ArrayList<Favorite>()
    private var questionArrayList = ArrayList<Question>()

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return favoriteArrayList.size
    }

    /**
     * @param position
     */
    override fun getItem(position: Int): Any {
        return favoriteArrayList[position]
    }

    /**
     * @param position
     */
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // ViewBindingを使うための設定
        val binding = if (convertView == null) {
            ListQuestionsBinding.inflate(layoutInflater, parent, false)
        } else {
            ListQuestionsBinding.bind(convertView)
        }
        val view: View = convertView ?: binding.root

        binding.titleTextView.text = questionArrayList[position].title
        binding.nameTextView.text = questionArrayList[position].name
        binding.resTextView.text = questionArrayList[position].answers.size.toString()

        val bytes = questionArrayList[position].imageBytes
        if (bytes.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                .copy(Bitmap.Config.ARGB_8888, true)
            binding.imageView.setImageBitmap(image)
        }

        return view
    }
}