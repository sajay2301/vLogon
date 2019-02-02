package com.vlogonappv1

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.TwitterAuthProvider
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.view.clicks

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {


    private var callbackManager: CallbackManager? = null
    private val RC_SIGN_IN = 9001
    private var mGoogleApiClient: GoogleApiClient? = null
    private val LinDink_SIGN_IN = 1001
    private var isFacebook = false
    private var isTwitter = false
    private var isLinkdin = false

    lateinit var dialog: Dialog
    val pattern: Pattern? = null
    val list: ArrayList<String> = ArrayList()
    var firebaseAuth: FirebaseAuth? = null
    lateinit var twitterSignInButton: TwitterLoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (!mSessionHolder.User_Login.isEmpty()) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }
        val button = findViewById<View>(R.id.btnVerify) as Button
        val btngooglelogin = findViewById<View>(R.id.btngooglelogin) as Button
        val btnfacebooklogin = findViewById<View>(R.id.btnfacebooklogin) as Button
        twitterSignInButton = findViewById<View>(R.id.twitter_sign_in_button) as TwitterLoginButton


        FirebaseAuth.getInstance().signOut()
        firebaseAuth = FirebaseAuth.getInstance()

        RxView.clicks(btnfacebooklogin).subscribe { aVoid ->

            isFacebook = true
            facebooklogin()

        }
        RxView.clicks(button).subscribe { aVoid ->

            isLinkdin = true
           /* LISessionManager.getInstance(applicationContext).init(this@LoginActivity, buildScope(), object :
                AuthListener {
                override fun onAuthSuccess() {
                    //linkdin()
                    linkdin()

                }

                override fun onAuthError(error: LIAuthError) {
                    // Handle authentication errors
                }
            }, true)*/


        }
        RxView.clicks(btngooglelogin).subscribe { aVoid ->

            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)


        }


        twitterSignInButton.clicks().subscribe {
            isTwitter = true

            val packageName = "com.twitter.android"
            val isYoutubeInstalled = isAppInstalled(packageName)
            if(isYoutubeInstalled)
            {
                Log.e("Installed", "Installed")

            }else
            {
                Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show();
            }
        }

        btnemail.clicks().subscribe {

            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            intent.putExtra("emaillogin", "true")
            intent.putExtra("source", "email")
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }
        txtlogin.clicks().subscribe{

            val intent = Intent(this@LoginActivity, UserLoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }

        twitterSignInButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.d("", "twitterLogin:success" + result)

                val Username = result.data.userName


                handleTwitterSession(result.data)


            }

            override fun failure(exception: TwitterException) {
                Log.w("", "twitterLogin:failure", exception)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        try {
            val info = packageManager.getPackageInfo(
                "com.vlogon",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }


        // getcontactlist()

       Permissions.verifyStoragePermissions(this@LoginActivity)
    }

    private fun handleTwitterSession(session: TwitterSession) {
        Log.d("", "handleTwitterSession:" + session)
        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret
        )
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.e("success", "signInWithCredential:success")
                    val user = firebaseAuth!!.currentUser
                    val email = firebaseAuth!!.currentUser!!.photoUrl


                    val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                    intent.putExtra("emailid", "")
                    intent.putExtra("firstname", user!!.displayName.toString().substring(0, user.displayName.toString().indexOf(" ")))
                    intent.putExtra("lastname",  user.displayName.toString().substring(user.displayName.toString().lastIndexOf(" ") + 1))
                    intent.putExtra("profilepic",  user.photoUrl.toString())
                    intent.putExtra("emaillogin", "false")
                    intent.putExtra("source", "twitter")
                    startActivity(intent)
                    overridePendingTransition(R.anim.enter, R.anim.exit)
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("failed", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("bett", "onConnectionFailed:" + connectionResult)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Add this line to your existing onActivityResult() method

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }

        if (isLinkdin) {
          //  LISessionManager.getInstance(applicationContext).onActivityResult(this, requestCode, resultCode, data)
            isFacebook = false
            isTwitter = false
        } else if (isFacebook) {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
            isLinkdin = false
            isTwitter = false
        } else if (isTwitter) {
            twitterSignInButton.onActivityResult(requestCode, resultCode, data)
            isLinkdin = false
            isFacebook = false
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {


            val acct = result.signInAccount
            var displayname = acct!!.givenName + " " + acct.familyName

            val ProfilePic = acct.photoUrl


            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            intent.putExtra("emailid", acct.email.toString())
            intent.putExtra("firstname", acct.givenName.toString())
            intent.putExtra("lastname", acct.familyName.toString())
            intent.putExtra("profilepic", acct.photoUrl.toString())
            intent.putExtra("emaillogin", "false")
            intent.putExtra("source", "google")
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()

        } else {
            Log.e("Dad", "dsd")
        }
    }


    fun facebooklogin() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.e("MainActivity", "Facebook token: " + loginResult.accessToken.token)
                    //startActivity(Intent(applicationContext, AuthenticatedActivity::class.java))

                    val accessToken = AccessToken.getCurrentAccessToken()
                    val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->


                        try {

                            var userid = `object`.getString("id")
                            var emailid = `object`.getString("email")
                            var firstname = `object`.getString("first_name")
                            var lastname = `object`.getString("last_name")
                            var displayname = `object`.getString("first_name") + " " + `object`.getString("last_name")

                            val profilePicUrl = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                            intent.putExtra("emailid", emailid)
                            intent.putExtra("firstname", firstname)
                            intent.putExtra("lastname", lastname)
                            intent.putExtra("profilepic", profilePicUrl)
                            intent.putExtra("emaillogin", "false")
                            intent.putExtra("source", "facebook")
                            startActivity(intent)
                            overridePendingTransition(R.anim.enter, R.anim.exit)
                            finish()

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (AccessToken.getCurrentAccessToken() != null) {
                            AccessToken.setCurrentAccessToken(null)
                            LoginManager.getInstance().logOut()

                        }


                        /* startActivity<SelectLanguageActivity>()
                         finish()*/

                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,first_name,last_name,gender,picture.type(normal)")
                    request.parameters = parameters
                    request.executeAsync()


                }

                override fun onCancel() {
                    Log.d("MainActivity", "Facebook onCancel.")

                }

                override fun onError(error: FacebookException) {
                    Log.d("MainActivity", "Facebook onError." + error.message)
                    showErrorSnack(getString(R.string.err_no_internet))
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                    }
                }
            })
    }


    private fun showErrorSnack(message: String) {

        loginlayout.snack(message)

    }


    private fun linkdin() {
        val url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)"

     /*   val apiHelper = APIHelper.getInstance(applicationContext)
        apiHelper.getRequest(this@LoginActivity, url, object : ApiListener {
            override fun onApiSuccess(apiResponse: ApiResponse) {
                // Success!
                Log.e("linkedin response", apiResponse.responseDataAsJson.toString())

                startActivity<RegistrationActivity>()
                finish()
            }

            override fun onApiError(liApiError: LIApiError) {
                // Error making GET request!
                Log.e("linkedin response", liApiError.message)


            }
        })*/
    }

    fun View.snack(msg: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, msg, duration).show()
    }

//    private fun buildScope(): Scope {
//        return Scope.build(Scope.R_BASICPROFILE)
//    }

    private fun isAppInstalled(packageName: String): Boolean {
        val mIntent = packageManager.getLaunchIntentForPackage(packageName)
        return mIntent != null
    }

    fun verifyStoragePermissions(activity: Activity) {

        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf<String>(

            //Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}
