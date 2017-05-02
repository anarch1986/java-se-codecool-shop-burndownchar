package com.codecool.shop.dao;

import com.codecool.shop.model.Order;

import java.util.List;

/**
 * Created by dynuchu on 2017.05.02..
 */
public interface Orderable {
    void add(Order order);
    Order find(int id);
    void remove(int id);

    List<Order> getAll();
}