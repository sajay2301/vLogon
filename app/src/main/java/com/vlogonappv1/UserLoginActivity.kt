package com.vlogonappv1

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.TwitterAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.view.clicks
import com.vlogonappv1.db.DBHelper
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import com.vlogonappv1.AppApplication.Companion.mSessionHolder
import com.vlogonappv1.dataclass.ProgressDialogshow
import com.vlogonappv1.dataclass.UserRegistrationClass
import com.vlogonappv1.forgetpassword.ForgetPasswordActivity
import kotlinx.android.synthetic.main.activity_user_login.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import kotlinx.android.synthetic.main.restoreforgetpasswordoptiondialog.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.regex.Pattern


class UserLoginActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener {

    private var db: DBHelper? = null
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
    lateinit var Firestoredb: FirebaseFirestore
    var passwordencodedKey: String = ""
    internal var flag = 0
    companion object {

        var forgetoptiondialog: Dialog? = null



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        if (!mSessionHolder.User_Login.isEmpty()) {

            val intent = Intent(this@UserLoginActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()

        }
        db = DBHelper(applicationContext)
        Firestoredb = FirebaseFirestore.getInstance()
        toolbar?.apply {

            tvToolbarTitle.text = "Log In"
            icBack.visibility = View.VISIBLE
            llActionIcon.visibility = View.GONE
            icBack.clicks().subscribe {
                onBackPressed()
            }
            setSupportActionBar(this)


        }
        if (!mSessionHolder.Login_id.isEmpty()) {
            checkBoxremeber.isChecked=true
            etemailid.text = Editable.Factory.getInstance().newEditable(mSessionHolder.Login_id)
            etpassword.text = Editable.Factory.getInstance().newEditable(mSessionHolder.Login_Password)
        }
        btnlogin.clicks().subscribe{

            if (etemailid.getText().toString().isEmpty()) {
                Toast.makeText(
                    this@UserLoginActivity,
                    "Please Enter Email ID",
                    Toast.LENGTH_SHORT
                ).show()

            }
            else if (etpassword.getText().toString().isEmpty()) {
                Toast.makeText(
                    this@UserLoginActivity,
                    "Please Enter Password",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                UserLogin(etemailid.getText().toString(),etpassword.getText().toString())
            }
            }

        txtsignup.clicks().subscribe{

            val intent = Intent(this@UserLoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)
            finish()
        }



        checkBoxremeber.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
            {
                mSessionHolder.Login_id = etemailid.text.toString()
                mSessionHolder.Login_Password = etpassword.text.toString()
            }else
            {
                mSessionHolder.Login_id = ""
                mSessionHolder.Login_Password = ""
            }
        }







        twitterSignInButton = findViewById<View>(R.id.twitter_user_in_button) as TwitterLoginButton


        FirebaseAuth.getInstance().signOut()
        firebaseAuth = FirebaseAuth.getInstance()

        ivfacebook.clicks().subscribe {

            isFacebook = true
            facebooklogin()

        }

        ivgoogle_plus.clicks().subscribe { aVoid ->

            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)


        }

        ivtwitter.clicks().subscribe { aVoid ->

            twitterSignInButton.performClick()

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




        txtsignup.clicks().subscribe {

            val intent = Intent(this@UserLoginActivity, RegistrationActivity::class.java)
            intent.putExtra("emaillogin", "true")
            intent.putExtra("source", "email")
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

        val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .requestScopes(Drive.SCOPE_APPFOLDER)

        val signInOptions = builder.build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
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

        Permissions.verifyStoragePermissions(this@UserLoginActivity)


        txtforgetpassword.clicks().subscribe {
          /*  forgetoptiondialog = ForgetPasswordOptionDialog(
                this@UserLoginActivity
            )
            forgetoptiondialog!!.show()*/
            mSessionHolder.User_ActivityName=""
            val intent = Intent(this@UserLoginActivity, ForgetPasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter, R.anim.exit)

        }
    }
    private fun getTasks() {
        class GetTasks : AsyncTask<Void, Void, List<UserRegistrationClass>>() {

            override fun doInBackground(vararg voids: Void): List<UserRegistrationClass> {
                return db!!.LoginUser(etemailid.text.toString(),etpassword.text.toString())

            }

            override fun onPostExecute(tasks: List<UserRegistrationClass>) {
                super.onPostExecute(tasks)


                if(tasks.isEmpty())
                {
                    Toast.makeText(applicationContext, "Email Id Or Password Is Incorrect", Toast.LENGTH_LONG).show()

                }else {
                    try {

                        mSessionHolder.User_Login = tasks[0].personalemail.toString()
                        mSessionHolder.Source_login = tasks[0].source.toString()
                        mSessionHolder.User_Mobilenumber=tasks[0].mobilenumber.toString()
                        mSessionHolder.User_Countrycode=tasks[0].countrycode.toString()
                        Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@UserLoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()
                    } catch (e: IndexOutOfBoundsException) {

                    }
                }

            }
        }

        val gt = GetTasks()
        gt.execute()
    }
    override fun onBackPressed() {


        finish()


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


                    val intent = Intent(this@UserLoginActivity, RegistrationActivity::class.java)
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
                        this@UserLoginActivity, "Authentication failed.",
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


            val intent = Intent(this@UserLoginActivity, RegistrationActivity::class.java)
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
                            val intent = Intent(this@UserLoginActivity, RegistrationActivity::class.java)
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

        mainlayout.snack(message)

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



    fun ForgetPasswordOptionDialog(context: Context): Dialog {

        var option: String = ""

        val inflate = LayoutInflater.from(context).inflate(R.layout.restoreforgetpasswordoptiondialog, null)
        val optiondialog = Dialog(context)
        optiondialog.setContentView(inflate)
        optiondialog.setCancelable(false)
        optiondialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val window = optiondialog.window
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        window.attributes = wlp
        optiondialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)


        optiondialog.forgetbuttoncancel.clicks().subscribe {
            optiondialog.dismiss()
        }
        optiondialog.radioforgetoption?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radiomobile -> option = "mobile"
                R.id.radioemail -> option = "email"

            }
        }

        optiondialog.buttonoksubmit.clicks().subscribe {

            optiondialog.dismiss()

            if (option.equals("mobile")) {

                val intent = Intent(this@UserLoginActivity, ForgetPasswordActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.enter, R.anim.exit)


            } else {
                mSessionHolder.User_ActivityName=""
                val intent = Intent(this@UserLoginActivity, ForgetPasswordActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.enter, R.anim.exit)
            }

        }



        optiondialog.setOnKeyListener { dialog, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK) {
                optiondialog.dismiss()
                true
            } else {
                false
            }
        }


