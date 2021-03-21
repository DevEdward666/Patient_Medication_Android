package com.example.newpc.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.newpc.model.LoginUserModel;
import com.example.newpc.qrcode.Login_Activity;
import com.example.newpc.qrcode.R;
import com.example.newpc.storage.SharedPrefManager;

public class HomeFragment extends Fragment {
    private TextView txt_empname;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt_empname=view.findViewById(R.id.txt_empname);
        LoginUserModel loginUserModel= SharedPrefManager.getInstance(getActivity()).getUser();
//        txt_empname.setText("Hello"+ loginUserModel.getUsername());
//        if(loginUserModel.getUsername()==null){
//           Intent intent=new Intent(getActivity(), Login_Activity.class);
//           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//           startActivity(intent);
//       }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(!SharedPrefManager.getInstance(getActivity()).isLoggedIn()){
            Intent intent=new Intent(getActivity(),Login_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }
}
