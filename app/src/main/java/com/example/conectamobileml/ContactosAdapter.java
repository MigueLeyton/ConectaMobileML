package com.example.conectamobileml;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactViewHolder> {

    private List<User> userList;
    private OnContactClickListener listener;

    public ContactosAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void setOnContactClickListener(OnContactClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getNombre());
        holder.tvLastName.setText(user.getApellido());
        holder.tvEmail.setText(user.getEmail());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactClick(user); // Llama al método en el listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastName, tvEmail;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvLastName = itemView.findViewById(R.id.tv_lastname);
            tvEmail = itemView.findViewById(R.id.tv_email);
        }
    }

    public interface OnContactClickListener {
        void onContactClick(User user);
    }
}