        return optiondialog
    }

    private fun UserLogin(emailid: String, password: String) {


        passwordencodedKey = String(Base64.encode(password.toByteArray(), 0))

        flag=0
        dialog = ProgressDialogshow.progressDialog(this@UserLoginActivity)
        dialog.show()
        Firestoredb.collection("RegisterUser").whereEqualTo("Primary Email", emailid)
            .whereEqualTo("Password", passwordencodedKey)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        //  Log.e("data", document.getId() + " => " + document.get("name"));
                        flag = 1
                        mSessionHolder.USER_ID = document.id
                        mSessionHolder.User_Login = document.get("Primary Email").toString()
                        mSessionHolder.Source_login = document.get("Source").toString()
                        mSessionHolder.User_Mobilenumber=document.get("Mobile Number").toString()
                        mSessionHolder.User_Countrycode=document.get("Countrycode").toString()
                        mSessionHolder.User_Password=document.get("Password").toString()


                    }
                    if (flag == 0) {
                        Toast.makeText(this@UserLoginActivity, "Username or Password Is Incorrect", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()


                    } else {
                        Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@UserLoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.enter, R.anim.exit)
                        finish()

                    }
                } else {
                    dialog.dismiss()
                    Log.e("dasd", "Error getting documents.", task.exception)
                }
            }


    }

}
