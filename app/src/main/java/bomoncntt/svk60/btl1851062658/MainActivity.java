package bomoncntt.svk60.btl1851062658;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import bomoncntt.svk60.btl1851062658.api.LoginAPI;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edittext_username = findViewById(R.id.login_user_edittext);
        EditText edittext_password = findViewById(R.id.login_user_password_edittext);
        Button button_login = findViewById(R.id.user_login_sign_in_button);

        LoginAPI login_api = new LoginAPI(this);
        SharedPreferences login_local_session = getApplicationContext().getSharedPreferences("login_session", MODE_PRIVATE);
        SharedPreferences.Editor login_local_session_editor = login_local_session.edit();

        try {
            Intent intent = getIntent();
            String global_request = intent.getStringExtra(WelcomeActivity.REQUEST_COMMAND);
            switch (global_request) {
                case "sign_out":
                    signOut(login_api, login_local_session, login_local_session_editor);
                    break;
            }
        } catch (Exception exception) {
            login_api.createDemoUser();

            if (checkLocalLoginSession(login_api, login_local_session)) {
                startActivity(new Intent(this, WelcomeActivity.class));
            } else {
                Toast.makeText(this, "Đăng nhập đã hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                Log.v("debugger","DNHH");
            }

            button_login.setOnClickListener(v -> {
                String input_username = edittext_username.getText().toString();
                String input_userpassword = edittext_password.getText().toString();

                if (login_api.loginByUsername(input_username, input_userpassword)) {
                    if (updateLocalLoginSession(login_api, input_username, input_userpassword, login_local_session_editor)) {
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, WelcomeActivity.class));
                    } else {
                        Toast.makeText(this, "Lưu phiên đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean checkLocalLoginSession(LoginAPI api, SharedPreferences session) {
        try {
            String username = session.getString("login_user", "");
            String usertoken = session.getString("login_token", "");

            if (!username.equals("")) {
                return checkOnlineLoginSession(api, username, usertoken);
            } else {
                return false;
            }
        } catch (Exception e) {
         Toast.makeText(this, "Lỗi đăng nhập", Toast.LENGTH_SHORT).show();
         return false;
        }
    }

    public boolean checkOnlineLoginSession(LoginAPI api, String username,  String usertoken) {
        return api.checkLoginToken(username, usertoken);
    }

    public boolean updateLocalLoginSession(LoginAPI api, String username, String userpassword, SharedPreferences.Editor editor) {
        try {
            editor.putString("login_token", api.setNewLoginToken(username, userpassword));
            editor.putString("login_user", username);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clearLocalLoginSession(SharedPreferences.Editor editor) {
        try {
            editor.putString("login_token", (String) null);
            editor.putString("login_user", (String) null);
            editor.apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void signOut(LoginAPI api, SharedPreferences session, SharedPreferences.Editor editor) {
        if (checkLocalLoginSession(api, session)) {
            String l_sun = session.getString("login_user", (String) null);
            String l_stoken = session.getString("login_token", (String) null);
            api.clearLoginToken(l_sun, l_stoken);
            if (clearLocalLoginSession(editor)) {
                Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}