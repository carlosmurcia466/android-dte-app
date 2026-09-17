package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.Entity.ProductoEntity;

import java.util.List;

@Dao
public interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProductos(List<ProductoEntity> productos);

    @Query("SELECT * FROM productos")
    List<ProductoEntity> getTodosLosProductos();

    @Query("DELETE FROM productos")
    void eliminarTodos();
}