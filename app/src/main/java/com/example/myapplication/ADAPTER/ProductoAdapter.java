package com.example.myapplication.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.modelos.Producto;

import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos;
    private OnProductoClickListener listener;

    public ProductoAdapter(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_inicial, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        double total = producto.getPrecio() + producto.getPropina();

        holder.tvDescripcionProducto_inicial.setText(producto.getDescripcion());
        holder.tvPrecioProducto_inicial.setText(String.format(Locale.US, "$%.2f", total));


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductoClick(producto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcionProducto_inicial, tvPrecioProducto_inicial;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcionProducto_inicial = itemView.findViewById(R.id.tvDescripcionProducto_inicial);
            tvPrecioProducto_inicial=itemView.findViewById(R.id.tvPrecioProducto_inicial);

        }
    }

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    public void setOnProductoClickListener(OnProductoClickListener listener) {
        this.listener = listener;
    }
}
