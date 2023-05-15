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
import com.example.bookapp.Activity.ProfileActivity;
import com.example.bookapp.Helper.SharedPrefManager;
import com.example.bookapp.Helper.VolleySingle;
import com.example.bookapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvname, tvEmail;
    private ImageView avatar;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        tvname.setText(SharedPrefManager.getInstance(getActivity()).getUser().getFull_name());
        tvEmail.setText(SharedPrefManager.getInstance(getActivity()).getUser().getEmail());
        Glide.with(this).load(R.drawable.defaultavt).into(avatar);
        if (SharedPrefManager.getInstance(getActivity()).getUser().getAvatar() != null) {
            Glide.with(this).load(SharedPrefManager.getInstance(getActivity()).getUser().getAvatar()).into(avatar);
        }

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