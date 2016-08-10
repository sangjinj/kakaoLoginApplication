package webview.sangjinj.kakaologinapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.security.MessageDigest;
//import com.squareup.picasso.Picasso;


public class MainActivity extends Activity {
    private SessionCallback mKakaocallback;
    // view
    private Button login_button;
    private TextView tv_user_id;
    private TextView tv_user_name;
    private ImageView iv_user_profile;

    private String userName;
    private String userId;
    private String profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 헤쉬키를 가져온다
        getAppKeyHash();

        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        iv_user_profile = (ImageView) findViewById(R.id.iv_user_profile);

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // 카카오 로그인 요청
                Log.d("###########","isKakaoLogin start");
                isKakaoLogin();
                Log.d("###########","isKakaoLogin end");
//                KakaoSDK.init(new KakaoSDKAdapter());
            }
        });




    }

    private void isKakaoLogin() {
        // 카카오 세션을 오픈한다
        Log.d("###########","1");
        mKakaocallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mKakaocallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        //mKakaocallback.onSessionOpened();
        Log.d("###########","2");
        Session.getCurrentSession().addCallback(mKakaocallback);
        Log.d("###########","3");
        Session.getCurrentSession().checkAndImplicitOpen();
        Log.d("###########","4");
        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, MainActivity.this);
        Log.d("###########","5");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaocallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d("###########TAG" , "세션 오픈됨");
            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            KakaorequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.d("###########TAG" , exception.getMessage());
            }
        }
    }
    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void KakaorequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;
                Log.d("########### ErrorCode" , ErrorCode+"");
                if (ErrorCode == ClientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("###########TAG1" , "오류로 카카오로그인 실패 ");

                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                errorResult.getErrorCode();

                Log.d("###########TAG2" , "오류로 카카오로그인 실패 ");
                Log.d("########## getErrorCode" , errorResult.getErrorCode()+"");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.d("###########","onSuccess");
                profileUrl = userProfile.getProfileImagePath();
                userId = String.valueOf(userProfile.getId());
                userName = userProfile.getNickname();
                setLayoutText();
            }

            @Override
            public void onNotSignedUp() {
                // 자동가입이 아닐경우 동의창
            }
        });
    }

    private void setLayoutText(){
        tv_user_id.setText(userId);
        tv_user_name.setText(userName);

//        Picasso.with(this)
//                .load(profileUrl)
//                .fit()
//                .into(iv_user_profile);
    }

    private void getAppKeyHash() {
        Log.d("###########","getAppKeyHash");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("######## Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("######## name not found", e.toString());
        }
    }
}
