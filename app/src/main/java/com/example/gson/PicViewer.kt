package com.example.gson

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
//import android.widget.Toolbar
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso

class PicViewer : AppCompatActivity() {
    private lateinit var view: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var link: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pic_viewer)
        link = intent?.getStringExtra(getString(R.string.key_link))?: ""

        view = findViewById(R.id.imageView2)
        Picasso.with(this).load(link).into(view)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.btn_heart) {
            // Отправляем результат обратно в родительскую Activity
            val resultIntent = Intent()
            resultIntent.putExtra(getString(R.string.key_link), link)
            setResult(RESULT_OK, resultIntent)
            finish() // Завершаем работу текущей Activity
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}