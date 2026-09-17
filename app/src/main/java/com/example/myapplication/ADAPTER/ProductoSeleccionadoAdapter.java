package com.example.myapplication.ADAPTER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.modelos.Producto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductoSeleccionadoAdapter extends RecyclerView.Adapter<ProductoSeleccionadoAdapter.ViewHolder> {

    public interface OnProductoModificadoListener {
        void onProductoActualizado();
        void onProductoEliminado(Producto producto);
    }

    private List<Producto> productosSeleccionados;
    private OnProductoModificadoListener listener;

    public ProductoSeleccionadoAdapter(List<Producto> productosSeleccionados, OnProductoModificadoListener listener) {
        this.productosSeleccionados = productosSeleccionados;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcionProducto, tvPrecioProducto, tvCantidad;
        ImageButton btnAumentar, btnDisminuir, btnEliminar;



        public ViewHolder(View itemView) {
            super(itemView);
            tvDescripcionProducto = itemView.findViewById(R.id.tvDescripcionProducto);
            tvPrecioProducto = itemView.findViewById(R.id.tvPrecioProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            btnAumentar = itemView.findViewById(R.id.btnAumentar);
            btnDisminuir = itemView.findViewById(R.id.btnDisminuir);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    @Override
    public ProductoSeleccionadoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto_seleccionado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductoSeleccionadoAdapter.ViewHolder holder, int position) {
        Producto producto = productosSeleccionados.get(position);
        holder.tvDescripcionProducto.setText(producto.getDescripcion());
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
        holder.tvPrecioProducto.setText("Precio: " + formatoMoneda.format(producto.getPrecio()+producto.getPropina()));
        holder.tvCantidad.setText(String.valueOf(producto.getCantidad()));


        holder.btnAumentar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Producto productoActual = productosSeleccionados.get(pos);
                productoActual.setCantidad(productoActual.getCantidad() + 1);
                notifyItemChanged(pos);
                if (listener != null) listener.onProductoActualizado();
            }
        });

        holder.btnDisminuir.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Producto productoActual = productosSeleccionados.get(pos);
                if (productoActual.getCantidad() > 1) {
                    productoActual.setCantidad(productoActual.getCantidad() - 1);
                    notifyItemChanged(pos);
                    if (listener != null) listener.onProductoActualizado();
                } else {
                    eliminarProducto(pos);
                }
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {

            eliminarProducto(pos);
            }
        });






    }

    @Override
    public int getItemCount() {
        return productosSeleccionados.size();
    }

    public List<Producto> getProductosSeleccionados() {
        List<Producto> seleccionados = new ArrayList<>();
        for (Producto p : productosSeleccionados) {
            if (p.getCantidad() > 0) {
                seleccionados.add(p);
            }
        }
        return seleccionados;
    }

    public void setProductosSeleccionados(List<Producto> nuevaLista) {
        this.productosSeleccionados = nuevaLista;
        notifyDataSetChanged();
    }


    private void eliminarProducto(int position) {
        if (position >= 0 && position < productosSeleccionados.size()) {
            Producto productoEliminado = productosSeleccionados.get(position);
            productosSeleccionados.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productosSeleccionados.size());
            if (listener != null) listener.onProductoEliminado(productoEliminado);
        }
    }



}
