package com.ckh.org.rssnews.fragment

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import com.ckh.org.rssnews.R

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ckh.org.rssnews.activity.MainActivity
import com.ckh.org.rssnews.adapter.NewsRecyclerAdapter
import com.ckh.org.rssnews.listener.NewsItemClickListener
import com.ckh.org.rssnews.vo.Data
import kotlinx.android.synthetic.main.news_list_fragment.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeoutException
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

class NewsListFragment:Fragment(), CoroutineScope{
    private val st = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"
    private var isLoading = true
    private lateinit var job: Job
    private lateinit var recyclerAdapter: NewsRecyclerAdapter
    private val items = ArrayList<Data>()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object{
        fun getInstance(bundle : Bundle): Fragment{
            val fragment = NewsListFragment()
            fragment.arguments = bundle
            return fragment
        }

        class TagItem:Comparable<TagItem>{
            var str: String = ""
            var cnt: Int = 0
            override fun compareTo(other: TagItem): Int {
                return when {
                    this.cnt < other.cnt -> 1
                    this.cnt == other.cnt -> {
                        this.str.compareTo(other.str)
                    }
                    else -> -1
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.news_list_fragment, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = Job()

        recyclerAdapter = NewsRecyclerAdapter()
        recyclerAdapter.setItemClickListener(object : NewsItemClickListener {
            override fun onClick(link:String) {
//                val mainActivity = activity as MainActivity
                showNewsOnWebView(link)
//                mainActivity.changeFragment(link)
            }
        })
        with(NewsRecyclerView){
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL)
            )
            adapter = recyclerAdapter
        }

        SwipeRefreshNewsLayout.setOnRefreshListener{
            if(!isLoading){
                items.clear()
                activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                getLoadDate(1)
            }else{
                SwipeRefreshNewsLayout.isRefreshing = false
            }
            //getItemLoad()
        }

        getLoadDate(0)

    }
    private fun getLoadDate(type:Int){
        launch {
            isLoading = true
            lateinit var doc:org.w3c.dom.Document
            withContext(Dispatchers.IO) {
                val url = URL(st)
                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()

                doc = db.parse(InputSource(url.openStream()))
                doc.documentElement.normalize()
            }
            val nodeList = doc.getElementsByTagName("item")
            for(i in 0 until nodeList.length){
                val node = nodeList.item(i)
                val element = node as Element
                val title = element.getElementsByTagName("title").item(0).childNodes.item(0).nodeValue
                val link = element.getElementsByTagName("link").item(0).childNodes.item(0).nodeValue
//                Log.d("rt-link",link)
                lateinit var temp:Document
                try {
                    withContext(Dispatchers.IO) {
                            temp = Jsoup.connect(link).get()
                    }
                }catch (e : IOException) {
                    continue
                }
                    val img = temp.select("meta[property=og:image]")
                    var imgRes = "https://via.placeholder.com/300.jpg"
                    if (img.size > 0) {
                        img.get(0).attr("content").let {
                            imgRes = it
                        }
                    }
                    val des = temp.select("meta[property=og:description]")
                    var desRes = ""
                    var tag1 = ""
                    var tag2 = ""
                    var tag3 = ""
                    if (des.size > 0) {
                        if(des.get(0).attr("content").isNotEmpty()) {
                            des.get(0).attr("content").let {
                                desRes = it.trim()
                                if (desRes.length > 0) {
//                            Log.d("rt-des",desRes)
                                    getTagInDes(desRes).let {
                                        tag1 = it[0]
                                        tag2 = it[1]
                                        tag3 = it[2]
                                    }
                                }
                            }
                        }else{
                            continue
                        }
                    } else {
                        continue
                    }

                    when (type) {
                        0 -> {
                            recyclerAdapter.addItem(Data(title, desRes, link, imgRes, tag1, tag2, tag3))
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        1 -> {
                            items.add(Data(title, desRes, link, imgRes, tag1, tag2, tag3))
                        }
                    }
            }
            if(type == 1)  {
                recyclerAdapter.addAllItem(items)
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                SwipeRefreshNewsLayout.isRefreshing = false
            }

            isLoading = false

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }

    private fun showNewsOnWebView( link:String){
        val builder = CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))

        val intent = builder.build();
        intent.launchUrl(context!!, Uri.parse(link))
    }

    private fun getTagInDes(str:String): Array<String> {
        val map = HashMap<String, Int>()
        val regex = Regex("[^A-Za-z0-9가-힣]+")
        val str_t = str.replace(regex," ")
//        Log.d("rt-des",str_t)
        val st = StringTokenizer(str_t)

        while (st.hasMoreTokens()){
            val tag = st.nextToken()
            if(tag.length >= 2){
                if(map.containsKey(tag)){
                    map[tag] = map[tag]!!+1
                }else{
                    map[tag] = 1
                }
            }
        }
        val tags = Array(map.size) { TagItem() }
        var idx = 0
        for (key:String in map.keys){
            tags[idx].str = key
            tags[idx].cnt = map.get(key)!!

            idx += 1
        }
        Arrays.sort(tags)

        return Array(3) {
            tags[it].str
        }
    }
}