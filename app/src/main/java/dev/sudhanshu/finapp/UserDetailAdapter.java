package dev.sudhanshu.finapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.makeassure.databinding.UserCardBinding;

import java.util.List;
import dev.sudhanshu.finapp.models.UserDetail;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.UserDetailViewHolder> {

    private List<UserDetail> userDetailList;

    public UserDetailAdapter(List<UserDetail> userDetailList) {
        this.userDetailList = userDetailList;
    }

    @NonNull
    @Override
    public UserDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        UserCardBinding itemBinding = UserCardBinding.inflate(layoutInflater, parent, false);
        return new UserDetailViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailViewHolder holder, int position) {
        UserDetail userDetail = userDetailList.get(position);
        holder.bind(userDetail);
    }

    @Override
    public int getItemCount() {
        return userDetailList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(List<UserDetail> filterList) {
        userDetailList = filterList;
        notifyDataSetChanged();
    }


    static class UserDetailViewHolder extends RecyclerView.ViewHolder {

        private UserCardBinding binding;

        public UserDetailViewHolder(UserCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(UserDetail userDetail) {
            binding.userName.setText("Name : " + userDetail.getName());
            binding.userAge.setText("Age : " + userDetail.getAge());
            binding.userCity.setText("City : " + userDetail.getCity());
        }
    }
}

