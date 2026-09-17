package com.example.myapplication.ROOM;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.DAO.ApendiceDao;
import com.example.myapplication.DAO.ClienteDao;
import com.example.myapplication.DAO.DetalleFacturaDao;
import com.example.myapplication.DAO.FacturaDao;
import com.example.myapplication.DAO.ProductoDao;
import com.example.myapplication.Entity.ClienteEntity;
import com.example.myapplication.Entity.DetalleFacturaEntity;
import com.example.myapplication.Entity.FacturaApendice;
import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.Entity.ProductoEntity;

@Database(entities = {ProductoEntity.class, ClienteEntity.class, FacturaEntity.class, DetalleFacturaEntity.class, FacturaApendice.class}, version = 19)
public abstract class productosdb extends RoomDatabase {

    private static productosdb instancia;

    public abstract ProductoDao productoDao();
    public abstract ClienteDao clienteDao();
    public abstract FacturaDao facturaDao();
    public abstract ApendiceDao apendiceDao();
    public abstract DetalleFacturaDao detalleFacturaDao();

    public static synchronized productosdb getInstancia(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(context.getApplicationContext(),
                            productosdb.class, "productos_db")
                    .fallbackToDestructiveMigration() //  borrar datos antiguos si hay cambios
                    .allowMainThreadQueries()
                    .build();
        }
        return instancia;
    }
}
