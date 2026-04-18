package com.zlagoda.service;

import com.zlagoda.dto.CheckDetailsDTO;
import java.util.List;

public class CheckService {

    public double getTotalProductsSum(List<CheckDetailsDTO> products) {
        double total = 0;
        for (CheckDetailsDTO p : products) {
            total += p.getSelling_price() * p.getProduct_number();
        }
        return total;
    }

    public double getDiscount(double totalSum, int percent) {
        return totalSum * percent/100;
    }

    public double getVat(double finalSum) {
        return finalSum * 0.2;
    }

}