package com.example.myapplication.DAO;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.myapplication.Entity.FacturaApendice;

import java.util.List;
@Dao
public interface ApendiceDao {
    @Insert
    void insertarApendices(List<FacturaApendice> apendices);
}
