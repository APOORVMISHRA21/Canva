package com.example.mycanva;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.example.mycanva.databinding.ActivitySignUpBinding;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager mCallbackManager;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> permissions = Arrays.asList("email", "public_profile");

        //facebool callback
        mCallbackManager = CallbackManager.Factory.create();


        binding.fb.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("TAG", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("TAG", "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("TAG", "facebook:onError", error);
                    }
                });
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("704674540707-6u38k98f8qgv64arm9mebn82shfaiq99.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.inputEmail.addTextChangedListener(emailWatcher);

        mAuth = FirebaseAuth.getInstance();

        binding.signUp.setOnClickListener(v -> {
            if(isValidEmail(binding.inputEmail.getText().toString())){
                signUpWithEmailPass(binding.inputEmail.getText().toString(), binding.inputPassword.getText().toString());
            }
        });

        binding.googleSignIn.setOnClickListener(v -> {
            signUpWithGoogle();
        });

        binding.fbSignIn.setOnClickListener(v -> {
            binding.fb.performClick();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        mCallbackManager.onActivityResult(requestCode, responseCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!isValidEmail(s.toString())) {
                binding.inputEmailLayout.setBoxStrokeColor(getResources().getColor(R.color.red));
            }
            else {
                binding.inputPasswordLayout.setBoxStrokeColor(getResources().getColor(R.color.green));
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    public static boolean isValidEmail(String email) {
        String expression = "^[\\w\\.]+@([\\w]+\\.)+[A-Z]{2,7}$";
        CharSequence inputString = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputString);

        return matcher.matches();

    }

    public void signUpWithEmailPass(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AUTH---STATUS", "%%%%%%%%%%%%%%%%%  createUserWithEmail:success %%%%%%%%%%%%%%%%%%%%");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("AUTH---CRED", "%%%%%%%%%%%%%%%%      Email : " + user.getEmail() + "  Name: " + user.getDisplayName()+"       %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%") ;
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTH--STATUS", "#################    createUserWithEmail:failure  #################################3", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUpWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        LaunchGoogleSignIn.launch(signInIntent);
    }

    private final ActivityResultLauncher<Intent> LaunchGoogleSignIn = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("CODE STATUS: ","%%%%%%%%%%%%%%%%%%%%%%    " + "Recieved activity result   " + "         $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                    googleAuth(result.getData());
                }
            });

    private void googleAuth(Intent data){

        Log.d("CODE STATUS: ","%%%%%%%%%%%%%%%%%%%%%%    " + "Called Google Auth   " + "         $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d("TAG", "####################    firebaseAuthWithGoogle:" + account.getId() + "           ###$$$$$$$$$$$$$$$$$$$$$$$$");
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w("TAG", "Google sign in failed", e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        Log.d("CODE STATUS: ","%%%%%%%%%%%%%%%%%%%%%%    " + "FirebaseAuth    " + "         $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AUTH---STATUS", "%%%%%%%%%%%%%%%%%  googleSignIn :success %%%%%%%%%%%%%%%%%%%%");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTH--STATUS", "#################    createUserWithEmail:failure  #################################3", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}