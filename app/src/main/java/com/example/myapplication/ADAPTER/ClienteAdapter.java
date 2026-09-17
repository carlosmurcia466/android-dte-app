package com.example.myapplication.ADAPTER;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Entity.ClienteEntity;
import com.example.myapplication.R;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<ClienteEntity> clientes;

    public ClienteAdapter(List<ClienteEntity> clientes) {
        this.clientes = clientes;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        ClienteEntity cliente = clientes.get(position);
        holder.txtNombre.setText(cliente.getNombre());
        holder.txtDocumento.setText(cliente.getTipoDocumento() + ": " + cliente.getNumDocumento());
        holder.txtTelefono.setText("Tel: " + cliente.getTelefono()+"\n"+ cliente.getTipo_cliente());
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDocumento, txtTelefono;

        public ClienteViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDocumento = itemView.findViewById(R.id.txtDocumento);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
        }
    }
}
