package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myapplication.Entity.ClienteEntity;

import java.util.List;
@Dao
public interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertClientes(List<ClienteEntity> clientes);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCliente(ClienteEntity cliente);

    @Query("SELECT * FROM clientes")
    List<ClienteEntity> getTodosLosClientes();

    @Query("DELETE FROM clientes where tipo_cliente='Descargado'")
    void eliminarTodos();

    @Query("SELECT MAX(idCliente) FROM clientes")
    int getMaxClienteId();
}
