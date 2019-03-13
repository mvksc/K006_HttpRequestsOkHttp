package m.vk.k006_httprequestsokhttp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnGetHttp.setOnClickListener{
            //getOkHttp("MyUrl?MyKey1=Value1&MyKey2=Value2")
            getOkHttp("http://api.plos.org/search?q=title:%22Drosophila%22%20and%20body:%22RNA%22&fl=id,abstract&wt=json&indent=on")
        }

        btnPostHttp.setOnClickListener {
            val myMap = HashMap<String, String>()
            /*Key = Value*/
            myMap["lat"] = "17.429723"
            myMap["lon"] = "102.814617"
            myMap["os"] = "2"
            postOkHttp("MyUrl",myMap)
        }

    }

    fun getOkHttp(url: String){
        if (isNetworkAvailable()){
            object : AsyncTask<String,Void,String>(){
                override fun onPreExecute() {
                    super.onPreExecute()
                    onShowLogCat("GET", "url = $url")
                    tvShowResult.gravity = Gravity.CENTER_HORIZONTAL
                    tvShowResult.text = "กำลังโหลด..."
                }
                override fun doInBackground(vararg p0: String?): String {
                    try {
                        var client = OkHttpClient()
                        var request = Request.Builder()
                            .url(url)
                            .build()
                        var response = client.newCall(request).execute()
                        if(response.isSuccessful){
                            return response.body()!!.string().toString()
                        }

                    }catch (e:Exception){
                        onShowLogCat("Error","getOkHttp ${e.message}")
                    }
                    return ""
                }

                override fun onPostExecute(result: String?) {
                    tvShowResult.gravity = Gravity.LEFT
                    tvShowResult.text = result
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }else{
            onShowToast("กรุณาเชื่อมต่ออินเตอร์เน็ต")
        }
    }
    fun postOkHttp(url: String,parameters: HashMap<String,String>){
        if (isNetworkAvailable()){
            object : AsyncTask<String,Void,String>(){
                var builders = FormBody.Builder()
                override fun onPreExecute() {
                    super.onPreExecute()
                    var it = parameters.entries.iterator()
                    onShowLogCat("POST", "url = $url")
                    while (it.hasNext()){
                        var pair = it.next() as Map.Entry<*,*>
                        onShowLogCat("POST","Key = " + pair.key.toString() + " Value = " + pair.value.toString())
                        builders.add(pair.key.toString(),pair.value.toString())
                    }

                    tvShowResult.gravity = Gravity.CENTER_HORIZONTAL
                    tvShowResult.text = "กำลังโหลด..."
                }
                override fun doInBackground(vararg p0: String?): String {
                    try {
                        val formBody = builders.build()
                        var client = OkHttpClient()
                        var request = Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build()
                        var response = client.newCall(request).execute()
                        if(response.isSuccessful){
                            return response.body()!!.string().toString()
                        }

                    }catch (e:Exception){
                        onShowLogCat("Error","postOkHttp ${e.message}")
                    }
                    return ""
                }

                override fun onPostExecute(result: String?) {
                    tvShowResult.gravity = Gravity.LEFT
                    tvShowResult.text = result
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }else{
            onShowToast("กรุณาเชื่อมต่ออินเตอร์เน็ต")
        }
    }

    fun isNetworkAvailable(): Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
       return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        }else false
    }
    fun onShowToast(msg: String){
        Toast.makeText(this@MainActivity,msg,Toast.LENGTH_LONG).show()
    }
    fun onShowLogCat(tag: String,msg: String){
        if (BuildConfig.DEBUG){
            Log.e("***MainActivity***","Tag : $tag ==> Msg : $msg")
        }
    }
}
