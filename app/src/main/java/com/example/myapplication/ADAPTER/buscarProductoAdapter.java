package com.example.myapplication.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.myapplication.R;
import com.example.myapplication.modelos.Producto;

import java.util.ArrayList;
import java.util.List;

public class buscarProductoAdapter extends RecyclerView.Adapter<buscarProductoAdapter.ViewHolder> implements Filterable {

    private List<Producto> listaOriginal;
    private List<Producto> listaFiltrada;

    private OnProductoClickListener productoClickListener;

    public buscarProductoAdapter(List<Producto> productos) {
        this.listaOriginal = productos;
        this.listaFiltrada = new ArrayList<>(productos);
    }



    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    public void setOnProductoClickListener(OnProductoClickListener listener) {
        this.productoClickListener = listener;
    }


    @NonNull
    @Override
    public buscarProductoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_seleccionable, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull buscarProductoAdapter.ViewHolder holder, int position) {
        Producto producto = listaFiltrada.get(position);
        holder.tvDescripcion.setText(producto.getDescripcion());
      //  holder.tvPrecio.setText(String.format("Precio: $%.2f", producto.getPrecio()));
        //  holder.tvPrecio.setText(String.format("Precio: $%.2f | Propina: $%.2f", producto.getPrecio(), producto.getPropina()));


        holder.itemView.setOnClickListener(v -> {
            if (productoClickListener != null) {
                productoClickListener.onProductoClick(producto);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Producto> filtrados = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtrados.addAll(listaOriginal);
                } else {
                    String filtro = constraint.toString().toLowerCase().trim();
                    for (Producto p : listaOriginal) {
                        if (p.getDescripcion().toLowerCase().contains(filtro) ||
                                p.getCodigoProducto().toLowerCase().contains(filtro)) {
                            filtrados.add(p);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtrados;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listaFiltrada.clear();
                listaFiltrada.addAll((List<Producto>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public List<Producto> getProductosSeleccionados() {
        List<Producto> seleccionados = new ArrayList<>();
        for (Producto p : listaOriginal) {
            if (p.getCantidad() > 0) {
                seleccionados.add(p);
            }
        }
        return seleccionados;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProducto);

        }
    }


}
