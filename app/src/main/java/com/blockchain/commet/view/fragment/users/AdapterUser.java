package com.blockchain.commet.view.fragment.users;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blockchain.commet.databinding.UserRowBinding;

import com.solana.models.buffer.UserModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyViewHolder> {
    public List<UserModel> list;
    public OnReply onReply;

    public AdapterUser(OnReply onReply) {
        list = new ArrayList<>();
        this.onReply = onReply;
    }


    public void add(UserModel model) {
        list.add(model);
        notifyDataSetChanged();
    }

    public interface OnReply {
        void onClick(UserModel id) throws Exception;
    }

    public void addAll(List<UserModel> models) {
        list.clear();
        list.addAll(models);
        notifyDataSetChanged();
    }

    public ArrayList getMembers(){
        return (ArrayList) list;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private UserRowBinding binding;

        private MyViewHolder(UserRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void Bind(UserModel model) {
            binding.setModel(model);
            binding.card.setOnClickListener(view -> {
                try {
                    onReply.onClick(model);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            binding.executePendingBindings();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        UserRowBinding itemBinding = UserRowBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
