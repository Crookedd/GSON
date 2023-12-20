package com.example.gson

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.io.IOException
import android.net.Uri

interface CellClickListener{
    fun onCellClickListener(link: String)
}
const val CODE = 1

class MainActivity : AppCompatActivity(), CellClickListener {
    private val URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"
    private val okHttpClient : OkHttpClient = OkHttpClient()
    private var links : Array<String> = arrayOf()
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rView)

        getJSONFromServer()
    }
    private fun getJSONFromServer() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(URL)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")

            }

            override fun onResponse(call: Call, response: Response) {
                val jsonFromServer = response.body?.string()
                parseJSON(jsonFromServer)
            }
        })
    }

    private fun parseJSON(jsonFromServer: String?) {

        val result: Wrapper = Gson().fromJson(jsonFromServer, Wrapper::class.java)
        for (i in 0 until result.photos.photo.size) {
            val link =
                "https://farm${result.photos.photo[i].farm}.staticflickr.com/${result.photos.photo[i].server}/${result.photos.photo[i].id}_${result.photos.photo[i].secret}_z.jpg"
            links += link
            Timber.d("Photo_link", links[i])
        }
        runOnUiThread {
            recyclerView.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerView.adapter = MyRecyclerAdapter(this, links, this)
        }
    }
    override fun onCellClickListener(link: String) {
        val intent = Intent(this, PicViewer::class.java)
        intent.putExtra(getString(R.string.key_link), link)
        startActivityForResult(intent, CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE && resultCode == RESULT_OK) {
            val link = data?.getStringExtra(getString(R.string.key_link))

            // Добавьте ваш код для обработки результата
            if (link != null) {
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Картинка добавлена в избранное",
                    Snackbar.LENGTH_LONG
                )

                snackbar.setAction("Открыть") {
                    // Открыть ссылку во встроенном браузере
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }

                snackbar.show()
            }
        }
    }
}

data class Wrapper(
    val photos : Page,
    val stat : String
)

data class Page(
    val page : Number,
    val pages : Number,
    val perpage : Number,
    val total : Number,
    val photo : Array<Photo>
)

data class Photo(
    val id : Number,
    val owner : String,
    val secret : Number,
    val server : Number,
    val farm : Number,
    val title : String,
    val isPublic : Boolean,
    val isFriend: Boolean,
    val isFamily : Boolean
)

class MyRecyclerAdapter(private val context: Context, private val links: Array<String>, private val clickListener: CellClickListener) :
    RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return links.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = links[position]
        Picasso.with(context).load(data).into(holder.pic)

        holder.itemView.setOnClickListener {
            clickListener.onCellClickListener(data)
        }
    }
}
