package dev.sudhanshu.fininfocom.screen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dev.sudhanshu.fininfocom.databinding.HomeLayoutBinding;

public class Home extends AppCompatActivity {


    HomeLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}