package com.example.myapplication.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.modelos.Cliente;

import java.util.ArrayList;
import java.util.List;

public class buscarClienteAdapter extends RecyclerView.Adapter<buscarClienteAdapter.ClienteViewHolder> implements Filterable {

    private List<Cliente> clienteList;
    private List<Cliente> clienteListFull;
    private OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente);
    }

    public buscarClienteAdapter(List<Cliente> clienteList, OnClienteClickListener listener) {
        this.clienteList = clienteList;
        this.clienteListFull = new ArrayList<>(clienteList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buscar_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = clienteList.get(position);
        String nombre = cliente.getNombre() != null ? cliente.getNombre() : "Sin nombre";
        holder.tvNombre.setText(nombre);
        holder.itemView.setOnClickListener(v -> listener.onClienteClick(cliente));
    }

    @Override
    public int getItemCount() {
        return clienteList.size();
    }

    public int getFilteredItemCount() {
        return clienteList != null ? clienteList.size() : 0;
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCliente);
        }
    }

    @Override
    public Filter getFilter() {
        return filtroCliente != null ? filtroCliente : new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {}
        };
    }

    private final Filter filtroCliente = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Cliente> filtrados = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtrados.addAll(clienteListFull);
            } else {
                String filtro = constraint.toString().toLowerCase().trim();
                for (Cliente c : clienteListFull) {
                    if (c.getNombre() != null && c.getNombre().toLowerCase().contains(filtro)) {
                        filtrados.add(c);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtrados;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (clienteList != null) {
                clienteList.clear();
                if (results.values instanceof List<?>) {
                    for (Object obj : (List<?>) results.values) {
                        if (obj instanceof Cliente) {
                            clienteList.add((Cliente) obj);
                        }
                    }
                }
            }

            notifyDataSetChanged();
        }

    };

}
