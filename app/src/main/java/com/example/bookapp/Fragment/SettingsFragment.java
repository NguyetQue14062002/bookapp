package com.example.bookapp.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.bookapp.Activity.LoginActivity;
import com.example.bookapp.Activity.PersonalPostActivity;
import com.example.bookapp.Activity.ProfileActivity;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private TextView tvname, tvEmail, tvPersonalPost;
    private ImageView avatar;

    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=  inflater.inflate(R.layout.fragment_settings, container, false);
        TextView tvseem = (TextView) rootView.findViewById(R.id.tvNameSetting);
        Button btnlogout = (Button) rootView.findViewById(R.id.btnLogout);

        tvseem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAttractProfile();
            }
        });
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAttractLogOut();
            }
        });
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvname = view.findViewById(R.id.tvNameSetting);
        avatar = view.findViewById(R.id.imageUserSetting);
        tvEmail= view.findViewById(R.id.tvEmailSetting);
        tvPersonalPost = view.findViewById(R.id.tvPersonalPost);
        tvname.setText(SharedPrefManager.getInstance(getActivity()).getUser().getFull_name());
        tvEmail.setText(SharedPrefManager.getInstance(getActivity()).getUser().getEmail());
        tvPersonalPost = view.findViewById(R.id.tvPersonalPost);
        Glide.with(this).load(R.drawable.defaultavt).into(avatar);
        if (SharedPrefManager.getInstance(getActivity()).getUser().getAvatar() != null) {
            Glide.with(this).load(SharedPrefManager.getInstance(getActivity()).getUser().getAvatar()).into(avatar);
        }

        tvPersonalPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PersonalPostActivity.class);
                startActivity(intent);
            }
        });

    }
    public void goToAttractProfile()
    {

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);

    }
    public void goToAttractLogin()
    {

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

    }
    public void goToAttractLogOut()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/api/account/logout",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (obj.getInt("err") == 0) {
                                goToAttractLogin();
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.e(TAG, "onErrorResponse: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String access_token = SharedPrefManager.getInstance(getActivity()).getUser().getAccess_token();
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",  access_token);
                return headers;
            }

            @Override
            protected Map<String, String> getParams()
            {
                String refresh_token=SharedPrefManager.getInstance(getActivity()).getUser().getRefresh_token();
                Map<String, String>  params = new HashMap<String, String>();
                params.put("refresh_token",refresh_token);

                return params;
            }

        };

        VolleySingle.getInstance(getContext()).addToRequestQueue(postRequest);

}

}