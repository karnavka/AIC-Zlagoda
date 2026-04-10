package com.zlagoda.model;
import java.time.LocalDateTime;

public class Check {
    private String check_number;
    private String id_employee;
    private String card_number; // може бути null
    private LocalDateTime print_date;
    private double sum_total;
    private double vat;

}
