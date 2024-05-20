package dev.sudhanshu.finapp.screen;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.makeassure.R;
import com.app.makeassure.databinding.HomeLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import dev.sudhanshu.finapp.UserDetailAdapter;
import dev.sudhanshu.finapp.models.User;
import dev.sudhanshu.finapp.models.UserDetail;
import dev.sudhanshu.finapp.util.ConnectivityReceiver;


public class Home extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Integer backPressCount = 0;
    HomeLayoutBinding binding;
    private Random random = new Random();

    List<UserDetail> userList = new ArrayList<>();
    UserDetailAdapter userDetailAdapter;

    boolean isSortCTA_Visible = false;
    Integer appliedSorting = -1;

    private ConnectivityReceiver connectivityReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializing and register the connectivity receiver
        connectivityReceiver = new ConnectivityReceiver(this);


        // handling view click
        listener();

    }

    private void fetchData() {
        setDummyDataToFirebase(() -> {
            // Fetching data from firebase
            getAllUserDataFromFirebase();
        });
    }

    private void listener() {
        binding.sortMenu.setOnClickListener(view -> {
            FilterChange();
        });

        binding.SortByName.setOnClickListener(view -> {
            if (appliedSorting == 0){
                appliedSorting = -1;
                setUserDataRecycler(userList);
            }else {
                appliedSorting = 0;
                setUserDataRecycler(sortByName(userList));
            }
            setSortBtn(0);
        });

        binding.SortByAge.setOnClickListener(view -> {
            if (appliedSorting == 1){
                appliedSorting = -1;
                setUserDataRecycler(userList);
            }else {
                appliedSorting = 1;
                setUserDataRecycler(sortByAge(userList));
            }
            setSortBtn(1);
        });

        binding.SortByCity.setOnClickListener(view -> {
            if (appliedSorting == 2){
                setUserDataRecycler(userList);
                appliedSorting = -1;
            }else {
                appliedSorting = 2;
                setUserDataRecycler(sortByCity(userList));
            }
            setSortBtn(2);
        });

        binding.cancelSearch.setOnClickListener(view -> {
            binding.cancelSearch.setVisibility(View.GONE);
            setUserDataRecycler(userList);
            binding.searchBox.setText("");
            binding.searchBox.clearFocus();
        });

        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchKey = editable.toString();
                clearFilter();
                if (searchKey.isEmpty()){
                    binding.cancelSearch.setVisibility(View.GONE);
                    setUserDataRecycler(userList);
                }else {
                    binding.cancelSearch.setVisibility(View.VISIBLE);
                    List<UserDetail> filterList = new ArrayList<>();
                    for (UserDetail user : userList) {
                        if (user.getName().toLowerCase().contains(searchKey) ||
                                user.getCity().toLowerCase().contains(searchKey)) {
                            filterList.add(user);
                        }
                    }
                    userDetailAdapter.filter(filterList);
                }
            }
        });
    }

    private void clearFilter() {
        appliedSorting = -1;
        setSortBtn(-1);
    }

    private void FilterChange() {
        if (isSortCTA_Visible){
            binding.sortIcon.setImageResource(R.drawable.sort);
            binding.sortingCTA.setVisibility(View.GONE);
            setUserDataRecycler(userList);
            appliedSorting = -1;
            setSortBtn(-1);
            isSortCTA_Visible = false;
        }else {
            binding.sortIcon.setImageResource(R.drawable.cancel);
            binding.sortingCTA.setVisibility(View.VISIBLE);
            isSortCTA_Visible = true;
        }
    }

    private void setSortBtn(int i) {
        for (int j  = 0 ; j < binding.sortingOption.getChildCount(); j++){
            View child = binding.sortingOption.getChildAt(j);
            if (child instanceof TextView) {
                if (appliedSorting == j ){
                    child.setBackgroundResource(R.drawable.user_card_bg);
                }else {
                    child.setBackgroundResource(R.drawable.sort_cta_bg);
                }
            }
        }
    }


    private void getAllUserDataFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserDetail user = userSnapshot.getValue(UserDetail.class);
                    userList.add(user);
                }
                setUserDataRecycler(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving data: " + databaseError.getMessage());
            }
        });
    }

    private void setUserDataRecycler(List<UserDetail> userDetailList) {
        binding.loader.setVisibility(View.GONE);
        binding.userDataRecycler.setVisibility(View.VISIBLE);
        userDetailAdapter = new UserDetailAdapter(userDetailList);
        binding.userDataRecycler.setAdapter(userDetailAdapter);
    }


    private void setDummyDataToFirebase(final OnDummyDataSavedListener onDummyDataSavedListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    List<UserDetail> userList = new ArrayList<>();
                    for (int i = 0; i < 20; i++) {
                        userList.add(new UserDetail(generateRandomName(), generateRandomAge(), generateRandomCity()));
                    }
                    usersRef.setValue(userList);
                }
                onDummyDataSavedListener.onDummyDataSaved();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Home.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Error while checking existing data: " + databaseError.getMessage());
            }
        });
    }


    public static List<UserDetail> sortByName(List<UserDetail> userList) {
        List<UserDetail> sortedList = new ArrayList<>(userList);
        Collections.sort(sortedList, new Comparator<UserDetail>() {
            @Override
            public int compare(UserDetail u1, UserDetail u2) {
                return u1.getName().compareToIgnoreCase(u2.getName());
            }
        });
        return sortedList;
    }

    public static List<UserDetail> sortByAge(List<UserDetail> userList) {
        List<UserDetail> sortedList = new ArrayList<>(userList);
        Collections.sort(sortedList, new Comparator<UserDetail>() {
            @Override
            public int compare(UserDetail u1, UserDetail u2) {
                return Integer.compare(u1.getAge(), u2.getAge());
            }
        });
        return sortedList;
    }

    public static List<UserDetail> sortByCity(List<UserDetail> userList) {
        List<UserDetail> sortedList = new ArrayList<>(userList);
        Collections.sort(sortedList, new Comparator<UserDetail>() {
            @Override
            public int compare(UserDetail u1, UserDetail u2) {
                return u1.getCity().compareToIgnoreCase(u2.getCity());
            }
        });
        return sortedList;
    }


    private String generateRandomName() {
        String[] names = {"John", "Alice", "Michael", "Emma", "James", "Sophia", "William", "Olivia", "David", "Ava"};
        return names[random.nextInt(names.length)];
    }

    private int generateRandomAge() {
        return random.nextInt(43) + 18;
    }


    private String generateRandomCity() {
        String[] cities = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"};
        return cities[random.nextInt(cities.length)];
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            fetchData();
        } else {
            Toast.makeText(this, "You are offline!", Toast.LENGTH_SHORT).show();
        }
    }


    interface OnDummyDataSavedListener {
        void onDummyDataSaved();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onBackPressed() {
        if (backPressCount <= 0){
            backPressCount++;
            Toast.makeText(this, "Press again to exit the app", Toast.LENGTH_SHORT).show();
        }else {
            super.onBackPressed();
        }
    }
}