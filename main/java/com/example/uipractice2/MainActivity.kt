package com.example.uipractice2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uipractice2.adapter.BookAdapter
import com.example.uipractice2.api.BookService
import com.example.uipractice2.databinding.ActivityMainBinding
import com.example.uipractice2.model.BestSellerDto
import com.example.uipractice2.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var bookService: BookService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivityMainBinding.inflate(layoutInflater)

        initBoookRecyclerView()

        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {

                override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {
                    //todo 성공처리
                    if(response.isSuccessful.not()){
                        Log.e(TAG, "NOT!! SUCCESS")
                        return
                    }
                    response.body()?.let{
                        Log.d(TAG,it.toString())

                        it.books.forEach{book ->
                            Log.d(TAG, book.toString())
                        }
                        adapter.submitList(it.books)
                    }


                }
                override fun onFailure(call: Call<BestSellerDto>,t:Throwable) {
                    //todo 실페처리
                    Log.e(TAG, t.toString())
                }

            })

        binding.searchEditText.setOnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun search(keyword: String) {
        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object : Callback<SearchBookDto> {

                override fun onResponse(call: Call<SearchBookDto>, response: Response<SearchBookDto>) {
                    //todo 성공처리
                    if(response.isSuccessful.not()){
                        Log.e(TAG, "NOT!! SUCCESS")
                        return
                    }

                    adapter.submitList(response.body()?.books.orEmpty())

                }
                override fun onFailure(call: Call<SearchBookDto>,t:Throwable) {
                    //todo 실페처리
                    Log.e(TAG, t.toString())
                }

            })
    }

    private fun initBoookRecyclerView(){
        adapter = BookAdapter(itemClickListener = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }
    companion object{
        private const val TAG="MainActivity"
    }
}