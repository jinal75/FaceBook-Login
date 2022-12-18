package com.example.facebooklogin


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.FacebookSdk.sdkInitialize
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import org.json.JSONObject
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {
    var id: String? = null
    var profile_pic: String? = null
    lateinit var profilePictureUri: Uri
    var name: String? = null
    private var img:ImageView?=null
    private var info: TextView? = null
    private var loginButton: LoginButton? = null
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        info = findViewById<View>(R.id.info) as TextView
        img=findViewById<View>(R.id.fimage)as ImageView

        loginButton = findViewById<View>(R.id.login_button) as LoginButton
        loginButton!!.setOnClickListener {


        }
        val permissionNeeds: List<String> = Arrays.asList("user_photos", "email",
            "user_birthday", "public_profile", "AccessToken")

        loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(result: LoginResult?) {


                println("onSuccess")
                val accessToken = result!!.accessToken
                    .token

                Log.i("accessToken", accessToken)
                val request = GraphRequest.newMeRequest(
                    result.accessToken,
                    object : GraphRequest.GraphJSONObjectCallback {
                        override fun onCompleted(
                            `object`: JSONObject?,
                            response: GraphResponse?,
                        ) {
                            Log.i("LoginActivity",
                                response.toString())
                            try {
                                id = `object`!!.getString("id")
                                name = `object`.getString("name")

                                try {
                                    profile_pic = URL(
                                        "http://graph.facebook.com/$id/picture?type=large").toString()
                                    Log.i("profile_pic", profile_pic.toString() + "")
                                    profilePictureUri =
                                        ImageRequest.getProfilePictureUri(Profile.getCurrentProfile()!!.id, 100, 100)
                                } catch (e: MalformedURLException) {
                                    e.printStackTrace()
                                }
                                Log.e("UserDate", `object`.toString())
                                info!!.text = "ID: $id\nName: $name"
                                Glide.with(this@MainActivity)
                                    .load(URL("http://graph.facebook.com/$id/picture?type=large"))
                                    .into(img!!)

                              //  Glide.with(this@MainActivity)
                                   // .load(profile_pic)
                                  //  .into(img!!)


                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    })
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender, birthday")
                request.parameters = parameters
                request.executeAsync()
            }


            override fun onCancel() {
                info!!.text = "Login attempt canceled."
            }

            override fun onError(e: FacebookException) {
                info!!.text = "Login attempt failed."
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

    }
}
